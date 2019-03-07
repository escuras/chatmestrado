package pt.IPG.messenger.recyclerview;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private int image;
    private String description;
    private String brand;

    public Product() { }

    public Product(String name, double price, String description, String brand, int image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.image = image;
    }

    public int getImage(){
        return image;
    }

    public void setImage(@DrawableRes int img){
        image = img;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() { return brand; }

    public void setBrand(String brand) { this.brand = brand; }
}
