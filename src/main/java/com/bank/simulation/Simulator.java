package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private int simulationDays = 10;
    private int simulationCustomersCount = 10;
    private int simulationRuns = 10;

    private final Random rand = new Random();
    private final SimulationConfigs configs = SimulationConfigs.instance;
    private final ArrayList<SimulationListener> listeners = new ArrayList<>();

    private List<EmployeeData> outdoorTellersData;
    private List<EmployeeData> indoorTellersData;
    private List<EmployeeData> serviceEmployeesData;
    private int outdoorQueueCapacity;
    private ProbabilityDistribution timeBetweenArrivalDistribution;
    private boolean shouldDispatchEvent;

    private final PriorityQueue<SimulationEvent> events = new PriorityQueue<>();

    private final Queue<Customer> outdoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> indoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> serviceEmployeeQueue = new LinkedList<>();

    private List<Employee> outdoorTellers;
    private List<Employee> indoorTellers;
    private List<Employee> serviceEmployees;

    private SimulationData firstDayStats;
    private SimulationData totalStats;


    private int currentTime = 0;
    private SimulationData currentStats;

    public Simulator() {
    }

    public void startSimulation() {
        rand.setSeed(System.currentTimeMillis());
        outdoorTellersData = configs.getOutdoorCashEmployeesData();
        indoorTellersData = configs.getIndoorCashEmployeesData();
        serviceEmployeesData = configs.getIndoorServiceEmployeesData();
        outdoorQueueCapacity = configs.getOutdoorQueueCapacity();
        timeBetweenArrivalDistribution = configs.getTimeBetweenArrivalDistribution();

        totalStats = new SimulationData();
        firstDayStats = null;

        for (int runs = 0; runs < simulationRuns; runs++) {
            for (int day = 0; day < simulationDays; day++) {
                if (runs == 0 && day == 0) {
                    shouldDispatchEvent = true;
                    runSingleSimulation();
                    shouldDispatchEvent = false;
                    firstDayStats = currentStats;
                } else {
                    runSingleSimulation();
                }
                totalStats = currentStats;
                totalStats.merge(currentStats);
            }
        }

        if (firstDayStats != null) {
            firstDayStats.calculateStatistics();
        }
        totalStats.calculateStatistics();
    }

    private void runSingleSimulation() {
        currentTime = 0;
        currentStats = new SimulationData();

        outdoorTellers = outdoorTellersData.stream().map(e -> new Employee(e, outdoorTellerQueue)).toList();
        indoorTellers = indoorTellersData.stream().map(e -> new Employee(e, indoorTellerQueue)).toList();
        serviceEmployees = serviceEmployeesData.stream().map(e -> new Employee(e, serviceEmployeeQueue)).toList();

        for (int i = 0; i < simulationCustomersCount; i++) {
            int timeBetweenCustomer = timeBetweenArrivalDistribution.getProbabilityValue(rand.nextDouble());
            int arrivalTime = currentTime + timeBetweenCustomer;

            ServiceType serviceType = rand.nextDouble() <= configs.getCashCustomerProbability()
                    ? ServiceType.CASH : ServiceType.SERVICE;

            Customer customer = new Customer(serviceType, arrivalTime, i);

            events.add(new SimulationEvent(SimulationEvent.Type.ARRIVAL, arrivalTime, customer));

            currentTime = arrivalTime;
        }

        currentTime = 0;
        while (!events.isEmpty()) {
            SimulationEvent event = events.poll();
            currentTime = event.getTime();

            if (event.getType() == SimulationEvent.Type.ARRIVAL) {
                handleArrival(event);
            } else {
                handleDeparture(event);
            }
        }

        currentStats.totalTime = currentTime;

        outdoorTellers.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.totalOutdoorTellerIdleTime =
                outdoorTellers.stream().map(Employee::getTotalIdle).reduce(0, Integer::sum);

        indoorTellers.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.totalIndoorTellerIdleTime =
                indoorTellers.stream().map(Employee::getTotalIdle).reduce(0, Integer::sum);

        serviceEmployees.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.totalServiceEmployeeIdleTime =
                serviceEmployees.stream().map(Employee::getTotalIdle).reduce(0, Integer::sum);

        currentStats.indoorTellersCount = indoorTellers.size();
        currentStats.outdoorTellersCount = outdoorTellers.size();
        currentStats.serviceEmployeesCount = serviceEmployees.size();
    }

    private void handleArrival(SimulationEvent event) {
        Customer c = event.getCustomer();
        printEvent(SimulationEventRecord.Type.ARRIVE, event, "Customer arrived");

        if (c.serviceType() == ServiceType.CASH)
            routeToOutdoorTeller(event);
        else
            routeToServiceEmployee(event);
    }

    private void routeToOutdoorTeller(SimulationEvent event) {
        Customer c = event.getCustomer();
        if (outdoorTellerQueue.size() >= outdoorQueueCapacity) {
            printEvent(SimulationEventRecord.Type.ROUTE, event, "Outdoor queue full → reroute indoor");
            routeToIndoorTeller(event);
        } else {
            Employee availableEmployee = outdoorTellers.stream().filter(Employee::isIdle).findFirst().orElse(null);
            if (availableEmployee != null) {
                serveCustomer(c, availableEmployee);
            } else {
                outdoorTellerQueue.offer(c);
                printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined outdoor queue");
                currentStats.totalOutdoorTellerWaitingCustomers++;
                currentStats.maxOutdoorTellerQueueSize = Math.max(outdoorTellerQueue.size(), currentStats.maxOutdoorTellerQueueSize);
            }

            currentStats.totalOutdoorTellerCustomers++;
        }
    }

    private void routeToIndoorTeller(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = indoorTellers.stream().filter(Employee::isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            indoorTellerQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined indoor teller queue");
            currentStats.totalIndoorTellerWaitingCustomers++;
            currentStats.maxIndoorTellerQueueSize = Math.max(indoorTellerQueue.size(), currentStats.maxIndoorTellerQueueSize);
        }

        currentStats.totalIndoorTellerCustomers++;
    }

    private void routeToServiceEmployee(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = serviceEmployees.stream().filter(Employee::isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            serviceEmployeeQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined service employee queue");
            currentStats.totalServiceEmployeeWaitingCustomers++;
            currentStats.maxServiceEmployeeQueueSize = Math.max(serviceEmployeeQueue.size(), currentStats.maxServiceEmployeeQueueSize);
        }

        currentStats.totalServiceEmployeeCustomers++;
    }

    private void serveCustomer(Customer customer, Employee employee) {
        customer.setServiceTimeStart(currentTime);
        employee.setBusy(currentTime);

        EmployeeData employeeData = employee.getEmployeeData();
        int serviceTime = employeeData.getServiceTime(rand.nextDouble());
        int departureTime = currentTime + serviceTime;

        if (employeeData.getType() == ServiceType.CASH) {
            currentStats.totalCashServiceTime += serviceTime;
            if (employeeData.getArea() == EmployeeData.Area.OUTDOOR) {
                currentStats.totalOutdoorTellerWaitTime += currentTime - customer.arrivalTime();
            } else {
                currentStats.totalIndoorTellerWaitTime += currentTime - customer.arrivalTime();
            }
        } else {
            currentStats.totalServiceServiceTime += serviceTime;
            currentStats.totalServiceWaitTime += currentTime - customer.arrivalTime();
        }

        SimulationEvent event = new SimulationEvent(SimulationEvent.Type.DEPARTURE, departureTime, customer, employee);

        printEvent(SimulationEventRecord.Type.SERVE, event, String.format("Service started (serviceTime=%d, depart=%d)", serviceTime, departureTime));

        events.add(event);
    }

    private void handleDeparture(SimulationEvent event) {
        currentTime = event.getTime();
        Employee employee = event.getEmployeeStatus();

        employee.setIdle(currentTime);
        printEvent(SimulationEventRecord.Type.DEPART, event, "Service completed");

        Queue<Customer> assignedQueue = employee.getAssignedQueue();
        if (!assignedQueue.isEmpty()) {
            Customer next = assignedQueue.poll();
            printEvent(SimulationEventRecord.Type.NEXT, event, "Next customer begins service");
            serveCustomer(next, employee);
        }
    }

    private void printEvent(SimulationEventRecord.Type type, SimulationEvent event, String description) {
        SimulationEventRecord eventRecord = new SimulationEventRecord(
                type,
                event,
                description,
                indoorTellerQueue.size(),
                outdoorTellerQueue.size(),
                serviceEmployeeQueue.size(),
                currentTime
        );
        dispatch(eventRecord);
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    private void dispatch(SimulationEventRecord event) {
        if (!shouldDispatchEvent) return;

        for (SimulationListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void setSimulationRuns(int simulationRuns) {
        this.simulationRuns = simulationRuns;
    }

    public void setSimulationCustomersCount(int simulationCustomersCount) {
        this.simulationCustomersCount = simulationCustomersCount;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }

    public SimulationData getTotalStats() {
        return totalStats;
    }

    public SimulationData getFirstDayStats() {
        return firstDayStats;
    }
}
