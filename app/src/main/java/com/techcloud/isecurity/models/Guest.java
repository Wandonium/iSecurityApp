package com.techcloud.isecurity.models;

import java.io.Serializable;

public class Guest implements Serializable {

    private int guestId;
    private String full_names;
    private long phone_no;
    private String gender;
    private String reason_for_visit;
    private String time_in;
    private String time_out;
    private int building_id;
    private int company_id;
    private int guard_id;
    private int guest_db_id;

    public Guest(int guestId, String full_names, long phone_no, String gender, String reason_for_visit, String time_in, String time_out, int building_id, int company_id, int guard_id) {
        this.guestId = guestId;
        this.full_names = full_names;
        this.phone_no = phone_no;
        this.gender = gender;
        this.reason_for_visit = reason_for_visit;
        this.time_in = time_in;
        this.time_out = time_out;
        this.building_id = building_id;
        this.company_id = company_id;
        this.guard_id = guard_id;
    }

    public Guest() {
    }

    public Guest(Guest other) {
        this.guestId = other.guestId;
        this.full_names = other.full_names;
        this.phone_no = other.phone_no;
        this.gender = other.gender;
        this.reason_for_visit = other.reason_for_visit;
        this.time_in = other.time_in;
        this.time_out = other.time_out;
        this.building_id = other.building_id;
        this.company_id = other.company_id;
        this.guard_id = other.guard_id;
        this.guest_db_id = other.guest_db_id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getFull_names() {
        return full_names;
    }

    public void setFull_names(String full_names) {
        this.full_names = full_names;
    }

    public long getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(long phone_no) {
        this.phone_no = phone_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReason_for_visit() {
        return reason_for_visit;
    }

    public void setReason_for_visit(String reason_for_visit) {
        this.reason_for_visit = reason_for_visit;
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

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
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

    public int getGuest_db_id() {
        return guest_db_id;
    }

    public void setGuest_db_id(int guest_db_id) {
        this.guest_db_id = guest_db_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return getGuestId() == guest.getGuestId() &&
                getPhone_no() == guest.getPhone_no() &&
                getBuilding_id() == guest.getBuilding_id() &&
                getCompany_id() == guest.getCompany_id() &&
                getGuard_id() == guest.getGuard_id() &&
                getGuest_db_id() == guest.getGuest_db_id() &&
                getFull_names().equals(guest.getFull_names()) &&
                getGender().equals(guest.getGender()) &&
                getReason_for_visit().equals(guest.getReason_for_visit()) &&
                getTime_in().equals(guest.getTime_in()) &&
                getTime_out().equals(guest.getTime_out());
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId=" + guestId +
                ", full_names='" + full_names + '\'' +
                ", phone_no=" + phone_no +
                ", gender='" + gender + '\'' +
                ", reason_for_visit='" + reason_for_visit + '\'' +
                ", time_in=" + time_in +
                ", time_out=" + time_out +
                ", building_id=" + building_id +
                ", company_id=" + company_id +
                ", guard_id=" + guard_id +
                ", guest_db_id=" + guest_db_id +
                '}';
    }
}
