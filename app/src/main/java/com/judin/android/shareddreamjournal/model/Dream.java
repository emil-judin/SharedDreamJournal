package com.judin.android.shareddreamjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

@IgnoreExtraProperties
public class Dream extends FirebaseData implements Parcelable {
    private String mId;
    private String mTitle;
    private String mText;
    private String mAuthor;
    private Boolean mLucid;
    private Date mCreationTimestamp;

    public Dream() {
        // Empty Constructor
    }

    protected Dream(Parcel in){
        mId = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mAuthor = in.readString();
        mLucid = in.readBoolean();
        mCreationTimestamp = (Date) in.readSerializable();
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

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getText() {
        return mText;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public boolean isLucid(){
        return mLucid;
    }

    public Date getCreationTimestamp() {
        return mCreationTimestamp;
    }

    @Exclude public String getCreationDateString(){
        return DateFormat.getDateInstance().format(mCreationTimestamp);
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
        dest.writeBoolean(mLucid);
        dest.writeSerializable(mCreationTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dream dream = (Dream) o;
        return mId.equals(dream.getId())
                && Objects.equals(mTitle, dream.getTitle())
                && Objects.equals(mText, dream.getText())
                && Objects.equals(mAuthor, dream.getAuthor())
                && Objects.equals(mLucid, dream.isLucid())
                && Objects.equals(mCreationTimestamp, dream.getCreationTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mText, mAuthor, mLucid, mCreationTimestamp);
    }
}