
package com.github.diwakar1988.junket.pojo;


import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable{

    public String id;
    public String name;
    public Contact contact;
    public Location location;
    public boolean verified;
    public Stats stats;
    public float rating;
    public String ratingColor;
    public int ratingSignals;
    public BeenHere beenHere;
    public Hours hours;
    public Photos photos;


    protected Venue(Parcel in) {
        id = in.readString();
        name = in.readString();
        contact = in.readParcelable(Contact.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        verified = in.readByte() != 0;
        stats = in.readParcelable(Stats.class.getClassLoader());
        rating = in.readFloat();
        ratingColor = in.readString();
        ratingSignals = in.readInt();
        beenHere = in.readParcelable(BeenHere.class.getClassLoader());
        hours = in.readParcelable(Hours.class.getClassLoader());
        photos = in.readParcelable(Photos.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(contact, flags);
        dest.writeParcelable(location, flags);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeParcelable(stats, flags);
        dest.writeFloat(rating);
        dest.writeString(ratingColor);
        dest.writeInt(ratingSignals);
        dest.writeParcelable(beenHere, flags);
        dest.writeParcelable(hours, flags);
        dest.writeParcelable(photos, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Venue> CREATOR = new Creator<Venue>() {
        @Override
        public Venue createFromParcel(Parcel in) {
            return new Venue(in);
        }

        @Override
        public Venue[] newArray(int size) {
            return new Venue[size];
        }
    };

    public Photo getFirstPhoto(){
        if (photos!=null && photos.groups!=null && photos.groups.size()>0 && photos.groups.get(0).items!=null&& photos.groups.get(0).items.size()>0){
            return photos.groups.get(0).items.get(0);
        }
        return null;
    }
}
