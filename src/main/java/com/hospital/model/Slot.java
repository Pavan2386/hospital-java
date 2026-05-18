package com.hospital.model;

public class Slot {
    private int id, doctorId;
    private String date, time;
    private boolean isBooked;

    public int     getId()       { return id; }
    public int     getDoctorId() { return doctorId; }
    public String  getDate()     { return date; }
    public String  getTime()     { return time; }
    public boolean isBooked()    { return isBooked; }

    public void setId(int id)             { this.id = id; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setDate(String date)      { this.date = date; }
    public void setTime(String time)      { this.time = time; }
    public void setBooked(boolean booked) { this.isBooked = booked; }
}
