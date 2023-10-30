package com.example.myproject.models;

import java.io.Serializable;

public class Cart implements Serializable {
    private boolean isChecked;

    private String userID;
        private int quantity;
        private Book book; // Thêm trường kiểu Book để tham chiếu đến sách.

        public Cart() {
        }

        public Cart( String userID, int quantity, Book book) {
            //   this.bookId = bookId;
            this.userID = userID;
            this.quantity = quantity;
            this.book = book;
        }

        // Các getter và setter hiện có cho bookId, userID và quantity vẫn giữ nguyên.

        public Book getBook() {
            return book;
        }

        public void setBook(Book book) {
            this.book = book;
        }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

