package malvolyo.teachingassistant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by malvo.
 */

public class Utils {

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void networkUnavailable(Context mContext) {
        Toast.makeText(mContext.getApplicationContext(), "Network Unavailable", Toast.LENGTH_SHORT).show();
    }

    public static String getDay() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(new Date());
    }
}
