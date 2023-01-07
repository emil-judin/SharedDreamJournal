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
    private String title;
    private String text;
    private String author;
    private Boolean lucid;
    private Date creationTimestamp;

    public Dream() {
        // Empty Constructor
    }

    protected Dream(Parcel in){
        title = in.readString();
        text = in.readString();
        author = in.readString();
        lucid = in.readBoolean();
        creationTimestamp = (Date) in.readSerializable();
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

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isLucid(){
        return lucid;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    @Exclude public String getCreationTimestampString(){
        return DateFormat.getDateInstance().format(creationTimestamp);
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
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(author);
        dest.writeBoolean(lucid);
        dest.writeSerializable(creationTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dream dream = (Dream) o;
        return Objects.equals(this.getId(), dream.getId())
                && Objects.equals(title, dream.getTitle())
                && Objects.equals(text, dream.getText())
                && Objects.equals(author, dream.getAuthor())
                && Objects.equals(lucid, dream.isLucid())
                && Objects.equals(creationTimestamp, dream.getCreationTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text, author, lucid, creationTimestamp);
    }
}