package daily.quotes.english

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import daily.quotes.english.model.QuoteModel
import java.util.*


class SharedPreferenceFavorites {
    // This four methods are used for maintaining favorites.
    fun saveFavorites(context: Context, favorites: List<QuoteModel?>?) {
        val settings: SharedPreferences
        val editor: SharedPreferences.Editor
        settings = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = settings.edit()
        val gson = Gson()
        val jsonFavorites = gson.toJson(favorites)
        editor.putString(FAVORITES, jsonFavorites)
        editor.commit()
    }

    fun addFavorite(context: Context, quote: QuoteModel?) {
        var favorites: MutableList<QuoteModel?>? = getFavorites(context)
        if (favorites == null) favorites = ArrayList<QuoteModel?>()
        favorites.add(quote)
        saveFavorites(context, favorites)
    }

    fun removeFavorite(context: Context, quote: QuoteModel?) {
        val favorites: ArrayList<QuoteModel?>? = getFavorites(context)
        if (favorites != null) {
            favorites.remove(quote)
            saveFavorites(context, favorites)
        }
    }

    fun getFavorites(context: Context): ArrayList<QuoteModel?>? {
        val settings: SharedPreferences
        var favorites: List<QuoteModel?>?
        settings = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        if (settings.contains(FAVORITES)) {
            val jsonFavorites = settings.getString(FAVORITES, null)
            val gson = Gson()
            val favoriteItems: Array<QuoteModel> = gson.fromJson<Array<QuoteModel>>(
                jsonFavorites,
                Array<QuoteModel>::class.java
            )
            favorites = Arrays.asList(*favoriteItems)
            favorites = ArrayList<QuoteModel>(favorites)
        } else return null
        return favorites as ArrayList<QuoteModel?>?
    }

    fun checkFavorite(context: Context, quoteID: Int): Boolean {
        val favorites = getFavorites(context)

        if (favorites != null) {
            for (favorite in favorites) {
                if (favorite!!.quoteID == quoteID) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val PREFS_NAME = "QUOTE_APP"
        const val FAVORITES = "Quote_Favorite"
    }
}