package inv.sfs.com.criticapp.Models;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Mian Azhar on 12/26/2017.
 */

public class FullReviewModel {

    public int overall_Rating;
    public String delivery_time;
    public boolean recommend_to_others;
    public String comments;
    public String servers_name;
    public int averageRating;
    public ParseObject parseObject;
    public ParseObject restaurantObj;
    public ParseUser userObj;

    public FullReviewModel(){
        delivery_time = "";
        recommend_to_others =true;
        comments = "";
        servers_name = "";
        averageRating = 0;
        parseObject = null;
    }
}
