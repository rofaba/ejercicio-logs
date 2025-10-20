package org.example.io;
/*
import org.apache.commons.csv.*;
import org.example.model.EventLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Lee events.csv con ; y comillas dobles como en HU-02.
public class EventCsvReader {
    public List<EventLog> read(String path) throws Exception {
        List<EventLog> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .withDelimiter(';')
                     .withFirstRecordAsHeader()
                     .withTrim()
                     .withQuote('"')
                     .withIgnoreEmptyLines()
                     .parse(br)) {
            for (CSVRecord r : parser) {
                EventLog e = new EventLog();
                e.timestamp = r.get("timestamp");
                e.appName   = r.get("appName");
                e.eventType = r.get("eventType");
                e.userId    = r.get("userId");
                e.sessionId = r.get("sessionId");
                e.eventData = r.get("eventData");
                out.add(e);
            }
        }
        return out;
    }
}
*/
import org.apache.commons.csv.*;
import org.example.model.EventLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Intenta parseo estricto. Si falla por comillas rotas en eventData, usa modo tolerante. */
public class EventCsvReader {

    public List<EventLog> read(String path) throws Exception {
        try {
            return readStrict(path);
        } catch (UncheckedIOException | IOException ex) {
            System.err.println("Aviso: CSV de eventos con comillas rotas. Aplicando lector tolerante. " + ex.getMessage());
            return readLenient(path);
        }
    }

    /** Modo estricto con Commons CSV: ; como delimitador, cabecera y comillas dobles. */
    private List<EventLog> readStrict(String path) throws IOException {
        List<EventLog> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .withDelimiter(';')
                     .withFirstRecordAsHeader()
                     .withTrim()
                     .withIgnoreSurroundingSpaces()
                     .withQuote('"')
                     .withIgnoreEmptyLines()
                     .parse(br)) {
            for (CSVRecord r : parser) {
                EventLog e = new EventLog();
                e.timestamp = r.get("timestamp");
                e.appName   = r.get("appName");
                e.eventType = r.get("eventType");
                e.userId    = r.get("userId");
                e.sessionId = r.get("sessionId");
                e.eventData = r.get("eventData");
                out.add(e);
            }
        }
        return out;
    }

    /**
     * Modo tolerante:
     * - Lee línea a línea.
     * - Split por ';' con límite 6 (timestamp, appName, eventType, userId, sessionId, eventData).
     * - Sanea comillas de eventData:
     *   * recorta espacios
     *   * quita una comilla sobrante al final
     *   * si empieza y termina con comillas, quítalas
     *   * colapsa comillas dobles "" -> "
     */
    private List<EventLog> readLenient(String path) throws IOException {
        List<EventLog> out = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        if (lines.isEmpty()) return out;

        // Saltar cabecera
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank()) continue;

            String[] parts = line.split(";", 6);
            if (parts.length < 6) {
                System.err.println("Línea ignorada por columnas insuficientes: " + (i + 1));
                continue;
            }
            String eventData = sanitize(parts[5]);

            EventLog e = new EventLog();
            e.timestamp = parts[0];
            e.appName   = parts[1];
            e.eventType = parts[2];
            e.userId    = parts[3];
            e.sessionId = parts[4];
            e.eventData = eventData;
            out.add(e);
        }
        return out;
    }

    private String sanitize(String s) {
        if (s == null) return null;
        String t = s.trim();

        // Quitar una comilla sobrante al final: ..."}"
        if (t.endsWith("\"") && !t.startsWith("\"")) {
            t = t.substring(0, t.length() - 1).trim();
        }
        // Si está rodeado por comillas, quitar ambas
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1);
        }
        // Reemplazar comillas dobles escapadas por una
        t = t.replace("\"\"", "\"");
        return t;
    }
}
