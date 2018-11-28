package inv.sfs.com.criticapp.Models;

import com.parse.ParseObject;

/**
 * Created by Mian Azhar on 12/26/2017.
 */

public class Rating {
    public String title;
    public String comment;
    public double rated_value;
    public ParseObject parseObject;

    public Rating(){
        title = "";
        comment = "";
        rated_value = 1;
        parseObject = null;
    }
}
