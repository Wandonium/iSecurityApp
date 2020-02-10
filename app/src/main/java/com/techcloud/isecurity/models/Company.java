package com.techcloud.isecurity.models;

import java.util.Objects;

public class Company {

    private String name;
    private String email;
    private String door_or_room;
    private int floor_number;
    private long phone_no;
    private int building_id;
    private int company_id;

    public Company(String name, String email, String door_or_room, int floor_number, long phone_no, int building_id) {
        this.name = name;
        this.email = email;
        this.door_or_room = door_or_room;
        this.floor_number = floor_number;
        this.phone_no = phone_no;
        this.building_id = building_id;
    }

    public Company() {
        name = null;
        email = null;
        door_or_room = null;
        floor_number = 0;
        phone_no = 0;
        building_id = 0;
    }

    public Company(Company other) {
        this.name = other.name;
        this.email = other.email;
        this.door_or_room = other.door_or_room;
        this.floor_number = other.floor_number;
        this.phone_no = other.phone_no;
        this.building_id = other.building_id;
        this.company_id = other.company_id;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoor_or_room() {
        return door_or_room;
    }

    public void setDoor_or_room(String door_or_room) {
        this.door_or_room = door_or_room;
    }

    public int getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\n' +
                ", email='" + email + '\n' +
                ", door_or_room='" + door_or_room + '\n' +
                ", floor_number=" + floor_number +
                ", phone_no=" + phone_no +
                ", building_id=" + building_id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return getFloor_number() == company.getFloor_number() &&
                getPhone_no() == company.getPhone_no() &&
                getBuilding_id() == company.getBuilding_id() &&
                getCompany_id() == company.getCompany_id() &&
                getName().equals(company.getName()) &&
                getEmail().equals(company.getEmail()) &&
                getDoor_or_room().equals(company.getDoor_or_room());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEmail(), getDoor_or_room(), getFloor_number(), getPhone_no(), getBuilding_id(), getCompany_id());
    }
}
