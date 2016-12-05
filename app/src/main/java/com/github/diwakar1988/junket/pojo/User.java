
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class User implements Parcelable{

    public String id;
    public String firstName;
    public String lastName;
    public String gender;
    public Photo photo;
    public boolean isAnonymous;
    private int name;

    public User() {
    }

    protected User(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        gender = in.readString();
        isAnonymous = in.readByte() != 0;
        name = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(gender);
        dest.writeByte((byte) (isAnonymous ? 1 : 0));
        dest.writeInt(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(firstName)){
            sb.append(firstName);
        }
        if (!TextUtils.isEmpty(lastName)){
            sb.append(' ').append(lastName);
        }
        return sb.toString();
    }
}
