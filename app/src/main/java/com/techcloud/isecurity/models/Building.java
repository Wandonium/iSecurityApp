package com.techcloud.isecurity.models;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Objects;

public class Building implements Comparable {

    private String name;
    private String street;
    private String city;
    private int no_of_floors;
    private float longitude;
    private float latitude;
    private int building_id;

    public Building(String name, String street, String city, int no_of_floors, float longitude, float latitude) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.no_of_floors = no_of_floors;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Building() {
        this.name = null;
        this.street = null;
        this.city = null;
        this.no_of_floors = 0;
        this.longitude = 0;
        this.latitude = 0;
    }

    public Building(Building other) {
        this.name = other.name;
        this.street = other.street;
        this.city = other.city;
        this.no_of_floors = other.no_of_floors;
        this.longitude = other.longitude;
        this.latitude = other.latitude;
        this.building_id = other.building_id;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getNo_of_floors() {
        return no_of_floors;
    }

    public void setNo_of_floors(int no_of_floors) {
        this.no_of_floors = no_of_floors;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return getNo_of_floors() == building.getNo_of_floors() &&
                Float.compare(building.getLongitude(), getLongitude()) == 0 &&
                Float.compare(building.getLatitude(), getLatitude()) == 0 &&
                getName().equals(building.getName()) &&
                getStreet().equals(building.getStreet()) &&
                getCity().equals(building.getCity());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getStreet(), getCity(), getNo_of_floors(), getLongitude(), getLatitude());
    }

    @Override
    public String toString() {
        return "Building{" +
                "name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", no_of_floors=" + no_of_floors +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Building other = (Building) o;
        int buildingID = other.getBuilding_id();
        return this.getBuilding_id() - buildingID;
    }
}
