package com.projects.jezinka.myplaces

import android.provider.BaseColumns


object MyPlacesContract {

    class MyPlaceEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "my_place"

            val _ID = "_id"
            val COLUMN_NAME_LONGTITUDE = "longtitude"
            val COLUMN_NAME_LATITUDE = "latitude"
            val COLUMN_NAME_NOTE = "note"
            val COLUMN_NAME_ORDER = "order_no"
        }
    }
}
