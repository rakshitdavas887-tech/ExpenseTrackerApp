package com.rakshit.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rakshit.expensetracker.ui.theme.ExpenseTrackerTheme

// 🔥 Model
data class Transaction(
    val amount: Int,
    val isIncome: Boolean,
    val category: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseTrackerTheme {

                var editIndex by remember { mutableStateOf(-1) }
                var showDialog by remember { mutableStateOf(false) }
                var amount by remember { mutableStateOf("") }
                var isIncome by remember { mutableStateOf(true) }
                var selectedCategory by remember { mutableStateOf("Food") }

                val categories = listOf("Food", "Travel", "Shopping", "Bills", "Salary", "Other")
                val transactions = remember { mutableStateListOf<Transaction>() }

                val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
                val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount }
                val balance = totalIncome - totalExpense

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            editIndex = -1
                            amount = ""
                            isIncome = true
                            selectedCategory = "Food"
                            showDialog = true
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                ) { paddingValues ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {

                        // 🔥 Balance Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Total Balance", color = MaterialTheme.colorScheme.onPrimary)
                                Text(
                                    "₹ $balance",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔥 CUSTOM PIE CHART
                        if (totalIncome != 0 || totalExpense != 0) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text("Analytics")

                                    Spacer(modifier = Modifier.height(16.dp))

                                    CustomPieChart(
                                        income = totalIncome.toFloat(),
                                        expense = totalExpense.toFloat()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // 🔥 Income & Expense
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Card(
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Income")
                                    Text("₹ $totalIncome")
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Expense")
                                    Text("₹ $totalExpense")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Transactions")

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn {
                            itemsIndexed(transactions) { index, txn ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    onClick = {
                                        amount = txn.amount.toString()
                                        isIncome = txn.isIncome
                                        selectedCategory = txn.category
                                        editIndex = index
                                        showDialog = true
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Column {
                                            Text("${txn.category} • ${if (txn.isIncome) "Income" else "Expense"}")
                                            Text("₹ ${txn.amount}")
                                        }

                                        IconButton(onClick = {
                                            transactions.removeAt(index)
                                        }) {
                                            Icon(Icons.Default.Delete, "Delete")
                                        }
                                    }
                                }
                            }
                        }

                        // 🔥 Dialog
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Add Transaction") },
                                text = {
                                    Column {

                                        OutlinedTextField(
                                            value = amount,
                                            onValueChange = { amount = it },
                                            label = { Text("Amount") }
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text("Category")

                                        Row {
                                            categories.forEach { cat ->
                                                FilterChip(
                                                    selected = selectedCategory == cat,
                                                    onClick = { selectedCategory = cat },
                                                    label = { Text(cat) },
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row {
                                            Button(
                                                onClick = { isIncome = true },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Income")
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Button(
                                                onClick = { isIncome = false },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Expense")
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        val amt = amount.toIntOrNull() ?: return@Button

                                        if (editIndex != -1) {
                                            transactions[editIndex] =
                                                Transaction(amt, isIncome, selectedCategory)
                                            editIndex = -1
                                        } else {
                                            transactions.add(Transaction(amt, isIncome, selectedCategory))
                                        }

                                        amount = ""
                                        showDialog = false
                                    }) {
                                        Text("Save")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomPieChart(income: Float, expense: Float) {

    val total = income + expense

    val incomeAngle = (income / total) * 360f
    val expenseAngle = (expense / total) * 360f

    Canvas(modifier = Modifier.size(200.dp)) {

        var startAngle = 0f

        // 🔥 Income Slice (Green)
        drawArc(
            color = Color(0xFF4CAF50),
            startAngle = startAngle,
            sweepAngle = incomeAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )

        startAngle += incomeAngle

        // 🔥 Expense Slice (Red)
        drawArc(
            color = Color(0xFFF44336),
            startAngle = startAngle,
            sweepAngle = expenseAngle,
            useCenter = true,
            size = Size(size.width, size.height)
        )
    }
}