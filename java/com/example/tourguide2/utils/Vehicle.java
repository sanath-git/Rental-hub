package com.example.tourguide2.utils;

public class Vehicle {
    public String name, imageUrl, mileage, type,extra,price,KMLimit;

    public Vehicle()
    {

    }

    public Vehicle(String name, String imageUrl, String mileage, String extra, String price,String KMLimit,String type)
    {
        this.name = name;
        this.imageUrl = imageUrl;
        this.mileage = mileage;
        this.extra = extra;
        this.price = price;
        this.KMLimit = KMLimit;
        this.type = type;

    }

}

