package com.github.diwakar1988.junket.db.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "REVIEW".
 */
public class Review {

    private String venueId;
    private String text;
    private java.util.Date date;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Review() {
    }

    public Review(String venueId) {
        this.venueId = venueId;
    }

    public Review(String venueId, String text, java.util.Date date) {
        this.venueId = venueId;
        this.text = text;
        this.date = date;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
