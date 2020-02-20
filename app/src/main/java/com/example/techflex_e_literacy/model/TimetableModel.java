package com.example.techflex_e_literacy.model;

public class TimetableModel {
    public int hour;
    public String subName, subCode, facName, roomNo, key, time;
    public TimetableModel(){

    }
    public TimetableModel(int hour, String subName, String subCode, String facName, String roomNo, String key, String time) {
        this.hour = hour;
        this.subName = subName;
        this.subCode = subCode;
        this.facName = facName;
        this.roomNo = roomNo;
        this.key = key;
        this.time = time;
    }
}
