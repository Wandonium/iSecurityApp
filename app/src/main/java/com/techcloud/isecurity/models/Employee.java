package com.techcloud.isecurity.models;

import java.util.Objects;

public class Employee {

    private int employeeId;
    private String name;
    private long phone_no;
    private String role;
    private String time_in;
    private String time_out;
    private String password;
    private int company_id;
    private int guard_id;
    private int building_id;
    private int employee_db_id;

    public Employee(int employeeId, String name, long phone_no, String role, String time_in, String time_out, int company_id, int guard_id, int building_id) {
        this.employeeId = employeeId;
        this.name = name;
        this.phone_no = phone_no;
        this.role = role;
        this.time_in = time_in;
        this.time_out = time_out;
        this.company_id = company_id;
        this.guard_id = guard_id;
        this.building_id = building_id;
    }

    public Employee() {
    }

    public Employee(Employee other) {
        this.employeeId = other.employeeId;
        this.name = other.name;
        this.phone_no = other.phone_no;
        this.role = other.role;
        this.time_in = other.time_in;
        this.time_out = other.time_out;
        this.company_id = other.company_id;
        this.guard_id = other.guard_id;
        this.building_id = other.building_id;
        this.employee_db_id = other.employee_db_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public int getGuard_id() {
        return guard_id;
    }

    public void setGuard_id(int guard_id) {
        this.guard_id = guard_id;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    public int getEmployee_db_id() {
        return employee_db_id;
    }

    public void setEmployee_db_id(int employee_db_id) {
        this.employee_db_id = employee_db_id;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", phone_no=" + phone_no +
                ", role='" + role + '\'' +
                ", time_in='" + time_in + '\'' +
                ", time_out='" + time_out + '\'' +
                ", password='" + password + '\'' +
                ", company_id=" + company_id +
                ", guard_id=" + guard_id +
                ", building_id=" + building_id +
                ", employee_db_id=" + employee_db_id +
                '}';
    }
}
