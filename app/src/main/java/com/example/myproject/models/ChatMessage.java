package com.example.myproject.models;

import java.util.Date;

public class ChatMessage {
    public String sendid, receivedid, mess, datetime, participantid;
    public Date dateObj;

    public String getParticipantid() {
        return participantid;
    }

    public void setParticipantid(String participantid) {
        this.participantid = participantid;
    }

    public String getSendid() {
        return sendid;
    }

    public void setSendid(String sendid) {
        this.sendid = sendid;
    }

    public String getReceivedid() {
        return receivedid;
    }

    public void setReceivedid(String receivedid) {
        this.receivedid = receivedid;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Date getDateObj() {
        return dateObj;
    }

    public void setDateObj(Date dateObj) {
        this.dateObj = dateObj;
    }
}
