package ru.calculator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarLauncher {

    private static final Logger log = LoggerFactory.getLogger(JarLauncher.class);

    private final String jarPath;
    private final int startMemoryMb;
    private final int stepMemoryMb;
    private final int runs;

    public JarLauncher(String jarPath, int startMemoryMb, int stepMemoryMb, int runs) {
        this.jarPath = jarPath;
        this.startMemoryMb = startMemoryMb;
        this.stepMemoryMb = stepMemoryMb;
        this.runs = runs;
    }

    public void launch() throws IOException, InterruptedException {
        List<Result> results = new ArrayList<>();

        File logsDir = new File("./logs");
        if (!logsDir.exists()) {
            if (logsDir.mkdirs()) {
                log.info("Created logs directory at {}", logsDir.getAbsolutePath());
            } else {
                log.warn("Failed to create logs directory at {}", logsDir.getAbsolutePath());
            }
        }

        for (int i = 0; i < runs; i++) {
            int memoryMb = startMemoryMb + i * stepMemoryMb;
            List<String> command = new ArrayList<>();
            command.add("java");
            command.add("-Xms" + memoryMb + "m");
            command.add("-Xmx" + memoryMb + "m");
            command.add("-XX:+HeapDumpOnOutOfMemoryError");
            command.add("-XX:HeapDumpPath=./logs/heapdump.hprof");
            command.add("-XX:+UseG1GC");
            command.add("-Xlog:gc=debug:file=./logs/gc-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m");
            command.add("-jar");
            command.add(jarPath);

            log.info("Running {} with memory: {} MB", jarPath, memoryMb);

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File("."));
            builder.redirectErrorStream(true);

            long startTime = System.nanoTime();
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            }

            int exitCode = process.waitFor();
            long endTime = System.nanoTime();

            long durationMs = (endTime - startTime) / 1_000_000;
            log.info("Execution finished with code {} in {} ms", exitCode, durationMs);

            results.add(new Result(memoryMb, durationMs));
        }

        log.info("\nSummary Results for {}:", jarPath);
        String header = String.format("%10s | %10s", "Memory(MB)", "Time(ms)");
        log.info(header);
        log.info("{}", "-".repeat(header.length()));
        for (Result result : results) {
            log.info(String.format("%10d | %10d", result.memoryMb, result.durationMs));
        }
    }

    private static class Result {
        int memoryMb;
        long durationMs;

        Result(int memoryMb, long durationMs) {
            this.memoryMb = memoryMb;
            this.durationMs = durationMs;
        }
    }

    public static void main(String[] args) {
        try {
            int startMemoryMb = 256;
            int stepMemoryMb = 256;
            int runs = 15;

            String[] jarPaths = {"C:\\Otus\\CalcDemo.jar", "C:\\Otus\\CalcDemoOptimised.jar"};

            for (String jarPath : jarPaths) {
                JarLauncher launcher = new JarLauncher(jarPath, startMemoryMb, stepMemoryMb, runs);
                launcher.launch();
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while launching jar", e);
        }
    }
}
