// Time

package com.example.youeye.alarm;

public class Time {
    private int id; // 고유 ID
    private int hour, minute;
    private String month, day, am_pm;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getAm_pm() { return am_pm; }
    public void setAm_pm(String am_pm) { this.am_pm = am_pm; }
}
