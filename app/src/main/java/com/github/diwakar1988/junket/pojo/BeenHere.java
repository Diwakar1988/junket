
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class BeenHere implements Parcelable{

    public int count;
    public int unconfirmedCount;
    public boolean marked;
    public int lastCheckinExpiredAt;

    protected BeenHere(Parcel in) {
        count = in.readInt();
        unconfirmedCount = in.readInt();
        marked = in.readByte() != 0;
        lastCheckinExpiredAt = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeInt(unconfirmedCount);
        dest.writeByte((byte) (marked ? 1 : 0));
        dest.writeInt(lastCheckinExpiredAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BeenHere> CREATOR = new Creator<BeenHere>() {
        @Override
        public BeenHere createFromParcel(Parcel in) {
            return new BeenHere(in);
        }

        @Override
        public BeenHere[] newArray(int size) {
            return new BeenHere[size];
        }
    };
}
