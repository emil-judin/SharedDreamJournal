package com.judin.android.shareddreamjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class Dream extends FirebaseData implements Parcelable {
    private String mTitle;
    private String mText;
    private String mAuthor;
    private Date mAddedDate;

    public Dream() {}

    protected Dream(Parcel in){
//        mId = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mAuthor = in.readString();
        mAddedDate = (Date) in.readSerializable();
    }

    public static final Creator<Dream> CREATOR = new Creator<Dream>() {
        @Override
        public Dream createFromParcel(Parcel source) {
            return new Dream(source);
        }

        @Override
        public Dream[] newArray(int size) {
            return new Dream[size];
        }
    };

//    public String getId() {
//        return mId;
//    }

//    public void setId(String id) {
//        mId = id;
//    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public Date getAddedDate() {
        return mAddedDate;
    }

    public void setAddedDate(Date addedDate) {
        mAddedDate = addedDate;
    }

    @Exclude public String getAddedDateString(){
        return DateFormat.getDateInstance().format(mAddedDate);
    }

    public static Creator<Dream> getCREATOR(){
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mText);
        dest.writeString(mAuthor);
        dest.writeSerializable(mAddedDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dream dream = (Dream) o;
        return /*mId.equals(dream.getId()) &&*/
                 Objects.equals(mTitle, dream.mTitle)
                && Objects.equals(mText, dream.mText)
                && Objects.equals(mAuthor, dream.mAuthor)
                && Objects.equals(mAddedDate, dream.mAddedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(/*mId,*/ mTitle, mText, mAuthor, mAddedDate);
    }
}