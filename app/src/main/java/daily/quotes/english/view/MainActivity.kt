package daily.quotes.english.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.huawei.hms.ads.*
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import daily.quotes.english.*
import daily.quotes.english.model.QuoteModel
import daily.quotes.english.work.WorkManagerPeriodic
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

private var quoteID: Int = 0
private var quote: String = ""
private var author: String = ""
private var quotesString: String? = null
private var isFavorite: Boolean = false
private var selectedLanguage: String? = null
private var selectedLanguageCode: String? = null

@SuppressLint("StaticFieldLeak")
var txtQuoteID: TextView? = null

@SuppressLint("StaticFieldLeak")
var txtQuote: TextView? = null

@SuppressLint("StaticFieldLeak")
var txtAuthor: TextView? = null

@SuppressLint("StaticFieldLeak")
var txtQuoteTranslate: TextView? = null

@SuppressLint("StaticFieldLeak")
var linearTranslate: LinearLayout? = null

@SuppressLint("StaticFieldLeak")
var imageRefresh: ImageView? = null

@SuppressLint("StaticFieldLeak")
var imageFavorite: ImageView? = null

@SuppressLint("StaticFieldLeak")
var imageSettings: ImageView? = null

private var interstitialAd: InterstitialAd? = null

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()

        if (Util().isHmsAvailable(applicationContext)) {
            setHMSAds()
        } else {
            setGMSAds()
        }

        // Get all quotes
        quotesString = Util().getJsonDataFromAsset(this, "quotes.json")
        getRandomQuote()

        //startAlarmBroadcastReceiver(applicationContext)

        WorkManagerPeriodic().setWork()
    }

    fun setUI() {
        linearTranslate = findViewById(R.id.linearTranslate)
        imageRefresh = findViewById(R.id.imageRefresh)
        imageFavorite = findViewById(R.id.imageFavorite)
        imageSettings = findViewById(R.id.imageSettings)
        txtQuoteID = findViewById<TextView>(R.id.txtQuoteID)
        txtQuote = findViewById<TextView>(R.id.txtQuote)
        txtAuthor = findViewById<TextView>(R.id.txtAuthor)
        txtQuoteTranslate = findViewById<TextView>(R.id.txtQuoteTranslate)
    }

    @SuppressLint("SetTextI18n")
    private fun getRandomQuote() {
        val quoteArray = JSONArray(quotesString)
        quoteID = Random.nextInt(0, quoteArray.length())
        val quoteObject = JSONObject(quoteArray.get(quoteID).toString())
        quote = quoteObject.getString("quoteText").toString()
        author = quoteObject.getString("quoteAuthor").toString()

        // Quote ID
        txtQuoteID!!.text = "# ${quoteID}"

        // Quote Text
        txtQuote!!.text = quote

        // Author
        if (author.isNotEmpty()) {
            txtAuthor!!.text = "- $author"
        } else {
            txtAuthor!!.text = ""
        }

        checkFavorite()

        // Get new quote
        imageRefresh!!.setOnClickListener {
            getRandomQuote()
        }

        // Toogle favorite
        imageFavorite!!.setOnClickListener {
            toggleFavorite()
        }

        // Open Settings
        imageSettings!!.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        // ...
        sharedPreferences = this.getSharedPreferences(packageName, android.content.Context.MODE_PRIVATE)
        selectedLanguage = sharedPreferences!!.getString("language", null)
        selectedLanguageCode = sharedPreferences!!.getString("languageCode", null)
        if (selectedLanguage != null) {
            // Translate
            translate(quote)
        }

    }

    private fun checkFavorite() {
        // Check Favorite
        isFavorite = SharedPreferenceFavorites().checkFavorite(applicationContext, quoteID)
        if (isFavorite) {
            imageFavorite!!.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            imageFavorite!!.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun toggleFavorite() {
        if (isFavorite) {
            // Remove favorite
            SharedPreferenceFavorites().removeFavorite(applicationContext, QuoteModel(quoteID, quote, author))
            imageFavorite!!.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            isFavorite = false
        } else {
            // Add favorite
            SharedPreferenceFavorites().addFavorite(applicationContext, QuoteModel(quoteID, quote, author))
            imageFavorite!!.setImageResource(R.drawable.ic_baseline_favorite_24)
            isFavorite = true
        }
    }

    private fun translate(quote: String) {
        // Set the API key.
        MLApplication.getInstance().setApiKey(getString(R.string.api_key));

        val setting = MLRemoteTranslateSetting.Factory()
            .setSourceLangCode("en")
            .setTargetLangCode(selectedLanguageCode)
            .create()

        val mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)

        val task = mlRemoteTranslator.asyncTranslate(quote)
        task.addOnSuccessListener {
            txtQuoteTranslate!!.text = it
            linearTranslate!!.visibility = View.VISIBLE
        }.addOnFailureListener { e ->
            linearTranslate!!.visibility = View.GONE
            try {
                val mlException = e as MLException
                val errorCode = mlException.errCode
                val errorMessage = mlException.message
                Log.e(TAG, "Translate Error: ${errorMessage}")
            } catch (error: Exception) {
                Log.e(TAG, "Translate Except Error: ${error}")
            }
        }
    }

    private fun setHMSAds() {
        Log.i(TAG, "HMS ads is proccesing..")

        HwAds.init(this)
        val adParam = AdParam.Builder().build()

        // Banner Ad Init
        val huaweiBannerAdMain: BannerView = findViewById(R.id.huaweiBannerAdMain)
        huaweiBannerAdMain.adId = getString(R.string.huawei_ads_banner_main)
        huaweiBannerAdMain.bannerAdSize = BannerAdSize.BANNER_SIZE_360_144

        // Interstitial Ad Init
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adId = getString(R.string.huawei_ads_fullscreen_main)
        interstitialAd!!.loadAd(adParam)

        // Load banner ad
        huaweiBannerAdMain.loadAd(adParam)
        huaweiBannerAdMain.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.i(TAG, "HMS Main Banner Ad Loaded!")
                huaweiBannerAdMain.visibility = View.VISIBLE
            }

            override fun onAdFailed(errorCode: Int) {
                super.onAdFailed(errorCode)
                Log.e(TAG, "HMS Main Banner Ad ErrorCode: ${errorCode}")
                setGMSAds()
            }
        }

        // Load Interstitial ads
        if (interstitialAd != null && interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
        } else {
            Log.i(TAG, "HMS Interstitial Ad is not loaded")
        }

    }

    private fun setGMSAds() {
        Log.i(TAG, "GMS ads is proccesing..")

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        // GMS Banner Ad
        val googleBannerAdMain: AdView = findViewById(R.id.googleBannerAdMain)
        googleBannerAdMain.loadAd(adRequest)
        googleBannerAdMain.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                googleBannerAdMain.visibility = View.VISIBLE
                Log.i(TAG, "GMS Main Banner Ad Loaded!")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "GMS Main Banner Ad ErrorCode: ${adError}")
            }
        }

        // GMS Interstitial Ad
        val googleInterstitialAd: com.google.android.gms.ads.InterstitialAd = com.google.android.gms.ads.InterstitialAd(this)
        googleInterstitialAd.adUnitId = getString(R.string.google_ads_instertitial_main)
        googleInterstitialAd.loadAd(adRequest)
        googleInterstitialAd.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                googleInterstitialAd.show()
                Log.i(TAG, "GMS Interstitial Ad Loaded!")
            }

            override fun onAdFailedToLoad(adError: LoadAdError?) {
                super.onAdFailedToLoad(adError)
                Log.e(TAG, "GMS Interstitial Ad ErrorCode: ${adError}")
            }
        }
    }

    /*
    fun startAlarmBroadcastReceiver(context: Context) {

        // Intent
        val _intent = Intent(context, AlarmBroadcastReceiver::class.java)

        // Is alarm working
        val isWorking = PendingIntent.getBroadcast(context, 0, _intent, PendingIntent.FLAG_NO_CREATE) != null
        Log.d(TAG, "alarm is " + (if (isWorking) "" else "not") + " working...")

        if (!isWorking) {
            val pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            //alarmManager.cancel(pendingIntent)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = 13
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pendingIntent

            Log.i(TAG, "Alarm was setted at 13:00 PM")
        }
    }
    */
}