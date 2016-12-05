package com.github.diwakar1988.junket.net.service.response;

import com.github.diwakar1988.junket.pojo.Tip;
import com.github.diwakar1988.junket.pojo.Venue;

import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class TipServiceResponse extends Response{

    private TipResponse response;

    public Tips getTips(){
        return response.tips;
    }
    private class TipResponse{
            private Tips tips;
    }
    public static class Tips{
            public int count;
            public ArrayList<Tip> items;
    }
}
