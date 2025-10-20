package org.example.model;

// model/AppMetrics.java


import java.math.BigDecimal;
import java.math.RoundingMode;

public class AppMetrics {
    public String appName;
    public long totalCrashes;
    public long totalEvents;
    public BigDecimal errorRatePct; // porcentaje con 2 decimales

    public AppMetrics() {}

    public AppMetrics(String appName, long crashes, long events) {
        this.appName = appName;
        this.totalCrashes = crashes;
        this.totalEvents = events;
        this.errorRatePct = (events == 0)
                ? BigDecimal.ZERO
                : BigDecimal.valueOf((crashes * 100.0) / events).setScale(2, RoundingMode.HALF_UP);
    }
}
