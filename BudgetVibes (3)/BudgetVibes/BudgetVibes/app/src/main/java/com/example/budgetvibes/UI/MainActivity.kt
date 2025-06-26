package com.example.budgetvibes.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetvibes.Adapters.CardAdapter
import com.example.budgetvibes.Adapters.ExpensesAdapter
import com.example.budgetvibes.Adapters.IncomeAdapter
import com.example.budgetvibes.Data.LoggedInUser
import com.example.budgetvibes.Databases.CardDB
import com.example.budgetvibes.Databases.ExpenseDB
import com.example.budgetvibes.Databases.IncomeDB
import com.example.budgetvibes.R
import com.example.budgetvibes.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityLoginBinding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var expenseDB: ExpenseDB
    private lateinit var incomeDB: IncomeDB
    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var cardDB: CardDB
    private lateinit var cardAdapter: CardAdapter
    private lateinit var incomeAdapter: IncomeAdapter

    private val PREFS_NAME = "theme_prefs"
    private val KEY_IS_DARK = "is_dark_mode"

    override fun onCreate(savedInstanceState: Bundle?) {

        // ✅ Apply saved theme before super.onCreate
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean(KEY_IS_DARK, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mainActivityLoginBinding = ActivityMainBinding.inflate(layoutInflater)

        expenseDB = ExpenseDB(this)
        incomeDB = IncomeDB(this)
        cardDB = CardDB(this)

        setContentView(mainActivityLoginBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        expensesAdapter = ExpensesAdapter(expenseDB.getExpenses(), this)
        mainActivityLoginBinding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        mainActivityLoginBinding.transactionsRecyclerView.adapter = expensesAdapter

        incomeAdapter = IncomeAdapter(incomeDB.getIncomeTransactions(), this)
        cardAdapter = CardAdapter(cardDB.getCards(), this)

        mainActivityLoginBinding.cardsRecyclerView.layoutManager = LinearLayoutManager(this)
        mainActivityLoginBinding.cardsRecyclerView.adapter = cardAdapter

        mainActivityLoginBinding.expenseChip.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        mainActivityLoginBinding.incomeChip.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }

        val totalExpenses = expenseDB.getTotalExpenses()
        val totalIncome = incomeDB.totalIncome()
        val balance = totalIncome - totalExpenses

        mainActivityLoginBinding.totalIncomeText.text = formatAsCurrency(totalIncome)
        mainActivityLoginBinding.totalExpensesText.text = formatAsCurrency(totalExpenses)
        mainActivityLoginBinding.balanceAmountText.text = formatAsCurrency(balance)

        bottomNav = mainActivityLoginBinding.bottomNavigationView
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_cards -> {
                    startActivity(Intent(this, CardsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_graph -> {
                    startActivity(Intent(this, GraphActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // ✅ Theme toggle logic
        mainActivityLoginBinding.themeToggleButton.setOnClickListener {
            val newIsDarkMode = !isDarkMode
            sharedPref.edit().putBoolean(KEY_IS_DARK, newIsDarkMode).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (newIsDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            Toast.makeText(this, "Theme changed", Toast.LENGTH_SHORT).show()
        }
    }

    fun formatAsCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return format.format(amount)
    }

    override fun onResume() {
        super.onResume()
        expensesAdapter.updateUi(expenseDB.getExpenses())
        incomeAdapter.updateIncomeUi(incomeDB.getIncomeTransactions())
    }
}
