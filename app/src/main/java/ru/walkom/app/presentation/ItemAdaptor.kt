package ru.walkom.app.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.walkom.app.R

class ItemAdaptor(private var mList: List<String>): RecyclerView.Adapter<ItemAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tour_route_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.item.text = item
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val item: TextView = ItemView.findViewById(R.id.titleRouteTour)
    }
}