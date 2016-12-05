
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Hours implements Parcelable{

    public boolean isOpen;
    public boolean isLocalHoliday;

    protected Hours(Parcel in) {
        isOpen = in.readByte() != 0;
        isLocalHoliday = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeByte((byte) (isLocalHoliday ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Hours> CREATOR = new Creator<Hours>() {
        @Override
        public Hours createFromParcel(Parcel in) {
            return new Hours(in);
        }

        @Override
        public Hours[] newArray(int size) {
            return new Hours[size];
        }
    };
}
