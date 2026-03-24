package com.rakshit.expensetracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonthlyReportScreen(

    transactions: List<Transaction>

) {

    var selectedMonth by remember {

        mutableStateOf("2026-03")

    }

    val monthlyTransactions = transactions.filter {

        it.date.startsWith(selectedMonth)

    }

    val totalIncome = monthlyTransactions

        .filter { it.isIncome }

        .sumOf { it.amount }

    val totalExpense = monthlyTransactions

        .filter { !it.isIncome }

        .sumOf { it.amount }

    val balance = totalIncome - totalExpense

    val categorySummary = monthlyTransactions

        .groupBy { it.category }

        .mapValues {

                entry -> entry.value.sumOf { it.amount }

        }

    Column(

        modifier = Modifier

            .fillMaxWidth()

            .padding(16.dp)

    ) {

        Text(

            text = "Monthly Report",

            style = MaterialTheme.typography.headlineSmall

        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Income ₹ $totalIncome")

        Text("Expense ₹ $totalExpense")

        Text("Balance ₹ $balance")

        Spacer(modifier = Modifier.height(16.dp))

        Text(

            "Category Summary",

            style = MaterialTheme.typography.titleMedium

        )

        LazyColumn {

            items(categorySummary.toList()) {

                    (category, amount) ->

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(8.dp),

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Text(category)

                    Text("₹ $amount")

                }

            }

        }

    }

}