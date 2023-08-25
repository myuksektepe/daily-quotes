package daily.quotes.english.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import daily.quotes.english.BaseActivity
import daily.quotes.english.R
import daily.quotes.english.Util
import daily.quotes.english.sharedPreferences
import org.json.JSONArray
import org.json.JSONObject

private var languagesArray: JSONArray? = null
private var languagesList: ArrayList<String> = ArrayList<String>()
private var languagesCodeList: ArrayList<String> = ArrayList<String>()
private var selectedLanguage: String? = null
private var selectedLanguageCode: String? = null

@SuppressLint("StaticFieldLeak")
var listViewLanguages: ListView? = null

@SuppressLint("StaticFieldLeak")
var buttonContiuneWithoutAutoTranslate: TextView? = null

@SuppressLint("StaticFieldLeak")
var buttonContiuneWithLanguage: Button? = null


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Clear the languages list
        languagesList.clear()
        languagesCodeList.clear()
        languagesArray = null

        // Intialize shared preferences
        sharedPreferences = this.getSharedPreferences(packageName, android.content.Context.MODE_PRIVATE)
        val mainLanguage = sharedPreferences!!.getString("language", null)
        if (mainLanguage == null) {

            // Init
            listViewLanguages = findViewById(R.id.listViewLanguages)
            buttonContiuneWithoutAutoTranslate = findViewById(R.id.buttonContiuneWithoutAutoTranslate)
            buttonContiuneWithLanguage = findViewById(R.id.buttonContiuneWithLanguage)

            // Gel all languages from json
            languagesArray = JSONArray(Util().getJsonDataFromAsset(this, "languages.json"))

            for (i in 0 until languagesArray!!.length()) {
                val languageObject: JSONObject = languagesArray!!.getJSONObject(i)
                languagesList.add(languageObject.getString("Language"))
                languagesCodeList.add(languageObject.getString("Code"))
            }

            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languagesList)

            listViewLanguages!!.adapter = spinnerAdapter

            listViewLanguages!!.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedLanguage = languagesList.get(position)
                    selectedLanguageCode = languagesCodeList.get(position)
                    buttonContiuneWithLanguage!!.text = getString(R.string.contiune_with_xxx) + " ${selectedLanguage}"
                    buttonContiuneWithLanguage!!.visibility = View.VISIBLE
                }
            }

            buttonContiuneWithLanguage!!.setOnClickListener {
                sharedPreferences!!.edit().putString("language", selectedLanguage).apply()
                sharedPreferences!!.edit().putString("languageCode", selectedLanguageCode).apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            buttonContiuneWithoutAutoTranslate!!.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                finish()
            }

        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            finish()
        }

    }
}