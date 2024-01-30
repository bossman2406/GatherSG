package com.gathersg.user.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dateCompare {

    public static Date parseDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
            return null;
        }
    }
}
