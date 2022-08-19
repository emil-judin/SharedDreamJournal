package com.judin.android.shareddreamjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

//TODO: lucid
@IgnoreExtraProperties
public class Dream implements Parcelable {
    @Exclude private String mId;
    private String mTitle;
    private String mText;
    private String mAuthor;
    private Date mAddedDate;
    @Exclude private boolean mIsFavorite = false;

    public Dream(){}

    protected Dream(Parcel in){
        mId = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mAuthor = in.readString();
        mAddedDate = (Date) in.readSerializable();
        mIsFavorite = in.readInt() == 1;
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

    @Exclude public String getId() {
        return mId;
    }

    @Exclude public void setId(String id) {
        mId = id;
    }

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

    @Exclude public boolean isFavorite() {
        return mIsFavorite;
    }

    @Exclude public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    @Exclude public String getAddedDateString(){
        return DateFormat.getDateInstance().format(mAddedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Dream){
            Dream dream = (Dream) obj;
            return  mId.equals(dream.getId()) &&
                    mAuthor.equals(dream.getAuthor()) &&
                    mText.equals(dream.getText()) &&
                    mTitle.equals(dream.getTitle()) &&
                    mAddedDate.equals(dream.getAddedDate()) &&
                    mIsFavorite == dream.isFavorite();
        }
        return false;
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
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mText);
        dest.writeString(mAuthor);
        dest.writeSerializable(mAddedDate);
        dest.writeInt(isFavorite() ? 1 : 0);
    }

    public static Dream fromMap(Map<String, Object> hashMap){
        //TODO: getting dream id wrong?
        String id = (String) hashMap.get("dreamId");
        String title = (String) hashMap.get("title");
        String text = (String) hashMap.get("text");
        String author = (String) hashMap.get("author");
        Date date = ((Timestamp) hashMap.get("addedDate")).toDate();

        Dream dream = new Dream();
        dream.setId(id);
        dream.setTitle(title);
        dream.setText(text);
        dream.setAuthor(author);
        dream.setAddedDate(date);

        return dream;
    }
}