package com.example.myproject.models;

public class Book {

    private int bookID;
    private String bookTitle;
    private String description;
    private int price;
    private int unitInStock;
    private int categoryId;
    private int imageSource;

    public Book(int bookID, String bookTitle, String description, int price, int unitInStock, int categoryId, int imageSource) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.description = description;
        this.price = price;
        this.unitInStock = unitInStock;
        this.categoryId = categoryId;
        this.imageSource = imageSource;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
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

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }
}
