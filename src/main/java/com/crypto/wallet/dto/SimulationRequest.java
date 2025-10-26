package com.crypto.wallet.dto;

import java.time.LocalDate;
import java.util.List;

// Input for the simulation: a target date and hypothetical positions
public class SimulationRequest {
    private LocalDate date;        // e.g. 2025-07-01
    private List<SimulationItem> items;

    public SimulationRequest() {}

    public SimulationRequest(LocalDate date, List<SimulationItem> items) {
        this.date = date;
        this.items = items;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public List<SimulationItem> getItems() { return items; }
    public void setItems(List<SimulationItem> items) { this.items = items; }
}
