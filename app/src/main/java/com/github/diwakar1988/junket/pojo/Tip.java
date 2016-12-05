package com.github.diwakar1988.junket.pojo;

import java.util.Date;

/**
 * Created by diwakar.mishra on 02/12/16.
 */


public class Tip {

    public String id;
    public String text;
    public int agreeCount;
    public int disagreeCount;
    public User user;
    public  long createdAt;

    public Date getCreatedDate() {
        return new Date(createdAt);
    }
}
