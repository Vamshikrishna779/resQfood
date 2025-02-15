package com.example.resqfood;

public class RecipientDetails {
    private String recipientId; // Add id field
    private String recipientName;
    private String recipientContact;
    private String pickupLocation;
    private String preferredDateTime;
    private String specialInstructions;
    private String status;

    public RecipientDetails() {
        // Default constructor required for Firebase
    }

    public RecipientDetails(String recipientName, String recipientContact, String pickupLocation, String preferredDateTime, String specialInstructions,String status) {

        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.pickupLocation = pickupLocation;
        this.preferredDateTime = preferredDateTime;
        this.specialInstructions = specialInstructions;
        this.status = status;
    }

    public String getRecipientId() {
        return recipientId;
    }
    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientContact() {
        return recipientContact;
    }

    public void setRecipientContact(String recipientContact) {
        this.recipientContact = recipientContact;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPreferredDateTime() {
        return preferredDateTime;
    }

    public void setPreferredDateTime(String preferredDateTime) {
        this.preferredDateTime = preferredDateTime;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;


    }

    public String getStatus(){return status;}

    public void setStatus(String status){
        this.status = status;
    }
}
