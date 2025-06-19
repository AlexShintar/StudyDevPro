package ru.otus;

import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.homework.ProcessorSwapField11toField12;
import ru.otus.processor.homework.ProcessorThrowOnEvenSecond;

public class HomeWork {

    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {

        var processors = List.of(new ProcessorSwapField11toField12(), new ProcessorThrowOnEvenSecond(LocalTime::now));

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerPrinter = new ListenerPrinterConsole();
        var historyListener = new HistoryListener();
        complexProcessor.addListener(listenerPrinter);
        complexProcessor.addListener(historyListener);

        var message = Message.builder()
                .id(2L)
                .field11("field11")
                .field12("field12")
                .field13(new ObjectForMessage())
                .build();

        System.out.println(message);

        var result = complexProcessor.handle(message);
        logger.info("result:{}", result);

        message = Message.builder().id(3L).field13(new ObjectForMessage()).build();

        complexProcessor.handle(message);
        complexProcessor.removeListener(listenerPrinter);
        complexProcessor.removeListener(historyListener);
    }
}
