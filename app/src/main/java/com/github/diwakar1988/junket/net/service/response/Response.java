package com.github.diwakar1988.junket.net.service.response;

/**
 * Created by diwakar.mishra on 02/12/16.
 */
public abstract class Response {

    public Meta meta;
    public static class Meta{
        public int code;
        public String requestId;
    }

}
