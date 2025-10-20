package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.example.io.CrashCsvReader;
import org.example.io.EventCsvReader;
import org.example.io.JsonWriter;
import org.example.model.AppMetrics;
import org.example.model.CrashLog;
import org.example.model.EventLog;
import org.example.service.ConsolidationService;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        // Rutas fijas (simples, alineadas al temario de archivos)
        Path crashesPath = Path.of("data/crashes.csv");
        Path eventsPath  = Path.of("data/events.csv");
        Path outJsonPath = Path.of("out/analysis_report.json");

        try {
            // Crear carpeta de salida si no existe
            if (outJsonPath.getParent() != null) {
                Files.createDirectories(outJsonPath.getParent());
            }

            // Leer CSV
            List<CrashLog> crashes = new CrashCsvReader().read(crashesPath.toString());
            List<EventLog> events  = new EventCsvReader().read(eventsPath.toString());

            // Consolidar
            List<AppMetrics> metrics = new ConsolidationService().consolidate(crashes, events);

            // Escribir JSON pretty
            new JsonWriter().write(metrics, outJsonPath.toString());

            // Consola
            System.out.println("OK -> " + outJsonPath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}