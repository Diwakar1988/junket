package com.github.diwakar1988.junket.util;

import com.github.diwakar1988.junket.pojo.Photo;

/**
 * Created by diwakar.mishra on 03/12/16.
 */

public class PhotoUtil {
    public static final int PHOTO_DIMENSION_SMALL =300;//px

    public static final String PHOTO_EMPTY ="empty";
    public static String createSmallPhotoURL(Photo photo) {

        StringBuilder sb=new StringBuilder();
        if (photo!=null){
            sb.append(photo.prefix);
            sb.append(PHOTO_DIMENSION_SMALL);
            sb.append("x");
            sb.append(PHOTO_DIMENSION_SMALL);
            sb.append(photo.suffix);
        }else{
            sb.append(PHOTO_EMPTY);
        }
        return sb.toString();
    }
    public static String createFullPhotoURL(Photo photo) {

        StringBuilder sb=new StringBuilder();
        if (photo!=null){
            sb.append(photo.prefix);
            sb.append(photo.width);
            sb.append("x");
            sb.append(photo.height);
            sb.append(photo.suffix);
        }else{
            sb.append(PHOTO_EMPTY);
        }
        return sb.toString();
    }


}
