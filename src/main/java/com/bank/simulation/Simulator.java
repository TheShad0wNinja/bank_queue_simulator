package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private int simulationDays = 10;
    private int simulationCustomersCount = 10;
    private int simulationRetries = 10;

    private final Random rand = new Random();
    private final SimulationConfigs configs = SimulationConfigs.instance;
    private final SimulationStatistics stats = new SimulationStatistics();
    private final ArrayList<SimulationListener> listeners = new ArrayList<>();

    private final PriorityQueue<SimulationEvent> events = new PriorityQueue<>();

    private final Queue<Customer> outdoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> indoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> serviceEmployeeQueue = new LinkedList<>();

    private List<Employee> outdoorTellersStatus;
    private List<Employee> indoorTellersStatus;
    private List<Employee> serviceEmployeesStatus;

    private final int outdoorQueueCapacity = configs.getOutdoorQueueCapacity();

    private int currentTime = 0;

    public Simulator() {
    }

    public void startSimulation() {
        List<EmployeeData> outdoorTellers = configs.getOutdoorCashEmployees();
        List<EmployeeData> indoorTellers = configs.getIndoorCashEmployees();
        List<EmployeeData> serviceEmp = configs.getIndoorServiceEmployees();

        outdoorTellersStatus = outdoorTellers.stream().map(e -> new Employee(e, outdoorTellerQueue)).toList();
        indoorTellersStatus = indoorTellers.stream().map(e -> new Employee(e, indoorTellerQueue)).toList();
        serviceEmployeesStatus = serviceEmp.stream().map(e -> new Employee(e, serviceEmployeeQueue)).toList();

        currentTime = 0;
        for (int i = 0; i < simulationCustomersCount; i++) {
            int timeBetweenCustomer = configs.getTimeBetweenArrival(rand.nextDouble());
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
            Employee availableEmployee = outdoorTellersStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
            if (availableEmployee != null) {
                serveCustomer(c, availableEmployee);
            } else {
                outdoorTellerQueue.offer(c);
                printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined outdoor queue");
            }
        }
    }

    private void routeToIndoorTeller(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = indoorTellersStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            indoorTellerQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined indoor teller queue");
        }
    }

    private void routeToServiceEmployee(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = serviceEmployeesStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            serviceEmployeeQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined service employee queue");
        }
    }

    private void serveCustomer(Customer customer, Employee employee) {
        customer.setServiceTimeStart(currentTime);
        employee.setBusy(currentTime);

        EmployeeData employeeData = employee.employeeData;
        int serviceTime = employeeData.getServiceTime(rand.nextDouble());
        int departureTime = currentTime + serviceTime;

        SimulationEvent event = new SimulationEvent(SimulationEvent.Type.DEPARTURE, departureTime, customer, employee);

        printEvent(SimulationEventRecord.Type.SERVE, event, String.format("Service started (serviceTime=%d, depart=%d)", serviceTime, departureTime));

        events.add(event);
    }

    private void handleDeparture(SimulationEvent event) {
        currentTime = event.getTime();
        Employee employee = event.getEmployeeStatus();

        employee.setIdle(currentTime);
        printEvent(SimulationEventRecord.Type.DEPART, event, "Service completed");

        Queue<Customer> assignedQueue = employee.assignedQueue;
        if (!assignedQueue.isEmpty()) {
            Customer next = assignedQueue.poll();
            printEvent(SimulationEventRecord.Type.NEXT, event, "Next customer begins service");
            serveCustomer(next, employee);
        }
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    private void dispatch(SimulationEventRecord event) {
        for (SimulationListener listener : listeners) {
            listener.onEvent(event);
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

    public void setSimulationRetries(int simulationRetries) {
        this.simulationRetries = simulationRetries;
    }

    public void setSimulationCustomersCount(int simulationCustomersCount) {
        this.simulationCustomersCount = simulationCustomersCount;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }
}
