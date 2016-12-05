package com.github.diwakar1988.junket.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by diwakar.mishra on 02/12/16.
 */
public class BackgroundExecutor {
    private Executor executor;
    private static BackgroundExecutor instance = new BackgroundExecutor();

    private BackgroundExecutor() {
        executor = Executors.newFixedThreadPool(3);
    }

    public static BackgroundExecutor getInstance() {
        return instance;
    }

    public void run(Runnable runnable) {
        executor.execute(runnable);
    }
}
