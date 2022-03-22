package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import ru.otus.model.Measurement;
import java.util.List;
import ru.otus.model.MeasurementMixin;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private final ObjectMapper objectMapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper().addMixIn(Measurement.class, MeasurementMixin.class);
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
