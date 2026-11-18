package com.miahshihab.mymoney.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miahshihab.mymoney.data.Loan
import com.miahshihab.mymoney.data.LoanStatus
import com.miahshihab.mymoney.data.LoanType
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Lent (Receivables), 1: Borrowed (Payables)
    var showAddDialog by remember { mutableStateOf(false) }

    // Sample active mock list for demonstration
    val loansList = remember {
        mutableStateListOf(
            Loan(
                id = 1,
                counterpartyName = "John Doe",
                counterpartyContact = "+1 555-0199",
                totalAmount = 500.0,
                remainingAmount = 250.0,
                loanType = LoanType.LENT,
                linkedAccountId = 1,
                status = LoanStatus.PARTIALLY_PAID,
                notes = "Personal advance for project"
            ),
            Loan(
                id = 2,
                counterpartyName = "Alex Smith",
                counterpartyContact = "+1 555-0144",
                totalAmount = 300.0,
                remainingAmount = 300.0,
                loanType = LoanType.BORROWED,
                linkedAccountId = 1,
                status = LoanStatus.ACTIVE,
                notes = "Equipment purchase share"
            )
        )
    }

    val currentType = if (selectedTab == 0) LoanType.LENT else LoanType.BORROWED
    val filteredLoans = loansList.filter { it.loanType == currentType }
    val totalBalance = filteredLoans.sumOf { it.remainingAmount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loans & Debts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("← Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", fontSize = 24.sp, color = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Lent (Owed to Me)") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Borrowed (I Owe)") }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTab == 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedTab == 0) "Total Receivables" else "Total Liabilities",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale.US).format(totalBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            if (filteredLoans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No records found. Tap '+' to create one.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredLoans) { loan ->
                        LoanCard(loan = loan, onRepayClick = {
                            // Update remaining amount or settle
                            val index = loansList.indexOf(loan)
                            if (index != -1) {
                                loansList[index] = loan.copy(
                                    remainingAmount = 0.0,
                                    status = LoanStatus.SETTLED
                                )
                            }
                        })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLoanDialog(
            loanType = currentType,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount, note ->
                loansList.add(
                    Loan(
                        id = System.currentTimeMillis(),
                        counterpartyName = name,
                        totalAmount = amount,
                        remainingAmount = amount,
                        loanType = currentType,
                        linkedAccountId = 1,
                        notes = note
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LoanCard(loan: Loan, onRepayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(loan.counterpartyName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (loan.counterpartyContact.isNotEmpty()) {
                        Text(loan.counterpartyContact, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                SuggestionChip(
                    onClick = {},
                    label = { Text(loan.status.name, fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Remaining:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        NumberFormat.getCurrencyInstance(Locale.US).format(loan.remainingAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (loan.loanType == LoanType.LENT) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
                if (loan.status != LoanStatus.SETTLED) {
                    Button(onClick = onRepayClick) {
                        Text("Settle Up")
                    }
                }
            }

            if (loan.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(loan.notes, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun AddLoanDialog(
    loanType: LoanType,
    onDismiss: () -> Unit,
    onConfirm: (name: String, amount: Double, note: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (loanType == LoanType.LENT) "Record New Lent Loan" else "Record New Borrowed Debt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Person / Counterparty Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Description") },
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amount > 0) {
                        onConfirm(name, amount, note)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
