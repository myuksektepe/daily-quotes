package daily.quotes.english.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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

private var mInterstitialAd: InterstitialAd? = null

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()
        setGMSAds()

        // Get all quotes
        quotesString = Util().getJsonDataFromAsset(this, "quotes.json")
        getRandomQuote()

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
        InterstitialAd.load(this, getString(R.string.google_ads_instertitial_main), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "adError: ${adError?.toString()}")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.apply { show(this@MainActivity) }
            }
        })
    }
}