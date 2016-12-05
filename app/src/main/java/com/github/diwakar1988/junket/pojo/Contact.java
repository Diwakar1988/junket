
package com.github.diwakar1988.junket.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Contact implements Parcelable{

    public String phone;
    public String formattedPhone;

    protected Contact(Parcel in) {
        phone = in.readString();
        formattedPhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(formattedPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getNumber(){
        if (!TextUtils.isEmpty(formattedPhone)){
            return formattedPhone;
        }
        if (!TextUtils.isEmpty(phone)){
            return phone;
        }
        return "";
    }
}
