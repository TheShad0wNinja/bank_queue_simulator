package com.bank.controllers;

import com.bank.models.SimulationHistoryRecord;
import com.bank.ui.components.ProbabilitiesTable;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.SimulationStatisticsTable;
import com.bank.ui.pages.HistoryDetailPage;
import com.bank.utils.TextUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.bank.utils.SimulationVisualization.*;

public class HistoryDetailPageController {
    private final SimulationHistoryRecord record;
    private final HistoryDetailPage view;

    public HistoryDetailPageController(HistoryDetailPage view, SimulationHistoryRecord record) {
        this.view = view;
        this.record = record;

        loadParams();
    }

    private void loadParams() {
        String dateStr = record.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        view.setSubtitleText("Run Date: " + dateStr);

        SimulationHistoryRecord.SimulationConfigSnapshot config = record.getConfigSnapshot();
        SimulationHistoryRecord.SimulationParams params = record.getSimulationParams();

        String[][] configs = {
                {"Outdoor Queue Size", String.valueOf(config.getOutdoorQueueCapacity())},
                {"Probability of Cash Customer", String.valueOf(config.getCashCustomerProbability())},
                {"Number of Outdoor Tellers", String.valueOf(countEmployees(config.getEmployees(), "OUTDOOR", "CASH"))},
                {"Number of Indoor Tellers", String.valueOf(countEmployees(config.getEmployees(), "INDOOR", "CASH"))},
                {"Number of Indoor Service Employees", String.valueOf(countEmployees(config.getEmployees(), "INDOOR", "SERVICE"))},
                {"Simulation Days", String.valueOf(params.simulationDays())},
                {"Customers per Day", String.valueOf(params.simulationCustomers())},
                {"Simulation Runs", String.valueOf(params.simulationRuns())}
        };
        view.setGeneralConfigPanelCells(configs);

        ProbabilitiesTable timeBetweenArrivalsTable = new ProbabilitiesTable(config.getTimeBetweenArrivalProbabilities());
        timeBetweenArrivalsTable.setEnabled(false);
        view.addDistributionTable("Time Between Arrivals", timeBetweenArrivalsTable);

        Map<String, List<SimulationHistoryRecord.EmployeeConfigSnapshot>> grouped = new LinkedHashMap<>();
        for (SimulationHistoryRecord.EmployeeConfigSnapshot emp : config.getEmployees()) {
            String key = emp.getArea() + "_" + emp.getType();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(emp);
        }

        for (var entry : grouped.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                SimulationHistoryRecord.EmployeeConfigSnapshot emp = entry.getValue().get(i);
                String employeeType = switch(emp.getType()) {
                    case "CASH" -> "teller";
                    case "SERVICE" -> "service";
                    default -> "employee";
                };
                String label = TextUtils.capitalize(emp.getArea().toLowerCase()) + " " + TextUtils.capitalize(employeeType) + " " + (i + 1);

                ProbabilitiesTable table = new ProbabilitiesTable(emp.getServiceTimeProbabilities());
                table.setEnabled(false);
                view.addDistributionTable(label, table);
            }
        }


        SimulationEventsTable eventsTable = new SimulationEventsTable();
        for (SimulationHistoryRecord.EventRow event : record.getEvents()) {
            eventsTable.addEventRow(
                    event.time(),
                    event.type(),
                    event.customer(),
                    event.service(),
                    event.employee(),
                    event.queues(),
                    event.action()
            );
        }
        view.addDataTable("First Day's Simulation Events", eventsTable, 400);

        var firstDayStats = record.getFirstDayStats();
        var totalStats = record.getTotalStats();

        SimulationStatisticsTable firstDayStatsTable = new SimulationStatisticsTable();
        firstDayStatsTable.setStatistics(new ArrayList<>(firstDayStats));
        view.addDataTable("First Day Statistics", firstDayStatsTable, 300);

        SimulationStatisticsTable totalStatsTable = new SimulationStatisticsTable();
        totalStatsTable.setStatistics(new ArrayList<>(totalStats));
        view.addDataTable("Total Statistics", totalStatsTable, 300);

        view.addChart("Average Service Times", createAvgServiceTimeChart(totalStats));
        view.addChart("Average Wait Times", createAvgWaitTimesChart(totalStats));
        view.addChart("Maximum Queue Sizes", createMaxQueueSizeChart(totalStats));
        view.addChart("Wait Probability Distribution", createWaitProbabilityPieChart(totalStats));
        view.addChart("Idle vs Busy Portion", createIdlePortionChart(totalStats));
    }

    private int countEmployees(List<SimulationHistoryRecord.EmployeeConfigSnapshot> employees, String area, String type) {
        return (int) employees.stream()
                .filter(e -> e.getArea().equals(area) && e.getType().equals(type))
                .count();
    }

}
