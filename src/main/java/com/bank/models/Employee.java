package com.bank.models;

import java.util.Queue;

public class Employee {
    public boolean isIdle = true;
    public int lastEventTime = 0;
    public int totalIdle = 0;
    public final EmployeeData employeeData;
    public final Queue<Customer> assignedQueue;

    public Employee(EmployeeData employeeData, Queue<Customer> assignedQueue) {
        this.employeeData = employeeData;
        this.assignedQueue = assignedQueue;
    }

    public void setBusy(int time) {
        isIdle = false;
        totalIdle += time - lastEventTime;
    }

    public void setIdle(int time) {
        isIdle = true;
        lastEventTime = time;
    }

    @Override
    public String toString() {
        return "EmployeeStatus{" +
                "totalIdle=" + totalIdle +
                ", lastEvent=" + lastEventTime +
                ", idle=" + isIdle +
                '}';
    }
}
