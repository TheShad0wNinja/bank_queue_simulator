package com.bank.models;

import com.bank.simulation.SimulationData;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SimulationHistoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime timestamp;
    private final List<EventRow> events;
    private final List<SimulationData.Statistic> firstDayStats;
    private final List<SimulationData.Statistic> totalStats;
    private final SimulationConfigSnapshot configSnapshot;
    private final SimulationParams simulationParams;

    public SimulationHistoryRecord(
            LocalDateTime timestamp,
            List<EventRow> events,
            List<SimulationData.Statistic> firstDayStats,
            List<SimulationData.Statistic> totalStats,
            SimulationConfigSnapshot configSnapshot,
            SimulationParams simulationParams
    ) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
        this.events = new ArrayList<>(events);
        this.firstDayStats = new ArrayList<>(firstDayStats);
        this.totalStats = new ArrayList<>(totalStats);
        this.configSnapshot = configSnapshot;
        this.simulationParams = simulationParams;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<EventRow> getEvents() {
        return new ArrayList<>(events);
    }

    public List<SimulationData.Statistic> getFirstDayStats() {
        return new ArrayList<>(firstDayStats);
    }

    public List<SimulationData.Statistic> getTotalStats() {
        return new ArrayList<>(totalStats);
    }

    public SimulationConfigSnapshot getConfigSnapshot() {
        return configSnapshot;
    }

    public SimulationParams getSimulationParams() {
        return simulationParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationHistoryRecord that = (SimulationHistoryRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public record EventRow(
            int time,
            String type,
            String customer,
            String service,
            String employee,
            String queues,
            String action
    ) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static class SimulationConfigSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int outdoorQueueCapacity;
        private final double cashCustomerProbability;
        private final Map<Integer, Double> timeBetweenArrivalProbabilities;
        private final List<EmployeeConfigSnapshot> employees;

        public SimulationConfigSnapshot(
                int outdoorQueueCapacity,
                double cashCustomerProbability,
                Map<Integer, Double> timeBetweenArrivalProbabilities,
                List<EmployeeConfigSnapshot> employees
        ) {
            this.outdoorQueueCapacity = outdoorQueueCapacity;
            this.cashCustomerProbability = cashCustomerProbability;
            this.timeBetweenArrivalProbabilities = new LinkedHashMap<>(timeBetweenArrivalProbabilities);
            this.employees = new ArrayList<>(employees);
        }

        public int getOutdoorQueueCapacity() {
            return outdoorQueueCapacity;
        }

        public double getCashCustomerProbability() {
            return cashCustomerProbability;
        }

        public Map<Integer, Double> getTimeBetweenArrivalProbabilities() {
            return new LinkedHashMap<>(timeBetweenArrivalProbabilities);
        }

        public List<EmployeeConfigSnapshot> getEmployees() {
            return new ArrayList<>(employees);
        }
    }

    public static class EmployeeConfigSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String area;
        private final String type;
        private final String id;
        private final Map<Integer, Double> serviceTimeProbabilities;

        public EmployeeConfigSnapshot(String area, String type, String id, Map<Integer, Double> serviceTimeProbabilities) {
            this.area = area;
            this.type = type;
            this.id = id;
            this.serviceTimeProbabilities = new LinkedHashMap<>(serviceTimeProbabilities);
        }

        public String getArea() {
            return area;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Map<Integer, Double> getServiceTimeProbabilities() {
            return new LinkedHashMap<>(serviceTimeProbabilities);
        }
    }

    public record SimulationParams(int simulationDays, int simulationCustomers, int simulationRuns)
            implements Serializable {
        private static final long serialVersionUID = 1L;
    }
}

