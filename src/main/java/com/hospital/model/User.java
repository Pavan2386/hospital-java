package com.hospital.model;
public class User {
    private int id;
    private String name, email, password, phone, role, gender, dateOfBirth, address, createdAt;

    public int    getId()          { return id; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getPassword()    { return password; }
    public String getPhone()       { return phone; }
    public String getRole()        { return role; }
    public String getGender()      { return gender; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getAddress()     { return address; }
    public String getCreatedAt()   { return createdAt; }

    public void setId(int id)                 { this.id = id; }
    public void setName(String name)          { this.name = name; }
    public void setEmail(String email)        { this.email = email; }
    public void setPassword(String password)  { this.password = password; }
    public void setPhone(String phone)        { this.phone = phone; }
    public void setRole(String role)          { this.role = role; }
    public void setGender(String gender)      { this.gender = gender; }
    public void setDateOfBirth(String d)      { this.dateOfBirth = d; }
    public void setAddress(String address)    { this.address = address; }
    public void setCreatedAt(String c)        { this.createdAt = c; }
}
