package inv.sfs.com.criticapp.Models;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iosdev-1 on 8/29/17.
 */

public class Restaurant {
    public Double latitude;
    public Double longitude;
    public Double rating_google;
    public String ID;
    public String PlaceId;
    public String icon_url;
    public String restaurant_name;
    public String vicinity;
    public int avgRating;
    public float starRating;
    public ParseObject parseObject;
    public List<ParseObject> reviews;
    public String category;

    public Restaurant(){
        reviews = new ArrayList<>();
        parseObject = null;
        avgRating = 0;
        starRating = 0.0f;
    }
}
