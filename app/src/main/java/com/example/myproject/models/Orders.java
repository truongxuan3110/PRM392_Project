package com.example.myproject.models;

import java.util.Date;

public class Orders {

    private String userId;
    private String phone;
    private String address;
    private Date orderDate;
    private String note;
    private float orderTotalCost;
    private String status;
    private String payment;




    public Orders(String userId, String phone, String address, Date orderDate, String note, float orderTotalCost,
                  String status, String payment) {
        this.userId = userId;
        this.phone = phone;
        this.address = address;
        this.orderDate = orderDate;
        this.note = note;
        this.orderTotalCost = orderTotalCost;
        this.status = status;
        this.payment = payment;
    }

    public Orders() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getOrderTotalCost() {
        return orderTotalCost;
    }

    public void setOrderTotalCost(float orderTotalCost) {
        this.orderTotalCost = orderTotalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
