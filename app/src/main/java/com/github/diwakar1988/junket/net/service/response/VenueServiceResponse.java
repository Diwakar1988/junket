package com.github.diwakar1988.junket.net.service.response;

import com.github.diwakar1988.junket.pojo.Tip;
import com.github.diwakar1988.junket.pojo.Venue;

import java.util.ArrayList;

/**
 * Created by diwakar.mishra on 02/12/16.
 */

public class VenueServiceResponse extends Response{

    public VenueResponse response;

    public static class VenueResponse{
        public int suggestedRadius;
        public int totalResults;
        public ArrayList<VenueGroup>groups=new ArrayList<>();
        public ArrayList<VenueItem> getVenueItems(){

            if (groups==null || groups.size()==0 ||groups.get(0).items==null){
                return new ArrayList<>();
            }

            return groups.get(0).items;

        }
    }
    private static class VenueGroup{
        public ArrayList<VenueItem> items=new ArrayList<>();

    }
    public static class VenueItem{
        public Venue venue;
        private ArrayList<Tip> tips;

        public ArrayList<Tip> getTips() {
            return tips;
        }
        public Tip getFirstTip() {
            if (tips!=null && tips.size()>0){
                return tips.get(0);
            }
            return null;
        }
    }
}
