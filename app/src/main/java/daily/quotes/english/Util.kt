package daily.quotes.english

//import com.huawei.hms.api.ConnectionResult
//import com.huawei.hms.api.HuaweiApiAvailability
import android.content.Context
import java.io.IOException


class Util {

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    /*
    fun isHmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS == result
        }
        Log.i(TAG, "isHmsAvailable: $isAvailable")
        return isAvailable
    }
     */

    /*
    fun isGmsAvailable(context: Context?): Boolean {
        var isAvailable = false
        if (null != context) {
            val result: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
            isAvailable = ConnectionResult.SUCCESS === result
        }
        Log.i(TAG, "isGmsAvailable: $isAvailable")
        return isAvailable
    }
     */

}