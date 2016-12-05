package com.github.diwakar1988.junket.net.service.response;

import com.github.diwakar1988.junket.pojo.Photo;

import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class PhotoServiceResponse extends Response {

    public PhotoResponse response;

    public Photos getPhotos(){
        return response.photos;
    }
    private class PhotoResponse{
        private Photos photos;
    }
    public static class Photos{
        public int count;
        public ArrayList<Photo> items;
    }
}
