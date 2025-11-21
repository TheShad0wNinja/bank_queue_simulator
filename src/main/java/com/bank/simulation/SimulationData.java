package com.bank.simulation;

import java.io.Serializable;
import java.util.ArrayList;

public class SimulationData {
    public record Statistic(String label, String value) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public int totalCashServiceTime = 0;
    public int totalServiceServiceTime = 0;

    public int totalIndoorTellerWaitTime = 0;
    public int totalOutdoorTellerWaitTime = 0;
    public int totalServiceWaitTime = 0;

    public int maxIndoorTellerQueueSize = 0;
    public int maxOutdoorTellerQueueSize = 0;
    public int maxServiceEmployeeQueueSize = 0;

    public  int totalIndoorTellerWaitingCustomers = 0;
    public int totalOutdoorTellerWaitingCustomers = 0;
    public int totalServiceEmployeeWaitingCustomers = 0;

    public int totalIndoorTellerCustomers = 0;
    public int totalOutdoorTellerCustomers = 0;
    public int totalServiceEmployeeCustomers = 0;

    public int totalTime = 0;
    public int totalIndoorTellerIdleTime = 0;
    public int totalOutdoorTellerIdleTime = 0;
    public int totalServiceEmployeeIdleTime = 0;

    public int indoorTellersCount = 1;
    public int outdoorTellersCount = 1;
    public int serviceEmployeesCount = 1;

    public ArrayList<Statistic> statistics;

    public void calculateStatistics() {
        statistics = new ArrayList<>();

        // Stat 1
        int totalCashCustomers = totalIndoorTellerCustomers + totalOutdoorTellerCustomers;
        double avgCashCustomerServiceTime = totalCashCustomers == 0 ? 0 : totalCashServiceTime / (double) totalCashCustomers;
        double avgServiceCustomerServiceTime = totalServiceEmployeeCustomers == 0 ? 0 : totalServiceServiceTime / (double) totalServiceEmployeeCustomers;

        statistics.add(new Statistic("Average Cash Customer Service Time", String.format("%.4f", avgCashCustomerServiceTime)));
        statistics.add(new Statistic("Average Service Customer Service Time", String.format("%.4f", avgServiceCustomerServiceTime)));

        // Stat 2
        double avgIndoorTellerWait = totalIndoorTellerWaitingCustomers == 0 ? 0 :
                totalIndoorTellerWaitTime / (double) totalIndoorTellerWaitingCustomers;
        double avgOutdoorTellerWait = totalOutdoorTellerWaitingCustomers == 0 ? 0 :
                totalOutdoorTellerWaitTime / (double) totalOutdoorTellerWaitingCustomers;
        double avgServiceEmployeeWait = totalServiceEmployeeWaitingCustomers == 0 ? 0 :
                totalServiceWaitTime / (double) totalServiceEmployeeWaitingCustomers;

        double totalWaitingCustomers = totalIndoorTellerWaitingCustomers + totalOutdoorTellerWaitingCustomers + totalServiceEmployeeWaitingCustomers;
        double totalWaitTime = totalIndoorTellerWaitTime + totalOutdoorTellerWaitTime + totalServiceWaitTime;
        double avgTotalWaitTime = totalWaitingCustomers == 0 ? 0 : totalWaitTime / totalWaitingCustomers;

        statistics.add(new Statistic("Average Indoor Teller Wait Time", String.format("%.4f", avgIndoorTellerWait)));
        statistics.add(new Statistic("Average Outdoor Teller Wait Time", String.format("%.4f", avgOutdoorTellerWait)));
        statistics.add(new Statistic("Average Service Employee Wait Time", String.format("%.4f", avgServiceEmployeeWait)));
        statistics.add(new Statistic("Average Total Wait Time", String.format("%.4f", avgTotalWaitTime)));

        // Stat 3
        statistics.add(new Statistic("Max Indoor Teller Queue Size", String.valueOf(maxIndoorTellerQueueSize)));
        statistics.add(new Statistic("Max Outdoor Teller Queue Size", String.valueOf(maxOutdoorTellerQueueSize)));
        statistics.add(new Statistic("Max Service Employee Queue Size", String.valueOf(maxServiceEmployeeQueueSize)));

        // Stat 4
        double probIndoorWait = totalIndoorTellerCustomers == 0 ? 0 :
                totalIndoorTellerWaitingCustomers / (double) totalIndoorTellerCustomers;

        double probOutdoorWait = totalOutdoorTellerCustomers == 0 ? 0 :
                totalOutdoorTellerWaitingCustomers / (double) totalOutdoorTellerCustomers;

        double probServiceWait = totalServiceEmployeeCustomers == 0 ? 0 :
                totalServiceEmployeeWaitingCustomers / (double) totalServiceEmployeeCustomers;

        statistics.add(new Statistic("Indoor Teller Wait Probability", String.format("%.2f%%", probIndoorWait * 100)));
        statistics.add(new Statistic("Outdoor Teller Wait Probability", String.format("%.2f%%", probOutdoorWait * 100)));
        statistics.add(new Statistic("Service Employee Wait Probability", String.format("%.2f%%", probServiceWait * 100)));

        // State 5
        double indoorIdlePortion = totalTime == 0 ? 0 : totalIndoorTellerIdleTime / (double) (totalTime * indoorTellersCount);
        double outdoorIdlePortion = totalTime == 0 ? 0 : totalOutdoorTellerIdleTime / (double) (totalTime * outdoorTellersCount);
        double serviceIdlePortion = totalTime == 0 ? 0 : totalServiceEmployeeIdleTime / (double) (totalTime * serviceEmployeesCount) ;

        statistics.add(new Statistic("Indoor Tellers Idle Portion", String.format("%.2f%%", indoorIdlePortion * 100)));
        statistics.add(new Statistic("Outdoor Tellers Idle Portion", String.format("%.2f%%", outdoorIdlePortion * 100)));
        statistics.add(new Statistic("Service Employees Idle Portion", String.format("%.2f%%", serviceIdlePortion * 100)));
    }

    public void merge(SimulationData stats) {
        this.totalCashServiceTime += stats.totalCashServiceTime;
        this.totalServiceServiceTime += stats.totalServiceServiceTime;

        this.totalIndoorTellerWaitTime += stats.totalIndoorTellerWaitTime;
        this.totalOutdoorTellerWaitTime += stats.totalOutdoorTellerWaitTime;
        this.totalServiceWaitTime += stats.totalServiceWaitTime;

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

        this.indoorTellersCount = stats.indoorTellersCount;
        this.outdoorTellersCount = stats.outdoorTellersCount;
        this.serviceEmployeesCount = stats.serviceEmployeesCount;
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
        sb.append(String.format(format, "totalServiceEmployeeWaitTime", totalServiceWaitTime));
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
    }
}
