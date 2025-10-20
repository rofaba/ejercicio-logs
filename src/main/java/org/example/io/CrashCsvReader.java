package org.example.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.model.CrashLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CrashCsvReader {
    public List<CrashLog> read(String path) throws Exception {
        List<CrashLog> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .withDelimiter(';')
                     .withFirstRecordAsHeader()
                     .withTrim()
                     .withQuote('"')
                     .withIgnoreEmptyLines()
                     .parse(br)) {
            for (CSVRecord r : parser) {
                CrashLog c = new CrashLog();
                c.timestamp    = r.get("timestamp");
                c.appName      = r.get("appName");
                c.errorCode    = r.get("errorCode");
                c.errorMessage = r.get("errorMessage");
                c.stackTrace   = r.get("stackTrace");
                c.deviceModel  = r.get("deviceModel");
                c.osVersion    = r.get("osVersion");
                out.add(c);
            }
        }
        return out;
    }
}