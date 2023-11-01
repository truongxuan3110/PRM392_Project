package com.example.myproject.models;

public class Book {
    private int bookId;
    private String bookTitle;
    private String description;
    private int price;
    private int unitInStock;
    private int categoryId;
    private String img;

    public Book() {
    }

    public Book(int bookId, String bookTitle, String description, int price, int unitInStock, int categoryId, String img) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.description = description;
        this.price = price;
        this.unitInStock = unitInStock;
        this.categoryId = categoryId;
        this.img = img;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUnitInStock() {
        return unitInStock;
    }

    public void setUnitInStock(int unitInStock) {
        this.unitInStock = unitInStock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
