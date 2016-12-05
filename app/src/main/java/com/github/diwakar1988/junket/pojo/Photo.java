
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{

    public String id;
    public String prefix;
    public String suffix;
    public int width;
    public int height;
    public User user;

    protected Photo(Parcel in) {
        id = in.readString();
        prefix = in.readString();
        suffix = in.readString();
        width = in.readInt();
        height = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(prefix);
        dest.writeString(suffix);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
