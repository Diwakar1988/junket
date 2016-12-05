
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Location implements Parcelable{

    public String address;
    public double lat;
    public double lng;
    public int distance;
    private List<String> formattedAddress = new ArrayList<String>();

    public Location() {
    }

    protected Location(Parcel in) {
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        distance = in.readInt();
        formattedAddress = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(distance);
        dest.writeStringList(formattedAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getCompleteAddress() {
        if (formattedAddress==null || formattedAddress.size()==0){
            if (!TextUtils.isEmpty(address)){
                return address;
            }else{
                return "";
            }
        }
        StringBuilder sb=new StringBuilder();
        sb.append(formattedAddress.get(0));
        for (int i = 1; i < formattedAddress.size(); i++) {

            sb.append(", ").append(formattedAddress.get(i));
        }
        return sb.toString();
    }
    public boolean hasValues(){
        return !TextUtils.isEmpty(address) && lat!=0 && lng!=0;
    }
}
