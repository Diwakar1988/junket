package com.github.diwakar1988.junket.net.service;

import android.util.Log;

import com.github.diwakar1988.junket.BuildConfig;
import com.github.diwakar1988.junket.Junket;
import com.github.diwakar1988.junket.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class ServiceController {

    private static final String TAG = ServiceController.class.getSimpleName();

    //create new on foursquare.com
    public static final String FOURSQUARE_CLIENT_ID ="CM21KZD4QJRUVTSIVPJISFUQSV0FHBKG3TZRLH4M5ZIVSUNX";
    public static final String FOURSQUARE_CLIENT_SECRET ="AWFDESPDPUG3GXSUOVWTRPRYCNYVXMFBBPHDIODAG5HOYECC";
    public static final String FOURSQUARE_API_VERSION = "20161018"; //up to this date (yyyymmdd) we are ok with changes

    private static final long SIZE_OF_DATA_CACHE = 10 * 1024 * 1024; // 10 MB
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final int MAX_AGE = 24 * 60 * 60;


    private volatile static ServiceController instance;

    private OkHttpClient client;
    public static  void init() {
        getInstance();
    }
    public static ServiceController getInstance() {
        if (instance==null){
            synchronized (ServiceController.class){
                if (instance==null){
                    instance=new ServiceController();
                }
            }
        }
        return instance;
    }
    private ServiceController() {
        if (instance!=null){
            //prevent reflection
            throw new IllegalStateException("Instance already initialized");
        }
        client = getOkHttpClient();
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .cache(getCache())
                .addNetworkInterceptor(getOfflineCacheInterceptor()).build();
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }
    private Object readResolve() throws ObjectStreamException {
        // prevent d-serialization
        return getInstance();
    }

    public void loadVenues(Map<String,String>options,Callback callback){
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        applyDefaultHeaders(options);
        options.put("venuePhotos","1"); //show venue pic

        service.listVenues(options).enqueue(callback);
    }
    public void loadPhotos(String venueId,Map<String,String>options,Callback callback){
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        applyDefaultHeaders(options);

        service.listPhotos(venueId,options).enqueue(callback);

    }
    public void loadTips(String venueId,Map<String,String>options,Callback callback){
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        applyDefaultHeaders(options);

        service.listTips(venueId,options).enqueue(callback);

    }
    private void applyDefaultHeaders(Map<String, String> options) {
        options.put("client_id", FOURSQUARE_CLIENT_ID);
        options.put("client_secret", FOURSQUARE_CLIENT_SECRET);
        options.put("v", FOURSQUARE_API_VERSION);
    }


    public static Interceptor getOfflineCacheInterceptor ()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept (Chain chain) throws IOException
            {
                Response originalResponse = chain.proceed(chain.request());
                if (Utils.isNetworkAvailable(Junket.getInstance())) {
                    return originalResponse.newBuilder()
                            .header(CACHE_CONTROL, "public, max-age=" + MAX_AGE)
                            .build();
                } else {
                    return originalResponse.newBuilder()
                            .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + 7*MAX_AGE)
                            .build();
                }
            }
        };
    }

    private Cache getCache() {
        Cache cache = null;

        try{
            cache = new Cache(new File(Junket.getInstance().getCacheDir(),"responses"), SIZE_OF_DATA_CACHE);
            cache.evictAll();
            Log.d(TAG,"***** Cache created!");
        }catch (Exception e){
            Log.e(TAG,"***** Could not create cache!",e);
        }
        return cache;
    }

}
