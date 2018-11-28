package inv.sfs.com.criticapp.Models;

import android.os.Parcelable;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Mian Azhar on 2/19/2018.
 */

public class PromotionTemplate implements Serializable {
    public ParseObject restaurantId;
    public String promotionText;
    public String discountText;
    public Date expirationDate;
    public ParseUser user;
    public String objectId;
}
