package com.bank.models;

import com.bank.ui.components.SimulationEventsTable;

public class EventPrinter {
    private final SimulationEventsTable eventsTable;

    public EventPrinter(SimulationEventsTable eventsTable) {
        this.eventsTable = eventsTable;
    }

    public void printEvent(
            String type,
            Customer c,
            Employee e,
            String action,
            int indoorTellerQueueSize,
            int outdoorTellerQueueSize,
            int serviceEmployeeQueueSize,
            int currentTime
    ) {
        String empName = (e == null) ? "-" : e.employeeData.toString();
        String queues = String.format("O:%d | I:%d | S:%d",
                outdoorTellerQueueSize,
                indoorTellerQueueSize,
                serviceEmployeeQueueSize
        );

        // Add row to UI table
        eventsTable.addEventRow(
                currentTime,
                type,
                "Cust#" + c.id(),
                c.serviceType().toString(),
                empName,
                queues,
                action
        );

        // Also keep console printing if you want:
        System.out.printf("%-8d | %-10s | %-12s | %-10s | %-15s | %-15s | %-15s%n",
                currentTime,
                type,
                "Cust#" + c.id(),
                c.serviceType(),
                empName,
                queues,
                action
        );
    }
}
