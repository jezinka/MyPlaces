package com.projects.jezinka.myplaces

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyPlacesDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES =
            """CREATE TABLE ${MyPlacesContract.MyPlaceEntry.Companion.TABLE_NAME}
                    (${MyPlacesContract.MyPlaceEntry.Companion._ID} INTEGER PRIMARY KEY,
                    ${MyPlacesContract.MyPlaceEntry.Companion.COLUMN_NAME_NOTE} TEXT,
                    ${MyPlacesContract.MyPlaceEntry.Companion.COLUMN_NAME_LONGITUDE} TEXT,
                    ${MyPlacesContract.MyPlaceEntry.Companion.COLUMN_NAME_LATITUDE} TEXT,
                    ${MyPlacesContract.MyPlaceEntry.Companion.COLUMN_NAME_ORDER} INTEGER)"""

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MyPlacesContract.MyPlaceEntry.Companion.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 4
        val DATABASE_NAME = "myPlaces.db"
    }
}