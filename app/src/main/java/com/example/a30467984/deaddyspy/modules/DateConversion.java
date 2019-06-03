package com.example.a30467984.deaddyspy.modules;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 30467984 on 3/29/2019.
 */

public class DateConversion {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    public long calculateDatesDiff(String date1 , String date2){
        Date d1 = new Date(date1);
        Date d2 = new Date(date2);
        return (d1.getTime() - d2.getTime()) / (60 * 1000);

    }
    public String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
