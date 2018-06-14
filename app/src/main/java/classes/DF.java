package classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DF {
    public static Calendar Now() {
        return Calendar.getInstance();
    }
    public static Calendar Tomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return cal;
    }

    public enum TruncMode {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        NONE
    }

    public static Calendar FromLong(Long milliSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds*1000);
        return cal;
    }

    public static Long ToLong(Calendar cal) {
        return cal.getTimeInMillis()/1000;
    }

    public static Calendar Trunc(Calendar calendar) {
        return Trunc(calendar, TruncMode.DAY);
    }

    public static Calendar Trunc(Calendar calendar, TruncMode mode) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(calendar.getTimeInMillis());

        if(mode == TruncMode.YEAR) {
            cal.set(
                    calendar.get(Calendar.YEAR),
                    Calendar.JANUARY,
                    1, 0, 0, 0);
        }
        else if(mode == TruncMode.MONTH) {
            cal.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    1, 0, 0, 0);
        }
        else if(mode == TruncMode.DAY) {
            cal.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    0, 0, 0);
        }
        else if(mode == TruncMode.HOUR) {
            cal.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    0, 0);
        }
        else if(mode == TruncMode.MINUTE) {
            cal.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    0);
        }

        return cal;
    }

    public static Calendar Copy(Calendar calendar) {
        return Trunc(calendar, TruncMode.NONE);
    }

    public static int CalendarToInt(Calendar cal) {
        String s = CalendarToString(cal, "yyyyMMdd");
        return Integer.valueOf(s);
    }

    public static String CalendarToString(Calendar cal, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String CalendarToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(cal.getTime());
    }

    public static String CalendarToString() {
        Calendar cal = Calendar.getInstance();
        return CalendarToString(cal, "dd.MM.yyyy");
    }

    public static String CalendarToString(Long milliSeconds) {
        return CalendarToString(DF.FromLong(milliSeconds));
    }

    public static String CalendarToString(Long milliSeconds, String format) {
        return CalendarToString(DF.FromLong(milliSeconds), format);
    }

    public static String CalendarToICMString(Calendar cal, String formatActYear, String formatPastYear) {
        String format = formatActYear;
        if(cal.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR))
            format = formatPastYear;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }

    public static String CalendarToString(String format) {
        Calendar cal = Calendar.getInstance();
        return CalendarToString(cal, format);
    }

    public static Calendar StringToCalendar(String date, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(date));
        }
        catch(Exception ex) {}
        return cal;
    }

    public static Long StringToLong(String date, String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(date));
        }
        catch(Exception ex) {}
        return DF.ToLong(cal);
    }

    public static Calendar MergeDateAndTime(Calendar date, Calendar time) {
        String s1 = CalendarToString(date, "dd.MM.yyyy");
        String s2 = CalendarToString(time, "HH:mm");
        Calendar cal = StringToCalendar(s1+" "+s2, "dd.MM.yyyy HH:mm");
        return cal;
    }

    public static Calendar MergeDateAndTime(Calendar date, String time) {
        String s1 = CalendarToString(date, "dd.MM.yyyy");
        String s2 = time;
        Calendar cal = StringToCalendar(s1+" "+s2, "dd.MM.yyyy HH:mm");
        return cal;
    }

    public static int CompareCalendarDate(Calendar date1, Calendar date2) {
        long d1 = Long.parseLong(CalendarToString(date1, "yyyyMMdd"));
        long d2 = Long.parseLong(CalendarToString(date2, "yyyyMMdd"));
        if(d1 == d2)
            return 0;
        else if(d1 < d2)
            return -1;
        else
            return 1;
    }

    public static int CompareCalendarDateTime(Calendar date1, Calendar date2) {
        return CompareCalendarDateTime(date1, date2, false);
    }

    public static int CompareCalendarDateTime(Calendar date1, Calendar date2, boolean withSeconds) {

        long d1, d2;
        if(!withSeconds) {
            //Log.d(Globals.TAG, "date1: " + CalendarToString(date1, "dd.MM.yyyy HH:mm") + ", date2: " + CalendarToString(date2, "dd.MM.yyyy HH:mm"));
            d1 = Long.parseLong(CalendarToString(date1, "yyyyMMddHHmm"));
            d2 = Long.parseLong(CalendarToString(date2, "yyyyMMddHHmm"));
        }
        else {
            //Log.d(Globals.TAG, "date1: " + CalendarToString(date1, "dd.MM.yyyy HH:mm:ss") + ", date2: " + CalendarToString(date2, "dd.MM.yyyy HH:mm:ss"));
            d1 = Long.parseLong(CalendarToString(date1, "yyyyMMddHHmmss"));
            d2 = Long.parseLong(CalendarToString(date2, "yyyyMMddHHmmss"));
        }

        if(d1 == d2)
            return 0;
        else if(d1 < d2)
            return -1;
        else
            return 1;
    }

    public static double GetHours(Calendar begin, Calendar end) {
        return GetHours(begin, end, true, null);
    }

    public static double GetHours(Calendar begin, Calendar end, TruncMode truncMode) {
        return GetHours(begin, end, true, truncMode);
    }

    public static double GetHours(Calendar begin, Calendar end, boolean abs, TruncMode truncMode) {
        Calendar b = DF.Copy(begin);
        Calendar e = DF.Copy(end);
        if(truncMode != null) {
            b = DF.Trunc(b, truncMode);
            e = DF.Trunc(e, truncMode);
        }

        long endMill = e.getTimeInMillis(), beginMill = b.getTimeInMillis();
        long differenceInMillis = Long.valueOf(0);
        if(beginMill > endMill && abs)
            differenceInMillis = beginMill - endMill;
        else
            differenceInMillis = endMill - beginMill;

        double hours = (double)differenceInMillis / 1000.0 / 60.0 / 60.0;

        if(hours < 0)
            hours = 0.0;
        return hours;
    }

    public static String HoursToTime(Calendar begin, Calendar end) {
        return HoursToTime(begin, end, TruncMode.MINUTE);
    }

    public static String HoursToTime(Calendar begin, Calendar end, TruncMode truncMode) {
        Calendar b = DF.Trunc(begin, truncMode);
        Calendar e = DF.Trunc(end, truncMode);
        Double hours = GetHours(b,e);
        return HoursToTime(hours);
    }

    public static String HoursToTime(double hours) {
        boolean negative = hours < 0;
        double acthours = Math.abs(hours);

        Double h = Math.floor(acthours);
        int hour = h.intValue();

        double diff = acthours - (double)hour;
        Long dmin = Math.round(diff * 60.0);
        int min = dmin.intValue();
        if (min == 60)
        {
            min = 0;
            hour++;
        }

        String sHour = String.valueOf(hour);
        if (hour < 10)
            sHour = "0" + String.valueOf(hour);

        String sMin = String.valueOf(min);
        if (min < 10)
            sMin = "0" + String.valueOf(min);

        return (negative ? "-" : "") +  sHour + ":" + sMin;
    }

    public static Boolean Crossing(Calendar cal1From, Calendar cal1To, Calendar cal2From, Calendar cal2To) {
        /*long buffer = 1000 * 60;

        long from1 = DF.Trunc(cal1From, TruncMode.MINUTE).getTimeInMillis();
        long to1 = DF.Trunc(cal1To, TruncMode.MINUTE).getTimeInMillis();
        long from2 = DF.Trunc(cal2From, TruncMode.MINUTE).getTimeInMillis();
        long to2 = DF.Trunc(cal2To, TruncMode.MINUTE).getTimeInMillis();

        if((from1 < from2 && to1 > to2) ||
           (from2 < from1 && to2 > to1))
            return true;

        if((from1 >= from2 && from1 < to2 && to1 > to2) ||
           (from2 >= from1 && from2 < to1 && to2 > to1))
            return true;

        if((to1 > from2 && to1 < to2 && from1 < from2) ||
           (to2 > from1 && to2 < to1 && from2 < from1)) {
            return true;
        }

        if((from1 >= from2 && to1 <= to2) ||
           (from2 >= from1 && to2 <= to1))
            return true;*/

        //return false;

        //new rules (since 07.04.2015)
        return DF.Trunc(cal1From, TruncMode.MINUTE).before(DF.Trunc(cal2To, TruncMode.MINUTE)) &&
                DF.Trunc(cal2From, TruncMode.MINUTE).before(DF.Trunc(cal1To, TruncMode.MINUTE));
    }

    public static Boolean IsBetween(Calendar day, Calendar calFrom, Calendar calTo) {
        return isBetween(DF.ToLong(DF.Trunc(day)), DF.ToLong(DF.Trunc(calFrom)), DF.ToLong(DF.Trunc(calTo)));
    }

    private static Boolean isBetween(long l1, long b1, long b2) {
        return (l1 >= b1 && l1 < b2);
    }

    public static Boolean IsToday(Calendar day) {
        return DF.CompareCalendarDate(DF.Now(), day) == 0;
    }

    public static HashMap<Calendar, Double> GetHoursPerDay(Calendar from, Calendar to) {
        HashMap<Calendar, Double> ret = new HashMap<>();

        Calendar act = Calendar.getInstance();
        act.setTimeInMillis(from.getTimeInMillis());

        boolean loop = true;
        while(loop) {
            Calendar endOfDay = DF.Trunc(act);
            endOfDay.add(Calendar.DATE,1);

            if(DF.CompareCalendarDateTime(endOfDay, to) > 0) {
                double hours = DF.GetHours(act, to);
                ret.put(DF.Trunc(act), hours);
                loop = false;
            }
            else {
                double hours = DF.GetHours(act, endOfDay);
                ret.put(DF.Trunc(act), hours);
                act = DF.Trunc(act);
                act.add(Calendar.DATE,1);
            }
        }

        return ret;
    }
}
