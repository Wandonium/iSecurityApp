package com.techcloud.isecurity.models;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.Objects;

public class Guard {

    private int guardId;
    private String guard_name;
    private long phone_no;
    private String password;
    private String security_company;
    private int building_id;
    private int guard_db_id;
    public static String jwtToken;

    public Guard(int guardId, String guard_name, long phone_no, String password, String security_company, int building_id) {
        this.guardId = guardId;
        this.guard_name = guard_name;
        this.phone_no = phone_no;
        this.password = password;
        this.security_company = security_company;
        this.building_id = building_id;
    }

    public Guard() {
        this.guardId = 0;
        this.guard_name = null;
        this.phone_no = 0;
        this.password = null;
        this.security_company = null;
        this.building_id = 0;
    }

    public Guard(Guard other) {
        this.guardId = other.guardId;
        this.guard_name = other.guard_name;
        this.phone_no = other.phone_no;
        this.password = other.password;
        this.security_company = other.security_company;
        this.building_id = other.building_id;
        this.guard_db_id = other.guard_db_id;
    }

    public int getGuard_db_id() {
        return guard_db_id;
    }

    public void setGuard_db_id(int guard_db_id) {
        this.guard_db_id = guard_db_id;
    }

    public int getGuardId() {
        return guardId;
    }

    public void setGuardId(int guardId) {
        this.guardId = guardId;
    }

    public String getGuard_name() {
        return guard_name;
    }

    public void setGuard_name(String guard_name) {
        this.guard_name = guard_name;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurity_company() {
        return security_company;
    }

    public void setSecurity_company(String security_company) {
        this.security_company = security_company;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    @Override
    public String toString() {
        return "Guard{" +
                "guardId=" + guardId + '\n' +
                ", guard_name='" + guard_name + '\n' +
                ", phone_no=" + phone_no + '\n' +
                ", password='" + password + '\n' +
                ", security_company='" + security_company + '\n' +
                ", building_id=" + building_id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guard guard = (Guard) o;
        return getGuardId() == guard.getGuardId() &&
                getPhone_no() == guard.getPhone_no() &&
                getBuilding_id() == guard.getBuilding_id() &&
                getGuard_name().equals(guard.getGuard_name()) &&
                getPassword().equals(guard.getPassword()) &&
                getSecurity_company().equals(guard.getSecurity_company());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(getGuardId(), getGuard_name(), getPhone_no(), getPassword(), getSecurity_company(), getBuilding_id());
    }
}
