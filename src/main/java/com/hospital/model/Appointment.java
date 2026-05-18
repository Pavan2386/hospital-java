package com.hospital.model;

public class Appointment {
    private int id, patientId, doctorId, slotId;
    private String appointmentDate, appointmentTime, reason, status, notes, prescription, createdAt;
    private String patientName, patientEmail, patientPhone, doctorName, doctorSpecialty;
    private double consultationFee;

    public int    getId()               { return id; }
    public int    getPatientId()        { return patientId; }
    public int    getDoctorId()         { return doctorId; }
    public int    getSlotId()           { return slotId; }
    public String getAppointmentDate()  { return appointmentDate; }
    public String getAppointmentTime()  { return appointmentTime; }
    public String getReason()           { return reason; }
    public String getStatus()           { return status; }
    public String getNotes()            { return notes; }
    public String getPrescription()     { return prescription; }
    public String getCreatedAt()        { return createdAt; }
    public String getPatientName()      { return patientName; }
    public String getPatientEmail()     { return patientEmail; }
    public String getPatientPhone()     { return patientPhone; }
    public String getDoctorName()       { return doctorName; }
    public String getDoctorSpecialty()  { return doctorSpecialty; }
    public double getConsultationFee()  { return consultationFee; }

    public void setId(int id)                      { this.id = id; }
    public void setPatientId(int patientId)        { this.patientId = patientId; }
    public void setDoctorId(int doctorId)          { this.doctorId = doctorId; }
    public void setSlotId(int slotId)              { this.slotId = slotId; }
    public void setAppointmentDate(String d)       { this.appointmentDate = d; }
    public void setAppointmentTime(String t)       { this.appointmentTime = t; }
    public void setReason(String reason)           { this.reason = reason; }
    public void setStatus(String status)           { this.status = status; }
    public void setNotes(String notes)             { this.notes = notes; }
    public void setPrescription(String p)          { this.prescription = p; }
    public void setCreatedAt(String createdAt)     { this.createdAt = createdAt; }
    public void setPatientName(String n)           { this.patientName = n; }
    public void setPatientEmail(String e)          { this.patientEmail = e; }
    public void setPatientPhone(String p)          { this.patientPhone = p; }
    public void setDoctorName(String n)            { this.doctorName = n; }
    public void setDoctorSpecialty(String s)       { this.doctorSpecialty = s; }
    public void setConsultationFee(double f)       { this.consultationFee = f; }
}
