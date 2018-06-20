package classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

public class Ride {

    Calendar lastStarted = null;
    float duration;
    float distanceMeter;
    boolean started = false;
    boolean empty = true;

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDistanceMeter() {
        return distanceMeter;
    }

    public void setDistanceMeter(float distanceMeter) {
        this.distanceMeter = distanceMeter;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setLastStarted(Calendar lastStarted) {
        this.lastStarted = lastStarted;
    }

    public String getFormattedDuration() {
        if(isEmpty())
            return "--:--";
        else {
            double duration = getDuration();
            if(isStarted() && lastStarted != null) {
                //add to duration
                duration += (float)DF.GetHours(lastStarted, DF.Now());
            }

            return DF.HoursToTime(duration);
        }
    }

    public String getFormattedDistance() {
        if(isEmpty())
            return "--";
        else
            return new DecimalFormat("###0.00").format(getDistanceMeter()/1000).replace(".",",");
    }

    public void start(Context context) {
        lastStarted = DF.Now();
        setStarted(true);
        setEmpty(false);
        save(context);
    }

    public void stop(Context context) {
        setStarted(false);
        setEmpty(false);
        setDuration(getDuration() + (float)DF.GetHours(lastStarted, DF.Now()));
        save(context);
    }

    public void addMeters(Context context, double meters) {
        distanceMeter += (float)meters;
        save(context);
    }

    public void save(Context context) {
        SharedPreferences.Editor editor = ((Activity)context).getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean("ride", true);
        editor.putBoolean("started", isStarted());
        editor.putBoolean("empty", isEmpty());
        editor.putFloat("distanceMeter", getDistanceMeter());
        editor.putFloat("duration", getDuration());
        if(lastStarted != null)
            editor.putLong("lastStarted", DF.ToLong(lastStarted));
        else
            editor.putLong("lastStarted", 0);
        editor.commit();
    }

    public static Ride load(Context context) {
        SharedPreferences pref = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        Ride ride = new Ride();
        if(pref.getBoolean("ride", false)) {
            ride.setEmpty(pref.getBoolean("empty", true));
            ride.setStarted(pref.getBoolean("started", false));
            ride.setDistanceMeter(pref.getFloat("distanceMeter", 0));
            ride.setDuration(pref.getFloat("duration", 0));

            long ls = pref.getLong("lastStarted", 0);
            if(ls > 0)
                ride.setLastStarted(DF.FromLong(ls));
        }
        return ride;
    }

    public static void reset(Context context) {
        SharedPreferences.Editor editor = ((Activity)context).getPreferences(Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
}
