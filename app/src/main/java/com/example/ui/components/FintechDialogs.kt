package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.PaymentMode
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionStatus
import com.example.ui.theme.*

@Composable
fun ModeBadge(
    mode: PaymentMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSandbox = mode == PaymentMode.SANDBOX
    val bgColor = if (isSandbox) SandboxAmberBg else Color(0x2600E676)
    val textColor = if (isSandbox) SandboxAmber else SuccessGreen
    val borderColor = if (isSandbox) SandboxAmber.copy(alpha = 0.5f) else SuccessGreen.copy(alpha = 0.5f)
    val text = if (isSandbox) "SANDBOX / DEMO" else "PRODUCTION UPI"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("mode_badge")
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Mode Information",
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun ModeSwitcherDialog(
    currentMode: PaymentMode,
    onSelectMode: (PaymentMode) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MoonGold)
                    Text("Payment Environment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "MOON Elite enforces a strict legal compliance architecture. Never display simulated sandbox transactions as real financial transactions.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Option: Sandbox
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentMode == PaymentMode.SANDBOX) SandboxAmberBg else CardSurfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (currentMode == PaymentMode.SANDBOX) SandboxAmber else BorderStroke)
                    ),
                    modifier = Modifier.fillMaxWidth().clickable { onSelectMode(PaymentMode.SANDBOX) }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentMode == PaymentMode.SANDBOX,
                            onClick = { onSelectMode(PaymentMode.SANDBOX) },
                            colors = RadioButtonDefaults.colors(selectedColor = SandboxAmber)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text("MODE A: SANDBOX / DEMO", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text("Used during development & audit. UPI operations are safely simulated. No real funds debited.", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Option: Production
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentMode == PaymentMode.PRODUCTION) Color(0x2600E676) else CardSurfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (currentMode == PaymentMode.PRODUCTION) SuccessGreen else BorderStroke)
                    ),
                    modifier = Modifier.fillMaxWidth().clickable { onSelectMode(PaymentMode.PRODUCTION) }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentMode == PaymentMode.PRODUCTION,
                            onClick = { onSelectMode(PaymentMode.PRODUCTION) },
                            colors = RadioButtonDefaults.colors(selectedColor = SuccessGreen)
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text("MODE B: PRODUCTION UPI", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text("Requires authorized PSP/TPAP partner & NPCI onboarding. Live banking rails.", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Apply & Close", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UpiIdPaymentDialog(
    onDismiss: () -> Unit,
    onPay: (vpa: String, name: String, amount: Double, note: String) -> Unit
) {
    var vpa by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = EmeraldLight)
                    Text("Pay via UPI ID / VPA", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = vpa,
                    onValueChange = {
                        vpa = it
                        if (it.contains("@")) {
                            name = "Verified Payee (" + it.substringBefore("@") + ")"
                        }
                    },
                    label = { Text("Recipient UPI ID (e.g. name@okhdfcbank)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderStroke
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("upi_id_input")
                )

                if (name.isNotBlank()) {
                    Text(
                        text = "Name: $name",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount (₹)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderStroke
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("amount_input")
                )

                // Quick amount chips
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("100", "500", "1000", "2000").forEach { quickAmount ->
                        AssistChip(
                            onClick = { amountText = quickAmount },
                            label = { Text("₹$quickAmount", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = CardSurfaceVariant, labelColor = TextPrimary)
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add a note (optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderStroke
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(it, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (vpa.isBlank() || !vpa.contains("@")) {
                                errorMessage = "Please enter a valid UPI ID with @ symbol."
                            } else if (amt == null || amt <= 0) {
                                errorMessage = "Please enter a valid payment amount."
                            } else {
                                onPay(vpa.trim(), if (name.isNotBlank()) name else vpa, amt, note.trim())
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(46.dp).testTag("confirm_pay_button")
                    ) {
                        Text("Pay Now", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionResultDialog(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val isSandbox = transaction.paymentMode == PaymentMode.SANDBOX

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Icon with glow ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0x2600E676))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SuccessGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSandbox) {
                    Text(
                        text = "DEMO / SANDBOX TRANSACTION",
                        color = SandboxAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "₹${"%.2f".format(transaction.amount)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Text(
                    text = "Paid to ${transaction.recipientName}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cashback Awarded Card
                if (transaction.cashbackEarned > 0) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x1AFFD54F)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MoonGold.copy(alpha = 0.5f))),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MoonGold, modifier = Modifier.size(28.dp))
                            Column {
                                Text(
                                    text = "🎉 ₹${"%.2f".format(transaction.cashbackEarned)} Cashback Earned!",
                                    color = MoonGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Credited to Promotional Reward Ledger",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Receipt details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardSurfaceVariant)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceiptRow(label = "Payee UPI ID", value = transaction.recipientVpa)
                    ReceiptRow(label = "Sender Account", value = transaction.senderVpa)
                    ReceiptRow(label = "UPI Ref (RRN)", value = transaction.bankRrn ?: "426189918231")
                    ReceiptRow(label = "Transaction ID", value = transaction.id.take(16) + "...")
                    ReceiptRow(label = "Provider", value = if (isSandbox) "MOON Sandbox Gateway" else "NPCI TPAP Rails")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun UPILiteSheet(
    balance: Double,
    onTopUp: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = ElectricCyan)
                    Text("UPI Lite (On-Device Wallet)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pay instantly without UPI PIN for payments up to ₹500. Balance is held safely subject to partner bank availability.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Current UPI Lite Balance", color = TextSecondary, fontSize = 12.sp)
                        Text("₹${"%.2f".format(balance)}", color = ElectricCyan, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("Maximum wallet limit: ₹2,000", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Quick Top-Up", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(200.0, 500.0, 1000.0).forEach { topUpAmt ->
                        Button(
                            onClick = {
                                onTopUp(topUpAmt)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan.copy(alpha = 0.5f))),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+₹${topUpAmt.toInt()}", color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun AccountDeletionDialog(
    onConfirmRequest: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.5f))),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed)
                    Text("Account Deletion Request", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "What happens when you request account deletion?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• All Personally Identifiable Information (PII) such as your name, email, and phone number will be anonymized.\n" +
                           "• Active device tokens, logins, and linked app credentials will be immediately revoked.\n" +
                           "• Any unredeemed promotional rewards points will be forfeited.\n" +
                           "• LEGAL RETENTION NOTICE: In strict compliance with RBI Master Directions, NPCI Guidelines, and the Prevention of Money Laundering Act (PMLA), immutable transaction logs, banking references, and dispute records must be retained for 5 years by law.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            onConfirmRequest()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm Delete", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
