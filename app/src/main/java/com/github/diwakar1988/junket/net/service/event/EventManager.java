package com.github.diwakar1988.junket.net.service.event;

import com.github.diwakar1988.junket.Junket;

import java.io.ObjectStreamException;
import java.util.LinkedList;

/**
 * Created by diwakar.mishra on 05/12/16.

 */

public class EventManager {
    private LinkedList<EventListener> listeners;

    private volatile static EventManager instance;

    public static EventManager getInstance() {
        if (instance==null){
            synchronized (EventManager.class){
                if (instance==null){
                    instance=new EventManager();
                }
            }
        }
        return instance;
    }
    private EventManager() {
        if (instance!=null){
            //prevent reflection
            throw new IllegalStateException("Instance already initialized");
        }
        this.listeners = new LinkedList<>();
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }
    private Object readResolve() throws ObjectStreamException {
        // prevent d-serialization
        return getInstance();
    }

    public void register(EventListener listener){
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public void unregister(EventListener listener){
        listeners.remove(listener);
    }
    /***
     * Fires event on Main(UI) thread
     */
    public void broadcast(final Event event){
        for (final EventListener l :
                listeners) {
            Junket.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    l.onEvent(event);
                }
            });
        }
    }

}
