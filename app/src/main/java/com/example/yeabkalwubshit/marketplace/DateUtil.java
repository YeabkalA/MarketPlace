package com.example.yeabkalwubshit.marketplace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import java.text.SimpleDateFormat;

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

    public void setDateFormatter(String format) {
        this.dateFormatter = new SimpleDateFormat(format);
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

    public ArrayList<String> sortStringDates(ArrayList<String> dateStrings) {
        HashMap<Date, String> dateStringMap
                = new HashMap<Date, String>();
        ArrayList<String> sortedDates = new ArrayList<>();
        ArrayList<Date> dates = new ArrayList<>();
        try {
            for(String dateString: dateStrings) {
                Date date = dateFormatter.parse(dateString);
                dates.add(date);
                dateStringMap.put(date, dateString);
            }
            Collections.sort(dates);

            for(Date date: dates) {
                sortedDates.add(dateStringMap.get(date));
            }
            return sortedDates;
        } catch (Exception e) {
            return null;
        }
    }

}
