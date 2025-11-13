package com.bank.models;

public class Event implements Comparable<Event> {
    @Override
    public int compareTo(Event o) {
        return this.time - o.time ;
    }

    public enum Type {
        ARRIVAL,
        DEPARTURE,
    }

    private Type type;
    private int time;
    private Customer customer;
    private Employee employee;

    public Event(Type type, int time, Customer customer) {
        this.type = type;
        this.time = time;
        this.customer = customer;
    }

    public Event(Type type, int time, Customer customer, Employee employee) {
        this(type, time, customer);
        this.employee = employee;
    }

    public Type getType() {
        return type;
    }

    public int getTime() {
        return time;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Employee getEmployeeStatus() {
        return employee;
    }

    @Override
    public String toString() {
        return "Event{" +
                type +
                "@" + time +
                ":" + customer.id() +
                "-" + customer.serviceType() +
                '}';
    }
}
