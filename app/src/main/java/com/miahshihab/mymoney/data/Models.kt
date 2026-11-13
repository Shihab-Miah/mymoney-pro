package com.miahshihab.mymoney.data

data class Account(
    val id: Long = 0,
    val name: String,
    val initialBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val currency: String = "USD",
    val colorHex: String = "#1E88E5",
    val icon: String = "wallet",
    val isExcluded: Boolean = false
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

data class TransactionRecord(
    val id: Long = 0,
    val accountId: Long,
    val toAccountId: Long? = null,
    val categoryId: Long,
    val amount: Double,
    val type: TransactionType,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val photoUri: String? = null
)

data class Category(
    val id: Long = 0,
    val name: String,
    val iconKey: String = "category",
    val colorHex: String = "#43A047",
    val type: TransactionType = TransactionType.EXPENSE
)

enum class LoanType {
    LENT,      // Money I lent to someone (they owe me)
    BORROWED   // Money I borrowed from someone (I owe them)
}

enum class LoanStatus {
    ACTIVE,
    PARTIALLY_PAID,
    SETTLED,
    OVERDUE
}

data class Loan(
    val id: Long = 0,
    val counterpartyName: String,
    val counterpartyContact: String = "",
    val totalAmount: Double,
    val remainingAmount: Double,
    val loanType: LoanType,
    val linkedAccountId: Long,
    val startDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val status: LoanStatus = LoanStatus.ACTIVE,
    val notes: String = ""
)

data class LoanPayment(
    val id: Long = 0,
    val loanId: Long,
    val amount: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val linkedAccountId: Long,
    val note: String = ""
)
