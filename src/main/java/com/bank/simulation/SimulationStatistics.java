package com.bank.simulation;

import java.util.ArrayList;

public class SimulationStatistics {
    public record Statistic(String label, Number value) {}

    private int totalCashServiceTime = 0;
    private int totalServiceServiceTime = 0;

    private int totalIndoorTellerWaitTime = 0;
    private int totalOutdoorTellerWaitTime = 0;
    private int totalServiceEmployeeWaitTime = 0;

    private int maxIndoorTellerQueueSize = 0;
    private int maxOutdoorTellerQueueSize = 0;
    private int maxServiceEmployeeQueueSize = 0;

    private int totalIndoorTellerWaitingCustomers = 0;
    private int totalOutdoorTellerWaitingCustomers = 0;
    private int totalServiceEmployeeWaitingCustomers = 0;

    private int totalIndoorTellerCustomers = 0;
    private int totalOutdoorTellerCustomers = 0;
    private int totalServiceEmployeeCustomers = 0;

    private int totalTime = 0;
    private int totalIndoorTellerIdleTime = 0;
    private int totalOutdoorTellerIdleTime = 0;
    private int totalServiceEmployeeIdleTime = 0;

    private ArrayList<Statistic> statistics;

    public void addTotalCashServiceTime(int time) {
        totalCashServiceTime += time;
    }

    public void addTotalServiceServiceTime(int time) {
        totalServiceServiceTime += time;
    }

    public void addTotalIndoorTellerWaitTime(int time) {
        totalIndoorTellerWaitTime += time;
    }

    public void addTotalOutdoorTellerWaitTime(int time) {
        totalOutdoorTellerWaitTime += time;
    }

    public void addTotalServiceEmployeeWaitTime(int time) {
        totalServiceEmployeeWaitTime += time;
    }

    public void updateIndoorTellerMaxQueueSize(int size) {
        maxIndoorTellerQueueSize = Math.max(maxIndoorTellerQueueSize, size);
    }

    public void updateOutdoorTellerMaxQueueSize(int size) {
        maxOutdoorTellerQueueSize = Math.max(maxOutdoorTellerQueueSize, size);
    }

    public void updateServiceEmployeeMaxQueueSize(int size) {
        maxServiceEmployeeQueueSize = Math.max(maxServiceEmployeeQueueSize, size);
    }

    public void addIndoorTellerCustomer() {
        totalIndoorTellerCustomers += 1;
    }

    public void addOutdoorTellerCustomer() {
        totalOutdoorTellerCustomers += 1;
    }

    public void addServiceEmployeeCustomer() {
        totalServiceEmployeeCustomers += 1;
    }

    public void addIndoorTellerWaitingCustomer() {
        totalIndoorTellerWaitingCustomers += 1;
    }

    public void addOutdoorTellerWaitingCustomer() {
        totalOutdoorTellerWaitingCustomers += 1;
    }

    public void addServiceEmployeeWaitingCustomer() {
        totalServiceEmployeeWaitingCustomers += 1;
    }

    public void setTotalIndoorTellerIdleTime(int time) {
        totalIndoorTellerIdleTime = time;
    }

    public void setTotalServiceEmployeeIdleTime(int time) {
        this.totalServiceEmployeeIdleTime = time;
    }

