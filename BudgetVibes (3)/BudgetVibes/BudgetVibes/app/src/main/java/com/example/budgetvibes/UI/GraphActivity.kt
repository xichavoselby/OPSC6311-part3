package com.example.budgetvibes.UI

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetvibes.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class GraphActivity : AppCompatActivity() {
    private var xp = 0
    private var level = 1
    private lateinit var levelText: TextView
    private lateinit var xpProgress: ProgressBar
    private lateinit var pieChart: PieChart
    private lateinit var badge: ImageView
    private var minBudget = 0
    private var maxBudget = 0
    private var totalExpense = 0f
    private val expenseEntries = ArrayList<PieEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph)

        // Initialize views
        levelText = findViewById(R.id.levelText)
        xpProgress = findViewById(R.id.xpProgress)
        val minInput = findViewById<EditText>(R.id.minBudgetInput)
        val maxInput = findViewById<EditText>(R.id.maxBudgetInput)
        val setBudgetBtn = findViewById<Button>(R.id.setBudgetBtn)
        val expenseInput = findViewById<EditText>(R.id.expenseInput)
        val addExpenseBtn = findViewById<Button>(R.id.addExpenseBtn)
        pieChart = findViewById(R.id.piechart)
        badge = findViewById(R.id.congratsBadge)

        // Set budget button click listener
        setBudgetBtn.setOnClickListener {
            minBudget = minInput.text.toString().toIntOrNull() ?: 0
            maxBudget = maxInput.text.toString().toIntOrNull() ?: 0
            Toast.makeText(this, "Budget Set: Min: $minBudget Max: $maxBudget", Toast.LENGTH_SHORT).show()
        }

        // Add expense button click listener
        addExpenseBtn.setOnClickListener {
            val expenseStr = expenseInput.text.toString()
            if (expenseStr.isNotEmpty()) {
                val expense = expenseStr.toFloat()
                totalExpense += expense
                expenseEntries.add(PieEntry(expense, "Item ${expenseEntries.size + 1}"))

                // Update chart
                updateChart()

                // Handle XP and Level
                if (totalExpense >= maxBudget) {
                    badge.visibility = View.VISIBLE
                    Toast.makeText(this, "Congratulations! Max Budget Reached", Toast.LENGTH_SHORT).show()
                    xp += 10
                    if (xp >= 100) {
                        level++
                        xp -= 100
                        Toast.makeText(this, "You leveled up to Level $level!", Toast.LENGTH_SHORT).show()
                    }
                    // Update level and XP text
                    levelText.text = "Level: $level (XP: $xp/100)"
                    xpProgress.progress = xp
                }
            } else {
                Toast.makeText(this, "Please enter a valid expense", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to update the Pie Chart
    private fun updateChart() {
        val dataSet = PieDataSet(expenseEntries, "Expenses")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Set multiple colors for the chart
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate() // Refresh the chart
    }
}
