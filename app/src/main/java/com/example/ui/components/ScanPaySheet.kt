package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun ScanPaySheet(
    onDismiss: () -> Unit,
    onPaymentConfirmed: (vpa: String, name: String, amount: Double, note: String) -> Unit
) {
    var detectedVpa by remember { mutableStateOf<String?>("starbucks.retail@bankupi") }
    var detectedName by remember { mutableStateOf<String?>("Starbucks Coffee") }
    var amountText by remember { mutableStateOf("280") }
    var note by remember { mutableStateOf("Coffee order") }
    var isSimulatingScan by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Scan Any UPI QR Code", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scanner Viewfinder Area
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF060A10))
                        .border(2.dp, EmeraldPrimary, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = if (isSimulatingScan) ElectricCyan else EmeraldLight,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isSimulatingScan) "Scanning..." else "Align QR code within frame",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Demo Sample QR Chips
                Text("Simulate Merchant QR Scan:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = {
                            detectedVpa = "starbucks.retail@bankupi"
                            detectedName = "Starbucks Coffee"
                            amountText = "280"
                            note = "Cappuccino & Pastry"
                        },
                        label = { Text("Starbucks", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = {
                            detectedVpa = "freshgrocer.merchant@okhdfc"
                            detectedName = "Fresh Groceries Supermarket"
                            amountText = "750"
                            note = "Grocery Essentials"
                        },
                        label = { Text("Groceries", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payee Confirmation Card
                detectedVpa?.let { vpa ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Text(detectedName ?: "Verified Merchant", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Text(vpa, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))

                            Spacer(modifier = Modifier.height(8.dp))

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
                                modifier = Modifier.fillMaxWidth().testTag("scan_pay_amount_input")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 100.0
                        if (detectedVpa != null) {
                            onPaymentConfirmed(detectedVpa!!, detectedName ?: detectedVpa!!, amt, note)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("scan_confirm_pay_btn")
                ) {
                    Text("Proceed to Pay", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
