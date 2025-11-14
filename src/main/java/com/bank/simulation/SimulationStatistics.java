package com.bank.simulation;

public class SimulationStatistics {
    private int totalCashServiceTime = 0;
    private int totalServiceServiceTime = 0;

    private int totalIndoorTellerWaitTime = 0;
    private int totalOutdoorTellerWaitTime = 0;
    private int totalServiceEmployeeWaitTime = 0;
    public int getTotalWaitTime() {
        return totalIndoorTellerWaitTime + totalOutdoorTellerWaitTime + totalServiceEmployeeWaitTime;
    }

    private int maxIndoorTellerQueueSize = 0;
    private int maxOutdoorTellerQueueSize = 0;
    private int maxServiceEmployeeQueueSize = 0;

    private int indoorTellerWaitingCount = 0;
    private int outdoorTellerWaitingCount = 0;
    private int serviceEmployeeWaitingCount = 0;



}
