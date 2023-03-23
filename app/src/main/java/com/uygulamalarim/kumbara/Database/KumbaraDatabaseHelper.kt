package com.uygulamalarim.kumbara.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class KumbaraDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kumbara.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "savings"
        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DEADLINE = "deadline"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_SAVED_MONEY = "savedmoney"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_AMOUNT REAL, " +
                "$COLUMN_DEADLINE TEXT DEFAULT NULL, " +
                "$COLUMN_NOTES TEXT DEFAULT NULL, " +
                "$COLUMN_SAVED_MONEY REAL DEFAULT 0)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun updateSavings(id: Long, title: String, amount: Double, deadline: String?, notes: String?, savedMoney: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DEADLINE, deadline)
            put(COLUMN_NOTES, notes)
            put(COLUMN_SAVED_MONEY, savedMoney)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }
    fun deleteSavingsByTitle(title: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_TITLE=?", arrayOf(title))
        db.close()
    }
    fun insertData(title: String, amount: String, deadline: String?, notes: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_AMOUNT, amount)
        contentValues.put(COLUMN_DEADLINE, deadline)
        contentValues.put(COLUMN_NOTES, notes)
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    fun getAllData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    fun depositMoney(title: String, amount: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SAVED_MONEY, getSavedMoney(title) + amount)
        db.update(TABLE_NAME, contentValues, "$COLUMN_TITLE=?", arrayOf(title))
    }
    fun withdrawMoney(title: String, amount: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SAVED_MONEY, getSavedMoney(title) - amount)
        db.update(TABLE_NAME, contentValues, "$COLUMN_TITLE=?", arrayOf(title))
    }
    fun getSavedMoney(title: String): Double {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_SAVED_MONEY FROM $TABLE_NAME WHERE $COLUMN_TITLE=?"
        val cursor = db.rawQuery(query, arrayOf(title))
        var savedMoney = 0.0
        if (cursor.moveToFirst()) {
            savedMoney = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SAVED_MONEY))
        }
        cursor.close()
        return savedMoney
    }


}


