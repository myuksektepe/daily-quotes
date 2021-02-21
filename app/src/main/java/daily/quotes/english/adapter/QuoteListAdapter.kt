package daily.quotes.english.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import daily.quotes.english.R
import daily.quotes.english.SharedPreferenceFavorites
import daily.quotes.english.model.QuoteModel
import java.util.*


class QuoteModelListAdapter(context: Context, quotes: MutableList<QuoteModel>) : ArrayAdapter<QuoteModel>(context, R.layout.quote_list_item, quotes) {
    var quotes: MutableList<QuoteModel>
    var sharedPreference: SharedPreferenceFavorites

    private inner class ViewHolder {
        var txtQuoteItem: TextView? = null
        var txtAuthorItem: TextView? = null
    }

    override fun getCount(): Int {
        return quotes.size
    }

    override fun getItem(position: Int): QuoteModel {
        return quotes[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder? = null
        if (convertView == null) {

            val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.quote_list_item, null)
            holder = ViewHolder()
            holder.txtQuoteItem = convertView.findViewById<View>(R.id.txtQuoteItem) as TextView
            holder.txtAuthorItem = convertView.findViewById<View>(R.id.txtAuthorItem) as TextView
            convertView!!.setTag(holder)

        } else {
            holder = convertView.tag as ViewHolder
        }

        val quote: QuoteModel = getItem(position) as QuoteModel
        holder!!.txtQuoteItem!!.setText(quote.quoteText)
        holder!!.txtAuthorItem!!.setText(quote.quoteAuthor)

        return convertView!!
    }

    /*Checks whether a particular product exists in SharedPreferences*/
    fun checkFavoriteItem(checkQuoteModel: QuoteModel?): Boolean {
        var check = false
        val favorites: ArrayList<QuoteModel?>? = sharedPreference.getFavorites(context)
        if (favorites != null) {
            for (product in favorites) {
                if (product!!.equals(checkQuoteModel)) {
                    check = true
                    break
                }
            }
        }
        return check
    }

    fun add(quote: QuoteModel) {
        super.add(quote)
        quotes.add(quote)
        notifyDataSetChanged()
    }

    fun remove(quote: QuoteModel) {
        super.remove(quote)
        quotes.remove(quote)
        notifyDataSetChanged()
    }

    init {
        this.quotes = quotes
        sharedPreference = SharedPreferenceFavorites()
    }
}