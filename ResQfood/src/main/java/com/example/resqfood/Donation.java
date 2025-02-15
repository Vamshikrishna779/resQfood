package com.example.resqfood;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public class Donation implements Parcelable{
    private String documentId;
    private String foodName;
    private int quantity;
    private String expirationDate;
    private String packaging;
    private String condition;
    private String mobileNumber;
    private String preferredTime;
    private String location;
    private String specialInstructions;
    private String imageUrl;
    private String userName;
    private String organization;

    public Donation() {
        // Default constructor required for Firebase
    }

    public Donation(String foodName, int quantity, String expirationDate, String packaging, String condition,
                    String mobileNumber, String preferredTime, String location, String specialInstructions,
                    String imageUrl, String userName, String organization) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.packaging = packaging;
        this.condition = condition;
        this.mobileNumber = mobileNumber;
        this.preferredTime = preferredTime;
        this.location = location;
        this.specialInstructions = specialInstructions;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.organization = organization;
    }

    // Getters and setters for all fields


    public String getDocumentId() {
        return documentId;
    }
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    // Parcelable implementation
    protected Donation(Parcel in) {
        foodName = in.readString();
        quantity = in.readInt();
        expirationDate = in.readString();
        packaging = in.readString();
        condition = in.readString();
        mobileNumber = in.readString();
        preferredTime = in.readString();
        location = in.readString();
        specialInstructions = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
        organization = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(foodName);
        dest.writeInt(quantity);
        dest.writeString(expirationDate);
        dest.writeString(packaging);
        dest.writeString(condition);
        dest.writeString(mobileNumber);
        dest.writeString(preferredTime);
        dest.writeString(location);
        dest.writeString(specialInstructions);
        dest.writeString(imageUrl);
        dest.writeString(userName);
        dest.writeString(organization);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Donation> CREATOR = new Creator<Donation>() {
        @Override
        public Donation createFromParcel(Parcel in) {
            return new Donation(in);
        }

        @Override
        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };
}

/*public class Donation {
    private String foodName;
    private int quantity;
    private String expirationDate;
    private String packaging;
    private String condition;
    private String mobileNumber;
    private String preferredTime;
    private String location;
    private String specialInstructions;
    private String imageUrl;
    private String userName;
    private String organization;

    public Donation() {
        // Default constructor required for Firebase
    }

    public Donation(String foodName, int quantity, String expirationDate, String packaging, String condition,
                    String mobileNumber, String preferredTime, String location, String specialInstructions,
                    String imageUrl, String userName, String organization) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.packaging = packaging;
        this.condition = condition;
        this.mobileNumber = mobileNumber;
        this.preferredTime = preferredTime;
        this.location = location;
        this.specialInstructions = specialInstructions;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.organization = organization;
    }

    // Add getters and setters for all fields
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {this.mobileNumber = mobileNumber;}

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
*/
/*
public class Donation implements Parcelable {
    private String id;
    private String foodName;
    private int quantity;
    private String expirationDate;
    private String imageUrl;
    private String userName;

    public Donation() {
        // Default constructor required for Firestore
    }

    protected Donation(Parcel in) {
        id = in.readString();
        foodName = in.readString();
        quantity = in.readInt();
        expirationDate = in.readString();
        imageUrl = in.readString();
        userName = in.readString();
    }

    public Donation(String foodName, int quantity, String expirationDate, String imageUrl, String userName) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }

    public static final Creator<Donation> CREATOR = new Creator<Donation>() {
        @Override
        public Donation createFromParcel(Parcel in) {
            return new Donation(in);
        }

        @Override
        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(foodName);
        dest.writeInt(quantity);
        dest.writeString(expirationDate);
        dest.writeString(imageUrl);
        dest.writeString(userName);
    }
}
*/