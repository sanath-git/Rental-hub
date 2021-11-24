package com.example.tourguide2.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GetDateFromString {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate getDateFromString(String string, DateTimeFormatter format)
    {

        // Convert the String to Date in the specified format
        LocalDate date = LocalDate.parse(string, format);

        // return the converted date
        return date;
    }
}