    public void setTotalOutdoorTellerIdleTime(int time) {
        this.totalOutdoorTellerIdleTime = time;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void calculateStatistics() {
        statistics = new ArrayList<>();

        // Stat 1
        int totalCashCustomers = totalIndoorTellerCustomers + totalOutdoorTellerCustomers;
        double avgCashCustomerServiceTime = totalCashCustomers == 0 ? 0 : totalCashServiceTime / (double) totalCashCustomers;
        double avgServiceCustomerServiceTime = totalServiceEmployeeCustomers == 0 ? 0 : totalServiceServiceTime / (double) totalServiceEmployeeCustomers;

        statistics.add(new Statistic("Average Cash Customer Service Time", avgCashCustomerServiceTime));
        statistics.add(new Statistic("Average Service Customer Service Time", avgServiceCustomerServiceTime));

        // Stat 2
        double avgIndoorTellerWait = totalIndoorTellerWaitingCustomers == 0 ? 0 :
                totalIndoorTellerWaitTime / (double) totalIndoorTellerWaitingCustomers;
        double avgOutdoorTellerWait = totalOutdoorTellerWaitingCustomers == 0 ? 0 :
                totalOutdoorTellerWaitTime / (double) totalOutdoorTellerWaitingCustomers;
        double avgServiceEmployeeWait = totalServiceEmployeeWaitingCustomers == 0 ? 0 :
                totalServiceEmployeeWaitTime / (double) totalServiceEmployeeWaitingCustomers;

        double totalWaitingCustomers = totalIndoorTellerWaitingCustomers + totalOutdoorTellerWaitingCustomers + totalServiceEmployeeWaitingCustomers;
        double totalWaitTime = totalIndoorTellerWaitTime + totalOutdoorTellerWaitTime + totalServiceEmployeeWaitTime;
        double avgTotalWaitTime = totalWaitingCustomers == 0 ? 0 : totalWaitTime / totalWaitingCustomers;

        statistics.add(new Statistic("Average Indoor Teller Wait Time", avgIndoorTellerWait));
        statistics.add(new Statistic("Average Outdoor Teller Wait Time", avgOutdoorTellerWait));
        statistics.add(new Statistic("Average Service Employee Wait Time", avgServiceEmployeeWait));
        statistics.add(new Statistic("Average Total Wait Time", avgTotalWaitTime));

        // Stat 3
        statistics.add(new Statistic("Max Indoor Teller Queue Size", maxIndoorTellerQueueSize));
        statistics.add(new Statistic("Max Outdoor Teller Queue Size", maxOutdoorTellerQueueSize));
        statistics.add(new Statistic("Max Service Employee Queue Size", maxServiceEmployeeQueueSize));

        // Stat 4
        double probIndoorWait = totalIndoorTellerCustomers == 0 ? 0 :
                totalIndoorTellerWaitingCustomers / (double) totalIndoorTellerCustomers;

        double probOutdoorWait = totalOutdoorTellerCustomers == 0 ? 0 :
                totalOutdoorTellerWaitingCustomers / (double) totalOutdoorTellerCustomers;

        double probServiceWait = totalServiceEmployeeCustomers == 0 ? 0 :
                totalServiceEmployeeWaitingCustomers / (double) totalServiceEmployeeCustomers;

        statistics.add(new Statistic("Indoor Teller Wait Probability", probIndoorWait));
        statistics.add(new Statistic("Outdoor Teller Wait Probability", probOutdoorWait));
        statistics.add(new Statistic("Service Employee Wait Probability", probServiceWait));

        // State 5
        double indoorIdlePortion = totalTime == 0 ? 0 : totalIndoorTellerIdleTime / (double) totalTime;
        double outdoorIdlePortion = totalTime == 0 ? 0 : totalOutdoorTellerIdleTime / (double) totalTime;
        double serviceIdlePortion = totalTime == 0 ? 0 : totalServiceEmployeeIdleTime / (double) totalTime;

        statistics.add(new Statistic("Indoor Tellers Idle Portion", indoorIdlePortion));
        statistics.add(new Statistic("Outdoor Tellers Idle Portion", outdoorIdlePortion));
        statistics.add(new Statistic("Service Employees Idle Portion", serviceIdlePortion));
    }

    public void merge(SimulationStatistics stats) {
        this.totalCashServiceTime += stats.totalCashServiceTime;
        this.totalServiceServiceTime += stats.totalServiceServiceTime;

        this.totalIndoorTellerWaitTime += stats.totalIndoorTellerWaitTime;
        this.totalOutdoorTellerWaitTime += stats.totalOutdoorTellerWaitTime;
        this.totalServiceEmployeeWaitTime += stats.totalServiceEmployeeWaitTime;

        this.maxIndoorTellerQueueSize = Math.max(this.maxIndoorTellerQueueSize, stats.maxIndoorTellerQueueSize);
        this.maxOutdoorTellerQueueSize = Math.max(this.maxOutdoorTellerQueueSize, stats.maxOutdoorTellerQueueSize);
        this.maxServiceEmployeeQueueSize = Math.max(this.maxServiceEmployeeQueueSize, stats.maxServiceEmployeeQueueSize);

        this.totalIndoorTellerWaitingCustomers += stats.totalIndoorTellerWaitingCustomers;
        this.totalOutdoorTellerWaitingCustomers += stats.totalOutdoorTellerWaitingCustomers;
        this.totalServiceEmployeeWaitingCustomers += stats.totalServiceEmployeeWaitingCustomers;

        this.totalIndoorTellerCustomers += stats.totalIndoorTellerCustomers;
        this.totalOutdoorTellerCustomers += stats.totalOutdoorTellerCustomers;
        this.totalServiceEmployeeCustomers += stats.totalServiceEmployeeCustomers;

        this.totalTime += stats.totalTime;
        this.totalIndoorTellerIdleTime += stats.totalIndoorTellerIdleTime;
        this.totalOutdoorTellerIdleTime += stats.totalOutdoorTellerIdleTime;
        this.totalServiceEmployeeIdleTime += stats.totalServiceEmployeeIdleTime;
    }

    public ArrayList<Statistic> getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String format = "| %-40s | %-10s |%n";

        sb.append("+------------------------------------------+------------+\n");
        sb.append(String.format(format, "Metric", "Value"));
        sb.append("+------------------------------------------+------------+\n");
        sb.append(String.format(format, "totalCashServiceTime", totalCashServiceTime));
        sb.append(String.format(format, "totalServiceServiceTime", totalServiceServiceTime));
        sb.append(String.format(format, "totalIndoorTellerWaitTime", totalIndoorTellerWaitTime));
        sb.append(String.format(format, "totalOutdoorTellerWaitTime", totalOutdoorTellerWaitTime));
        sb.append(String.format(format, "totalServiceEmployeeWaitTime", totalServiceEmployeeWaitTime));
        sb.append(String.format(format, "maxIndoorTellerQueueSize", maxIndoorTellerQueueSize));
        sb.append(String.format(format, "maxOutdoorTellerQueueSize", maxOutdoorTellerQueueSize));
        sb.append(String.format(format, "maxServiceEmployeeQueueSize", maxServiceEmployeeQueueSize));
        sb.append(String.format(format, "totalIndoorTellerWaitingCustomers", totalIndoorTellerWaitingCustomers));
        sb.append(String.format(format, "totalOutdoorTellerWaitingCustomers", totalOutdoorTellerWaitingCustomers));
        sb.append(String.format(format, "totalServiceEmployeeWaitingCustomers", totalServiceEmployeeWaitingCustomers));
        sb.append(String.format(format, "totalIndoorTellerCustomers", totalIndoorTellerCustomers));
        sb.append(String.format(format, "totalOutdoorTellerCustomers", totalOutdoorTellerCustomers));
        sb.append(String.format(format, "totalServiceEmployeeCustomers", totalServiceEmployeeCustomers));
        sb.append(String.format(format, "totalTime", totalTime));
        sb.append(String.format(format, "totalIndoorTellerIdleTime", totalIndoorTellerIdleTime));
        sb.append(String.format(format, "totalOutdoorTellerIdleTime", totalOutdoorTellerIdleTime));
        sb.append(String.format(format, "totalServiceEmployeeIdleTime", totalServiceEmployeeIdleTime));
        sb.append("+------------------------------------------+------------+\n");

        return sb.toString();
    }}
