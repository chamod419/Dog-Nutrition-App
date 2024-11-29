package com.example.happydog;

public class SaleItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private String brand;
    private String type;
    private String ageGroup;
    private String imageUrl;

    public SaleItem() {
        // Default constructor required for Firebase
    }

    public SaleItem(String id, String name, String description, double price, String brand, String type, String ageGroup, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.type = type;
        this.ageGroup = ageGroup;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
