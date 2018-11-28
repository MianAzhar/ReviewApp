package inv.sfs.com.criticapp.Models;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Mian Azhar on 2/15/2018.
 */

public class Promotion {
    public ParseObject restaurantId;
    public String promotionText;
    public ParseFile promotionImage;
    public Date expirationDate;
    public ParseUser user;
    public String couponNumber;
    public ParseObject parseObject;
}
