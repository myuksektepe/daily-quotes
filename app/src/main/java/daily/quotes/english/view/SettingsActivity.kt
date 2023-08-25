package daily.quotes.english.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import daily.quotes.english.*
import daily.quotes.english.adapter.QuoteModelListAdapter
import daily.quotes.english.model.QuoteModel
import org.json.JSONArray
import org.json.JSONObject

private var languagesArray: JSONArray? = null
private var languagesList: ArrayList<String> = ArrayList<String>()
private var languagesCodeList: ArrayList<String> = ArrayList<String>()
private var selectedLanguage: String? = null
private var selectedLanguageCode: String? = null
private var favoritesList: List<QuoteModel>? = null

@SuppressLint("StaticFieldLeak")
var imageGoBack: ImageView? = null

@SuppressLint("StaticFieldLeak")
var spinnerLanguages: Spinner? = null

@SuppressLint("StaticFieldLeak")
var txtNoFavorite: TextView? = null

@SuppressLint("StaticFieldLeak")
var listViewFavorites: ListView? = null


class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setUI()
        setGMSAds()

        // Go Back
        imageGoBack!!.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.hold, R.anim.slide_out_left);
        }

        // Get favorite list
        favoritesList = SharedPreferenceFavorites().getFavorites(this) as List<QuoteModel>?
        if (favoritesList == null) {
            txtNoFavorite!!.visibility = View.VISIBLE
        } else {
            if (favoritesList!!.isEmpty()) {
                txtNoFavorite!!.visibility = View.VISIBLE
            } else {
                val quoteModelListAdapter = QuoteModelListAdapter(this, favoritesList as MutableList<QuoteModel>)
                listViewFavorites!!.adapter = quoteModelListAdapter
                listViewFavorites!!.visibility = View.VISIBLE
            }
        }


        // Get selected language
        selectedLanguage = sharedPreferences!!.getString("language", null)
        selectedLanguageCode = sharedPreferences!!.getString("languageCode", null)

        // Gel all languages from json
        // Clear the languages list
        languagesList.clear()
        languagesCodeList.clear()
        languagesArray = null
        languagesArray = JSONArray(Util().getJsonDataFromAsset(this, "languages.json"))
        for (i in 0 until languagesArray!!.length()) {
            val languageObject: JSONObject = languagesArray!!.getJSONObject(i)
            languagesList.add(languageObject.getString("Language"))
            languagesCodeList.add(languageObject.getString("Code"))
        }

        if (selectedLanguage != null) {
            languagesList.add(0, "Selected: ${selectedLanguage}")
            languagesCodeList.add(0, "null")
        } else {
            languagesList.add(0, "Select")
            languagesCodeList.add(0, "null")
        }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languagesList)
        spinnerLanguages!!.adapter = spinnerAdapter
        spinnerLanguages!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Log.i(TAG, "selectedPosition: ${position}")

                if (position != 0) {
                    selectedLanguage = languagesList.get(position)
                    selectedLanguageCode = languagesCodeList.get(position)
                    sharedPreferences!!.edit().putString("language", selectedLanguage).apply()
                    sharedPreferences!!.edit().putString("languageCode", selectedLanguageCode).apply()

                    Log.i(TAG, "selectedLanguage: ${selectedLanguage}")
                    Log.i(TAG, "selectedLanguageCode: ${selectedLanguageCode}")

                    Toast.makeText(applicationContext, getString(R.string.language_saved), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }

    private fun setUI() {
        imageGoBack = findViewById(R.id.imageGoBack)
        spinnerLanguages = findViewById(R.id.spinnerLanguages)
        txtNoFavorite = findViewById(R.id.txtNoFavorite)
        listViewFavorites = findViewById(R.id.listViewFavorites)
    }

    private fun setGMSAds() {
        Log.i(TAG, "GMS ads is proccesing..")

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        val googleBannerAdSettings: AdView = findViewById(R.id.googleBannerAdSettings)
        googleBannerAdSettings.loadAd(adRequest)
        googleBannerAdSettings.adListener = object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                googleBannerAdSettings.visibility = View.VISIBLE
                Log.i(TAG, "GMS Settings Banner Ad Loaded!")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "GMS Settings Banner Ad ErrorCode: ${adError}")
            }
        }
    }

}