package com.miahshihab.mymoney.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "mymoney_pro.db"
        const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_ACCOUNTS = "accounts"
        const val TABLE_RECORDS = "records"
        const val TABLE_CATEGORIES = "categories"
        const val TABLE_LOANS = "loans"
        const val TABLE_LOAN_PAYMENTS = "loan_payments"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_ACCOUNTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                initial_balance REAL NOT NULL,
                current_balance REAL NOT NULL,
                currency TEXT NOT NULL,
                color_hex TEXT NOT NULL,
                icon TEXT NOT NULL,
                is_excluded INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_CATEGORIES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                icon_key TEXT NOT NULL,
                color_hex TEXT NOT NULL,
                type TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_RECORDS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id INTEGER NOT NULL,
                to_account_id INTEGER,
                category_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                type TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                notes TEXT,
                photo_uri TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_LOANS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                counterparty_name TEXT NOT NULL,
                counterparty_contact TEXT,
                total_amount REAL NOT NULL,
                remaining_amount REAL NOT NULL,
                loan_type TEXT NOT NULL,
                linked_account_id INTEGER NOT NULL,
                start_date INTEGER NOT NULL,
                due_date INTEGER,
                status TEXT NOT NULL,
                notes TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_LOAN_PAYMENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                loan_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                payment_date INTEGER NOT NULL,
                linked_account_id INTEGER NOT NULL,
                note TEXT
            )
        """.trimIndent())

        // Seed default starter categories and accounts
        seedDefaults(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Migration logic
    }

    private fun seedDefaults(db: SQLiteDatabase) {
        val acc = ContentValues().apply {
            put("name", "Cash Wallet")
            put("initial_balance", 0.0)
            put("current_balance", 0.0)
            put("currency", "USD")
            put("color_hex", "#4CAF50")
            put("icon", "wallet")
            put("is_excluded", 0)
        }
        db.insert(TABLE_ACCOUNTS, null, acc)

        val defaultCats = listOf(
            Triple("Food & Dining", "#FF7043", "EXPENSE"),
            Triple("Groceries", "#26A69A", "EXPENSE"),
            Triple("Transportation", "#42A5F5", "EXPENSE"),
            Triple("Salary & Income", "#66BB6A", "INCOME"),
            Triple("Investments", "#AB47BC", "INCOME"),
            Triple("Bills & Utilities", "#FFA726", "EXPENSE")
        )

        for ((name, color, type) in defaultCats) {
            val cv = ContentValues().apply {
                put("name", name)
                put("icon_key", "category")
                put("color_hex", color)
                put("type", type)
            }
            db.insert(TABLE_CATEGORIES, null, cv)
        }
    }

    fun exportToJsonString(): String {
        val root = JSONObject()
        val db = readableDatabase

        // Export Accounts
        val accArr = JSONArray()
        val cAcc = db.rawQuery("SELECT * FROM $TABLE_ACCOUNTS", null)
        while (cAcc.moveToNext()) {
            val item = JSONObject().apply {
                put("name", cAcc.getString(cAcc.getColumnIndexOrThrow("name")))
                put("initial_balance", cAcc.getDouble(cAcc.getColumnIndexOrThrow("initial_balance")))
                put("current_balance", cAcc.getDouble(cAcc.getColumnIndexOrThrow("current_balance")))
                put("currency", cAcc.getString(cAcc.getColumnIndexOrThrow("currency")))
                put("color_hex", cAcc.getString(cAcc.getColumnIndexOrThrow("color_hex")))
                put("icon", cAcc.getString(cAcc.getColumnIndexOrThrow("icon")))
            }
            accArr.put(item)
        }
        cAcc.close()
        root.put("accounts", accArr)

        // Export Categories
        val catArr = JSONArray()
        val cCat = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES", null)
        while (cCat.moveToNext()) {
            val item = JSONObject().apply {
                put("name", cCat.getString(cCat.getColumnIndexOrThrow("name")))
                put("color_hex", cCat.getString(cCat.getColumnIndexOrThrow("color_hex")))
                put("type", cCat.getString(cCat.getColumnIndexOrThrow("type")))
            }
            catArr.put(item)
        }
        cCat.close()
        root.put("categories", catArr)

        // Export Records
        val recArr = JSONArray()
        val cRec = db.rawQuery("SELECT * FROM $TABLE_RECORDS", null)
        while (cRec.moveToNext()) {
            val item = JSONObject().apply {
                put("amount", cRec.getDouble(cRec.getColumnIndexOrThrow("amount")))
                put("type", cRec.getString(cRec.getColumnIndexOrThrow("type")))
                put("timestamp", cRec.getLong(cRec.getColumnIndexOrThrow("timestamp")))
                put("notes", cRec.getString(cRec.getColumnIndexOrThrow("notes")))
            }
            recArr.put(item)
        }
        cRec.close()
        root.put("records", recArr)

        // Export Loans
        val loanArr = JSONArray()
        val cLoan = db.rawQuery("SELECT * FROM $TABLE_LOANS", null)
        while (cLoan.moveToNext()) {
            val item = JSONObject().apply {
                put("counterparty_name", cLoan.getString(cLoan.getColumnIndexOrThrow("counterparty_name")))
                put("total_amount", cLoan.getDouble(cLoan.getColumnIndexOrThrow("total_amount")))
                put("remaining_amount", cLoan.getDouble(cLoan.getColumnIndexOrThrow("remaining_amount")))
                put("loan_type", cLoan.getString(cLoan.getColumnIndexOrThrow("loan_type")))
                put("status", cLoan.getString(cLoan.getColumnIndexOrThrow("status")))
                put("notes", cLoan.getString(cLoan.getColumnIndexOrThrow("notes")))
            }
            loanArr.put(item)
        }
        cLoan.close()
        root.put("loans", loanArr)

        return root.toString(2)
    }
}
