package com.example.yeabkalwubshit.marketplace.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import java.text.SimpleDateFormat;

/**
 * Class to handle date-related actions (including formatting for display | database storage).
 */
public class DateUtil {

    SimpleDateFormat dateFormatter;

    private Date date;
    public DateUtil() {
        dateFormatter = new SimpleDateFormat("MM:dd:yyyy");
        date = new Date();
    }

    public  String today() {
        return convertToString(date);
    }

    private static String convertToString(Date d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);

        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String date = Integer.toString(calendar.get(Calendar.DATE));
        String year = Integer.toString(calendar.get(Calendar.YEAR));

        String strDate = month + ":" + date + ":" + year;
        return strDate;
    }

}
