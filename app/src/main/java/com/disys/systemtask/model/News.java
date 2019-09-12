package com.disys.systemtask.model;

import android.os.Parcel;
import android.os.Parcelable;

public class News implements Parcelable{


    public News()
    {

    }

   String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String description;
    String date;
    String image;


    protected News(Parcel in)
    {
        title=in.readString();
        description=in.readString();
        date=in.readString();
        image=in.readString();

    }


    public News(String title,String description,String date,String image)
    {
        this.title=title;
        this.description=description;
        this.date=date;
        this.image=image;
    }
    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public static Creator<News> getCREATOR() {
        return CREATOR;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(image);
    }
}
