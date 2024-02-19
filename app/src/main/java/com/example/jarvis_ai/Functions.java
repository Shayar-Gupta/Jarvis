package com.example.jarvis_ai;
import java.util.Calendar;

public class Functions {
    static String wishMe() {
        String s = " ";
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);
        if(time >= 6 && time < 12){
            s = ".A very happy Good Morning to you Sir";
        }
        else if(time >= 12 && time < 16){
            s = " Good Afternoon Sir. Sun shines, you are mine Sir";
        } else if (time >= 16 && time < 22) {
            s = "Happy Good Evening Sir";
        }
        return s;
    }
}
