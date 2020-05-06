package e.yunus.kasapp.helper;

import java.time.Year;
import java.util.Calendar;

public class CurrentDate {

    // fungsi public static biar semua bisa diakses diaktivity
    public static Calendar calendar = Calendar.getInstance();  // memanggil fungsi kalender
    public static int year          = calendar.get(Calendar.YEAR);
    public static int month         = calendar.get(Calendar.MONTH);
    public static int  day          = calendar.get(Calendar.DAY_OF_MONTH);



}
