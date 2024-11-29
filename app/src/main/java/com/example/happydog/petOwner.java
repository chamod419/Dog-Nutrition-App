package com.example.happydog;

public class petOwner {
    private String email;
    private String name;
    private String address;
    private String paymentMethod;
    private int age;

    public petOwner() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public petOwner(String email, String name, String address, String paymentMethod , int age) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.age = age;

    }

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for address
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter and Setter for paymentMethod
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
