package com.projects.jezinka.myplaces

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

import kotlinx.android.synthetic.main.places_list_view.view.*

class MyPlacesAdapter(context: Context, private val layoutResourceId: Int, private var data: List<MyPlacesContract.MyPlace>?) : ArrayAdapter<MyPlacesContract.MyPlace>(context, layoutResourceId, data) {

    override fun getCount(): Int {
        return this.data!!.size
    }

    override fun getItem(position: Int): MyPlacesContract.MyPlace? {
        return this.data!![position]
    }

    override fun getItemId(position: Int): Long {
        return this.data!![position].id
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var convertView = view
        val holder: ViewHolder
        val myPlace = this.data!![position]
        if (convertView == null) {
            val inflater = (context as Activity).layoutInflater
            convertView = inflater.inflate(layoutResourceId, parent, false)

            holder = ViewHolder()
            holder.longtitudeNameView = convertView.longtitude_col as TextView
            holder.latitudeNameView = convertView.latitude_col as TextView
            holder.noteNameView = convertView.note_col as TextView
            holder.deleteButton = convertView.delete_button as ImageButton
            holder.editButton = convertView.edit_button as ImageButton
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.longtitudeNameView!!.setText(myPlace.longtitude)
        holder.latitudeNameView!!.setText(myPlace.latitude)
        holder.noteNameView!!.setText(myPlace.note)
        (holder.deleteButton as ImageButton).setOnClickListener({ (context as MainActivity).deletePlace(myPlace.id) })
        (holder.editButton as ImageButton).setOnClickListener({ (context as MainActivity).editPlace(myPlace.id) })


        return convertView
    }

    private class ViewHolder {
        internal var longtitudeNameView: TextView? = null
        internal var latitudeNameView: TextView? = null
        internal var noteNameView: TextView? = null
        internal var deleteButton: ImageButton? = null
        internal var editButton: ImageButton? = null
    }
}
