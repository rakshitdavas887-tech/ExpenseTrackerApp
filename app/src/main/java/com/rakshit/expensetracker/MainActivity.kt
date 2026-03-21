package com.rakshit.expensetracker

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rakshit.expensetracker.ui.theme.ExpenseTrackerTheme
import java.util.Calendar

// UPDATED MODEL
data class Transaction(
    val amount: Int,
    val isIncome: Boolean,
    val category: String,
    val date: String
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            ExpenseTrackerTheme {

                val context = LocalContext.current

                var showDialog by remember { mutableStateOf(false) }
                var editIndex by remember { mutableStateOf(-1) }

                var amount by remember { mutableStateOf("") }
                var isIncome by remember { mutableStateOf(true) }
                var selectedCategory by remember { mutableStateOf("Food") }
                var selectedDate by remember { mutableStateOf("") }

                val categories = listOf(
                    "Food","Travel","Shopping","Bills","Salary","Other"
                )

                val transactions = remember {
                    mutableStateListOf<Transaction>()
                }

                val totalIncome =
                    transactions.filter { it.isIncome }.sumOf { it.amount }

                val totalExpense =
                    transactions.filter { !it.isIncome }.sumOf { it.amount }

                val balance = totalIncome - totalExpense

                val calendar = Calendar.getInstance()

                val datePicker = DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        selectedDate =
                            "$day/${month+1}/$year"

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                Scaffold(

                    floatingActionButton = {

                        FloatingActionButton(

                            onClick = {

                                editIndex = -1
                                amount = ""
                                selectedDate = ""

                                showDialog = true
                            }

                        ) {

                            Icon(Icons.Default.Add,"add")

                        }

                    }

                ) { paddingValues ->

                    Column(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)

                    ) {

                        // BALANCE CARD
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text("Total Balance")

                                Text(
                                    "₹ $balance",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .headlineLarge
                                )

                            }

                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        // CHART
                        if (totalIncome!=0 || totalExpense!=0) {

                            CustomPieChart(

                                income =
                                    totalIncome.toFloat(),

                                expense =
                                    totalExpense.toFloat()

                            )

                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Text("Transactions")

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        LazyColumn {

                            itemsIndexed(transactions)
                            { index,txn ->

                                Card(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical=4.dp),

                                    onClick = {

                                        editIndex=index

                                        amount=
                                            txn.amount.toString()

                                        isIncome=txn.isIncome

                                        selectedCategory=
                                            txn.category

                                        selectedDate=
                                            txn.date

                                        showDialog=true

                                    }

                                ) {

                                    Row(

                                        modifier =
                                            Modifier
                                                .padding(12.dp)
                                                .fillMaxWidth(),

                                        horizontalArrangement =
                                            Arrangement.SpaceBetween

                                    ) {

                                        Column {

                                            Text(

                                                "${txn.category} • " +
                                                        if(txn.isIncome)
                                                            "Income"
                                                        else
                                                            "Expense"

                                            )

                                            Text("₹ ${txn.amount}")

                                            Text(
                                                txn.date,
                                                style =
                                                    MaterialTheme
                                                        .typography
                                                        .bodySmall
                                            )

                                        }

                                        IconButton(

                                            onClick = {

                                                transactions
                                                    .removeAt(index)

                                            }

                                        ) {

                                            Icon(
                                                Icons.Default.Delete,
                                                "delete"
                                            )

                                        }

                                    }

                                }

                            }

                        }

                        // DIALOG
                        if(showDialog){

                            AlertDialog(

                                onDismissRequest = {

                                    showDialog=false

                                },

                                title = {

                                    Text("Add Transaction")

                                },

                                text = {

                                    Column {

                                        OutlinedTextField(

                                            value = amount,

                                            onValueChange = {

                                                amount=it

                                            },

                                            label = {

                                                Text("Amount")

                                            }

                                        )

                                        Spacer(
                                            Modifier.height(8.dp)
                                        )

                                        // DATE BUTTON
                                        Button(

                                            onClick = {

                                                datePicker.show()

                                            }

                                        ) {

                                            Text(

                                                if(selectedDate=="")
                                                    "Select Date"
                                                else
                                                    selectedDate

                                            )

                                        }

                                        Spacer(
                                            Modifier.height(8.dp)
                                        )

                                        Text("Category")

                                        Row {

                                            categories.forEach{cat->

                                                FilterChip(

                                                    selected =
                                                        selectedCategory==cat,

                                                    onClick = {

                                                        selectedCategory=cat

                                                    },

                                                    label = {

                                                        Text(cat)

                                                    }

                                                )

                                            }

                                        }

                                        Spacer(
                                            Modifier.height(8.dp)
                                        )

                                        Row {

                                            Button(

                                                onClick = {

                                                    isIncome=true

                                                },

                                                modifier =
                                                    Modifier.weight(1f)

                                            ){

                                                Text("Income")

                                            }

                                            Spacer(
                                                Modifier.width(8.dp)
                                            )

                                            Button(

                                                onClick = {

                                                    isIncome=false

                                                },

                                                modifier =
                                                    Modifier.weight(1f)

                                            ){

                                                Text("Expense")

                                            }

                                        }

                                    }

                                },

                                confirmButton = {

                                    Button(

                                        onClick = {

                                            val amt =
                                                amount.toIntOrNull()
                                                    ?: return@Button

                                            if(selectedDate=="")
                                                return@Button

                                            if(editIndex!=-1){

                                                transactions[editIndex] =

                                                    Transaction(

                                                        amt,

                                                        isIncome,

                                                        selectedCategory,

                                                        selectedDate

                                                    )

                                            }

                                            else{

                                                transactions.add(

                                                    Transaction(

                                                        amt,

                                                        isIncome,

                                                        selectedCategory,

                                                        selectedDate

                                                    )

                                                )

                                            }

                                            showDialog=false

                                        }

                                    ){

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

// CUSTOM CHART
@Composable
fun CustomPieChart(
    income:Float,
    expense:Float
){

    val total = income + expense

    val incomeAngle =
        (income/total)*360f

    val expenseAngle =
        (expense/total)*360f

    Canvas(
        modifier = Modifier.size(200.dp)
    ){

        var startAngle=0f

        drawArc(

            color = Color(0xFF4CAF50),

            startAngle=startAngle,

            sweepAngle=incomeAngle,

            useCenter=true,

            size = Size(size.width,size.height)

        )

        startAngle+=incomeAngle

        drawArc(

            color = Color(0xFFF44336),

            startAngle=startAngle,

            sweepAngle=expenseAngle,

            useCenter=true,

            size = Size(size.width,size.height)

        )

    }

}