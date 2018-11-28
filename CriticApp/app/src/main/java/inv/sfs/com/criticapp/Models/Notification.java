package inv.sfs.com.criticapp.Models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by applepc on 29/01/2018.
 */

public class Notification{

    public ParseUser user;
    public Boolean isAccepted;
    public Boolean isDeclined;
    public ParseObject promotionId;
    public ParseObject parseObject;
    public Restaurant restaurant;
    public String couponNumber;

    public Notification(){

        isAccepted = false;
        isDeclined = false;
        restaurant = new Restaurant();
    }
}
