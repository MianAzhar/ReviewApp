package inv.sfs.com.criticapp.Models;

import com.parse.ParseObject;

/**
 * Created by Mian Azhar on 12/26/2017.
 */

public class FullReviewModel {
    public String delivery_time;
    public boolean recommend_to_others;
    public String comments;
    public String servers_name;
    public int averageRating;
    public ParseObject parseObject;

    public FullReviewModel(){
        delivery_time = "";
        recommend_to_others =true;
        comments = "";
        servers_name = "";
        averageRating = 0;
        parseObject = null;
    }
}
