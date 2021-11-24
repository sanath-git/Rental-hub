package com.example.tourguide2.utils;

public class vehicleHistory {
    public String carName,imageUrl,pickUpDate,dropOffDate,rentAmount;
    public vehicleHistory()
    {

    }
    public vehicleHistory(String carName,String imageUrl,String pickUpDate,String dropOffDate,String rentAmount)
    {
        this.carName = carName;
        this.imageUrl = imageUrl;
        this.pickUpDate = pickUpDate;
        this.dropOffDate = dropOffDate;
        this.rentAmount = rentAmount;
    }
}
