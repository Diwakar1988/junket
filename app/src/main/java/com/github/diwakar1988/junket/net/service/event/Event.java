package com.github.diwakar1988.junket.net.service.event;


/**
 * Created by diwakar.mishra on 05/12/16.
 */

public class Event<T> {
    public static final int TYPE_NETWORK_AVAILABLE=1;

    private int type;
    private T data;

    public Event(int type) {
        this(type,null);
    }

    public Event(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
