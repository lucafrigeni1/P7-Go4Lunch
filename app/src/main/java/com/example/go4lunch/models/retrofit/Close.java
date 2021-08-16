
package com.example.go4lunch.models.retrofit;

public class Close {

    private Integer day;
    private String time;

    public Close(Integer day, String time) {
        this.day = day;
        this.time = time;
    }

    public Close() {

    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
