package com.hospital.model;
import java.util.List;

public class Doctor {
    private int id, experience;
    private String name, email, specialty, qualification, phone, bio;
    private double consultationFee, rating;
    private boolean isActive;
    private List<Slot> slots;

    public int     getId()              { return id; }
    public String  getName()            { return name; }
    public String  getEmail()           { return email; }
    public String  getSpecialty()       { return specialty; }
    public String  getQualification()   { return qualification; }
    public int     getExperience()      { return experience; }
    public String  getPhone()           { return phone; }
    public String  getBio()             { return bio; }
    public double  getConsultationFee() { return consultationFee; }
    public double  getRating()          { return rating; }
    public boolean isActive()           { return isActive; }
    public List<Slot> getSlots()        { return slots; }

    public void setId(int id)                      { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setEmail(String email)             { this.email = email; }
    public void setSpecialty(String specialty)     { this.specialty = specialty; }
    public void setQualification(String q)         { this.qualification = q; }
    public void setExperience(int experience)      { this.experience = experience; }
    public void setPhone(String phone)             { this.phone = phone; }
    public void setBio(String bio)                 { this.bio = bio; }
    public void setConsultationFee(double fee)     { this.consultationFee = fee; }
    public void setRating(double rating)           { this.rating = rating; }
    public void setActive(boolean active)          { this.isActive = active; }
    public void setSlots(List<Slot> slots)         { this.slots = slots; }
}
