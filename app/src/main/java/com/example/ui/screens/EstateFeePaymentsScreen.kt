package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.EstateFeeInvoiceEntity
import com.example.data.local.entities.ResidentAccountEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun EstateFeePaymentsScreen(
    isAdmin: Boolean,
    residentUnit: String,
    allResidents: List<ResidentAccountEntity>,
    invoices: List<EstateFeeInvoiceEntity>,
    onPayInvoice: (invoice: EstateFeeInvoiceEntity, method: String) -> Unit,
    onCreateInvoice: (resident: ResidentAccountEntity, title: String, category: String, amount: Double, period: String, dueDays: Int) -> Unit
) {
    var selectedInvoiceToPay by remember { mutableStateOf<EstateFeeInvoiceEntity?>(null) }
    var selectedReceiptToView by remember { mutableStateOf<EstateFeeInvoiceEntity?>(null) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    val relevantInvoices = if (isAdmin) {
        invoices
    } else {
        invoices.filter { it.unitNumber == residentUnit || it.unitNumber.isEmpty() }
    }

    val totalPending = relevantInvoices.filter { it.status == "PENDING" }.sumOf { it.amount }
    val totalSettled = relevantInvoices.filter { it.status == "PAID" }.sumOf { it.amount }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Balance Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAdmin) "ESTATE TREASURY REVENUE" else "RESIDENCE LEVIES & SERVICE CHARGE",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "₦%,.2f".format(if (isAdmin) totalSettled else totalPending),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )

                    Text(
                        text = if (isAdmin) "Total Settled Invoices Across Estate" else "Outstanding Total Due for $residentUnit",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Pending Dues", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("₦%,.2f".format(totalPending), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Settled Total", color = Color(0xFF86EFAC), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("₦%,.2f".format(totalSettled), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "INVOICES & ASSESSMENT DUES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = GushedTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (relevantInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = GushedCobalt.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All levies and estate dues are fully settled!", color = GushedTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(relevantInvoices) { invoice ->
                        InvoiceItemCard(
                            invoice = invoice,
                            onPay = { selectedInvoiceToPay = invoice },
                            onViewReceipt = { selectedReceiptToView = invoice }
                        )
                    }
                }
            }
        }

        // Admin Create Invoice Button
        if (isAdmin) {
            FloatingActionButton(
                onClick = { showCreateInvoiceDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = GushedCobalt,
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Create Levy")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Issue Levy Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Interactive Checkout Dialog
    selectedInvoiceToPay?.let { inv ->
        PaymentCheckoutDialog(
            invoice = inv,
            onDismiss = { selectedInvoiceToPay = null },
            onConfirmPay = { method ->
                onPayInvoice(inv, method)
                selectedInvoiceToPay = null
            }
        )
    }

    // Receipt View Dialog
    selectedReceiptToView?.let { inv ->
        ReceiptViewDialog(
            invoice = inv,
            onDismiss = { selectedReceiptToView = null }
        )
    }

    // Admin Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            residents = allResidents,
            onDismiss = { showCreateInvoiceDialog = false },
            onCreate = { res, title, cat, amt, per, days ->
                onCreateInvoice(res, title, cat, amt, per, days)
                showCreateInvoiceDialog = false
            }
        )
    }
}

@Composable
fun InvoiceItemCard(
    invoice: EstateFeeInvoiceEntity,
    onPay: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val isPaid = invoice.status == "PAID"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.feeTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                    Text(
                        text = "${invoice.period} • ${invoice.residentName} (${invoice.unitNumber})",
                        style = MaterialTheme.typography.bodySmall,
                        color = GushedTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPaid) GushedEmeraldApproved.copy(alpha = 0.12f) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (isPaid) "SETTLED" else "PENDING DUE",
                        color = if (isPaid) GushedEmeraldApproved else Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AMOUNT", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(
                        text = "₦%,.2f".format(invoice.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                }

                if (isPaid) {
                    Button(
                        onClick = onViewReceipt,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Receipt", color = GushedCobalt, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay Now", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCheckoutDialog(
    invoice: EstateFeeInvoiceEntity,
    onDismiss: () -> Unit,
    onConfirmPay: (String) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("DEBIT_CARD") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
            modifier = Modifier.border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pay Estate Assessment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(invoice.feeTitle, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(
                            "₦%,.2f".format(invoice.amount),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Resident: ${invoice.residentName} (${invoice.unitNumber})", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("CHOOSE PAYMENT METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                listOf(
                    "DEBIT_CARD" to "Debit Card (Mastercard / Visa / Verve)",
                    "BANK_TRANSFER" to "Instant Estate Dedicated Account Transfer",
                    "ESTATE_WALLET" to "Resident Digital Security Wallet"
                ).forEach { (key, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (paymentMethod == key) GushedCobalt.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { paymentMethod = key }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (key == "DEBIT_CARD") Icons.Default.CreditCard else Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (paymentMethod == key) GushedCobalt else GushedTextSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(name, fontSize = 12.sp, fontWeight = if (paymentMethod == key) FontWeight.Bold else FontWeight.Normal)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onConfirmPay(paymentMethod) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Authorize ₦%,.2f Payment".format(invoice.amount), fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReceiptViewDialog(
    invoice: EstateFeeInvoiceEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
            modifier = Modifier.border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = GushedEmeraldApproved.copy(alpha = 0.12f),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GushedEmeraldApproved, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("OFFICIAL ESTATE LEVY RECEIPT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(invoice.receiptNumber, color = GushedCobalt, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Resident:", fontSize = 12.sp, color = GushedTextSecondary)
                            Text("${invoice.residentName} (${invoice.unitNumber})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Assessment:", fontSize = 12.sp, color = GushedTextSecondary)
                            Text(invoice.feeTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Period:", fontSize = 12.sp, color = GushedTextSecondary)
                            Text(invoice.period, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Settlement Method:", fontSize = 12.sp, color = GushedTextSecondary)
                            Text(invoice.paymentMethod.replace("_", " "), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid:", fontSize = 12.sp, color = GushedTextSecondary)
                            Text("₦%,.2f".format(invoice.amount), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GushedEmeraldApproved)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Text("Close Receipt", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CreateInvoiceDialog(
    residents: List<ResidentAccountEntity>,
    onDismiss: () -> Unit,
    onCreate: (resident: ResidentAccountEntity, title: String, category: String, amount: Double, period: String, dueDays: Int) -> Unit
) {
    var selectedResident by remember { mutableStateOf(residents.firstOrNull()) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("SERVICE_CHARGE") }
    var amountStr by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("Q3 2026") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
            modifier = Modifier.border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Issue Levy Invoice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assessment Title") },
                    placeholder = { Text("e.g. Estate Solar Streetlight Levy") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₦)") },
                    placeholder = { Text("e.g. 50000") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = period,
                    onValueChange = { period = it },
                    label = { Text("Billing Cycle / Period") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val resident = selectedResident ?: residents.firstOrNull()
                        val amt = amountStr.toDoubleOrNull() ?: 0.0
                        if (resident != null && title.isNotBlank() && amt > 0) {
                            onCreate(resident, title.trim(), category, amt, period.trim(), 14)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Text("Issue Invoice to Resident", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
