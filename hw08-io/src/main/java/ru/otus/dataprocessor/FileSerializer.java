package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileSerializer implements Serializer {
    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final String fileName;

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            objectMapper.writeValue(new File(fileName), new LinkedHashMap<>(data));
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
