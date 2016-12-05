
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Stats implements Parcelable{

    public int checkinsCount;
    public int usersCount;
    public int tipCount;

    protected Stats(Parcel in) {
        checkinsCount = in.readInt();
        usersCount = in.readInt();
        tipCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(checkinsCount);
        dest.writeInt(usersCount);
        dest.writeInt(tipCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Stats> CREATOR = new Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };
}
