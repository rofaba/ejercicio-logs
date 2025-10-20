package org.example.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.file.Files;
import java.nio.file.Path;

// JSON pretty con Jackson ObjectMapper, como pide HU-03.
public class JsonWriter {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void write(Object data, String outputPath) throws Exception {
        Path p = Path.of(outputPath);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        mapper.writeValue(p.toFile(), data);
    }
}