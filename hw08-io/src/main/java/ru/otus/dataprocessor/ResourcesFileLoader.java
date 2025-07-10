package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.model.Measurement;

@RequiredArgsConstructor
public class ResourcesFileLoader implements Loader {
    private final String fileName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Measurement> load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            return objectMapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
