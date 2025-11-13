package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private int simulationDays = 10;
    private int simulationCustomers = 10;
    private int simulationRetries = 10;

    private final Random rand = new Random();
    private final SimulationConfigs configs = SimulationConfigs.instance;
    private final SimulationStatistics stats = new SimulationStatistics();
    private final int outdoorQueueCapacity = configs.getOutdoorQueueCapacity();

    private final PriorityQueue<Event> events = new PriorityQueue<>();

    private final Queue<Customer> outdoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> indoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> serviceEmployeeQueue = new LinkedList<>();

    private List<Employee> outdoorTellersStatus;
    private List<Employee> indoorTellersStatus;
    private List<Employee> serviceEmployeesStatus;

    private final EventPrinter eventPrinter;

    private int currentTime = 0;

    public Simulator(EventPrinter eventPrinter) {
        this.eventPrinter = eventPrinter;
    }

    public void startSimulation(Runnable onSimulationEnd) {
        List<EmployeeData> outdoorTellers = configs.getOutdoorCashEmployees();
        List<EmployeeData> indoorTellers = configs.getIndoorCashEmployees();
        List<EmployeeData> serviceEmp = configs.getIndoorServiceEmployees();

        outdoorTellersStatus = outdoorTellers.stream().map(e -> new Employee(e, outdoorTellerQueue)).toList();
        indoorTellersStatus = indoorTellers.stream().map(e -> new Employee(e, indoorTellerQueue)).toList();
        serviceEmployeesStatus = serviceEmp.stream().map(e -> new Employee(e, serviceEmployeeQueue)).toList();

        currentTime = 0;
        for (int i = 0; i < simulationCustomers; i++) {
            int timeBetweenCustomer = configs.getTimeBetweenArrival(rand.nextDouble());
            int arrivalTime = currentTime + timeBetweenCustomer;

            ServiceType serviceType = rand.nextDouble() <= configs.getCashCustomerProbability()
                    ? ServiceType.CASH : ServiceType.SERVICE;

            Customer customer = new Customer(serviceType, arrivalTime, i);

            events.add(new Event(Event.Type.ARRIVAL, arrivalTime, customer));

            currentTime = arrivalTime;
        }

        currentTime = 0;
        while (!events.isEmpty()) {
            Event event = events.poll();
            currentTime = event.getTime();

            if (event.getType() == Event.Type.ARRIVAL) {
                handleArrival(event);
            } else {
                handleDeparture(event);
            }
        }

        onSimulationEnd.run();
    }

    private void handleArrival(Event event) {
        Customer c = event.getCustomer();
        printEvent("ARRIVAL", c, null, "Customer arrived");

        if (c.serviceType() == ServiceType.CASH)
            routeToOutdoorTeller(event);
        else
            routeToServiceEmployee(event);
    }

    private void routeToOutdoorTeller(Event event) {
        Customer c = event.getCustomer();
        if (outdoorTellerQueue.size() > outdoorQueueCapacity) {
            printEvent("ROUTE", c, null, "Outdoor queue full → reroute indoor");
            routeToIndoorTeller(event);
        } else {
            Employee availableEmployee = outdoorTellersStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
            if (availableEmployee != null) {
                serveCustomer(c, availableEmployee);
            } else {
                outdoorTellerQueue.offer(c);
                printEvent("QUEUE", c, null, "Joined outdoor queue");
            }
        }
    }

    private void routeToIndoorTeller(Event event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = indoorTellersStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            indoorTellerQueue.offer(c);
            printEvent("QUEUE", c, null, "Joined indoor teller queue");
        }
    }

    private void routeToServiceEmployee(Event event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = serviceEmployeesStatus.stream().filter(e -> e.isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            serviceEmployeeQueue.offer(c);
            printEvent("QUEUE", c, null, "Joined service employee queue");
        }
    }

    private void serveCustomer(Customer customer, Employee employee) {
        customer.setServiceTimeStart(currentTime);
        employee.setBusy(currentTime);

        EmployeeData employeeData = employee.employeeData;
        int serviceTime = employeeData.getServiceTime(rand.nextDouble());
        int departureTime = currentTime + serviceTime;

        printEvent("SERVE", customer, employee,
                String.format("Service started (serviceTime=%d, depart=%d)", serviceTime, departureTime));

        events.add(new Event(Event.Type.DEPARTURE, departureTime, customer, employee));
    }

    private void handleDeparture(Event event) {
        currentTime = event.getTime();
        Employee employee = event.getEmployeeStatus();
        Customer customer = event.getCustomer();

        employee.setIdle(currentTime);
        printEvent("DEPART", customer, employee, "Service completed");

        Queue<Customer> assignedQueue = employee.assignedQueue;
        if (!assignedQueue.isEmpty()) {
            Customer next = assignedQueue.poll();
            printEvent("NEXT", next, employee, "Next customer begins service");
            serveCustomer(next, employee);
        }
    }

    private void printEvent(String type, Customer c, Employee e, String action) {
        eventPrinter.printEvent(type, c, e, action, indoorTellerQueue.size(), outdoorTellerQueue.size(), serviceEmployeeQueue.size(), currentTime);
    }

    public int getSimulationRetries() {
        return simulationRetries;
    }

    public void setSimulationRetries(int simulationRetries) {
        this.simulationRetries = simulationRetries;
    }

    public int getSimulationCustomers() {
        return simulationCustomers;
    }

    public void setSimulationCustomers(int simulationCustomers) {
        this.simulationCustomers = simulationCustomers;
    }

    public int getSimulationDays() {
        return simulationDays;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }
}
