package com.example.budgetvibes.Databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.budgetvibes.Data.Expense

class ExpenseDB(context: Context) : SQLiteOpenHelper(context, DatabaseName, null, DatabaseVersion) {

    companion object {
        private const val DatabaseName = "ExpensesDb"
        private const val DatabaseVersion = 1
        private const val TableName = "Expenses"
        private const val ExpenseId = "ExpenseId"
        private const val Category = "Category"
        private const val Notes = "Notes"
        private const val Date = "Date"
        private const val Amount = "Amount"
        private const val ExpenseImage = "ExpenseImage"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TableName (
                $ExpenseId INTEGER PRIMARY KEY AUTOINCREMENT,
                $Category TEXT,
                $Notes TEXT,
                $Date TEXT,
                $Amount REAL,
                $ExpenseImage BLOB
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TableName")
        onCreate(db)
    }

    fun addNewExpense(expense: Expense) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(Category, expense.category)
            put(Notes, expense.notes)
            put(Date, expense.date)
            put(Amount, expense.amount)
            put(ExpenseImage, expense.receiptPhoto)
        }
        db.insert(TableName, null, values)
        db.close()
    }

    fun getTotalExpenses(): Double {
        val expenseDb = readableDatabase
        val query = "SELECT SUM($Amount) FROM $TableName"
        val cursor = expenseDb.rawQuery(query, null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        expenseDb.close()
        return total
    }


    fun getExpenses(): List<Expense> {
        val expenseDb = readableDatabase
        val latestExpenses = mutableListOf<Expense>()
        val selectQuery = "SELECT * FROM $TableName"
        val cursor = expenseDb.rawQuery(selectQuery, null)

        while (cursor.moveToNext()) {
            val category = cursor.getString(cursor.getColumnIndexOrThrow(Category))
            val notes = cursor.getString(cursor.getColumnIndexOrThrow(Notes))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(Date))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(Amount))
            val receiptPhoto = cursor.getBlob(cursor.getColumnIndexOrThrow(ExpenseImage))

            val expenseCreated = Expense(
                category = category,
                notes = notes,
                date = date,
                amount = amount,
                receiptPhoto = receiptPhoto
            )
            latestExpenses.add(expenseCreated)
        }

        cursor.close()
        expenseDb.close()
        return latestExpenses
    }
}