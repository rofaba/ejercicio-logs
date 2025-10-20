package org.example.service;




import org.example.model.AppMetrics;
import org.example.model.CrashLog;
import org.example.model.EventLog;

import java.util.*;
import java.util.stream.Collectors;

// Une por appName y calcula totales y tasa de error.
public class ConsolidationService {

    public List<AppMetrics> consolidate(List<CrashLog> crashes, List<EventLog> events) {
        Map<String, Long> crashCount = crashes.stream()
                .collect(Collectors.groupingBy(c -> c.appName, Collectors.counting()));

        Map<String, Long> eventCount = events.stream()
                .collect(Collectors.groupingBy(e -> e.appName, Collectors.counting()));

        Set<String> apps = new HashSet<>();
        apps.addAll(crashCount.keySet());
        apps.addAll(eventCount.keySet());

        List<AppMetrics> out = new ArrayList<>();
        for (String app : apps) {
            long c = crashCount.getOrDefault(app, 0L);
            long e = eventCount.getOrDefault(app, 0L);
            out.add(new AppMetrics(app, c, e));
        }

        // Orden opcional: por tasa de error descendente
        out.sort(Comparator.comparing((AppMetrics m) -> m.errorRatePct).reversed());
        return out;
    }
}