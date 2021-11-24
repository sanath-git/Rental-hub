package com.example.tourguide2.utils;

import java.time.LocalDate;

public class pickAndDropDate {
    public LocalDate pickupDate,dropOffDate;
    public pickAndDropDate()
    {

    }
    public pickAndDropDate(LocalDate pickupDate,LocalDate dropOffDate)
    {
        this.pickupDate = pickupDate;
        this.dropOffDate = dropOffDate;
    }
}
