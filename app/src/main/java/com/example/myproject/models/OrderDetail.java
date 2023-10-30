package com.example.myproject.models;

public class OrderDetail {
  //  private int orderDetailId;
    private String orderId;

    private int bookID;
    private int quantity;
    private int total;

    public OrderDetail( String orderId, int bookID, int quantity, int total) {
     //   this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.bookID = bookID;
        this.quantity = quantity;
        this.total = total;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getBook() {
        return bookID;
    }

    public void setBook(Book book) {
        this.bookID = bookID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
