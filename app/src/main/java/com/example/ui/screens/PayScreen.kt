package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PaymentMode
import com.example.ui.components.ModeBadge
import com.example.ui.theme.*

@Composable
fun PayScreen(
    currentMode: PaymentMode,
    onModeClick: () -> Unit,
    onScanPayClick: () -> Unit,
    onUpiIdClick: () -> Unit,
    onMyQrClick: () -> Unit,
    onUpiLiteClick: () -> Unit,
    onPayContact: (vpa: String, name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPayees = listOf(
        Pair("Starbucks Coffee", "starbucks.demo@bankupi"),
        Pair("BigBazaar Supermarket", "bigbazaar.demo@bankupi"),
        Pair("Swiggy Delivery", "swiggy.demo@bankupi"),
        Pair("Rahul Verma", "rahul.verma@oksbi"),
        Pair("Priya Patel", "priyapatel@okicici")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transfer & Pay",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                ModeBadge(mode = currentMode, onClick = onModeClick)
            }
        }

        // Primary Scan & Pay Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onScanPayClick)
                    .testTag("pay_screen_scan_card")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ElegantBlue)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Scan Any UPI QR Code", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Pay at stores, restaurants, or scan recipient's QR", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        // Payment Methods Grid
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment Options", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        PaymentOptionTile(
                            icon = Icons.Default.AlternateEmail,
                            title = "To UPI ID",
                            subtitle = "Any UPI handle",
                            tint = ElegantBlue,
                            onClick = onUpiIdClick,
                            testTag = "pay_option_upi_id"
                        )
                        PaymentOptionTile(
                            icon = Icons.Default.QrCode,
                            title = "Receive (My QR)",
                            subtitle = "Show your QR",
                            tint = ElegantBlue,
                            onClick = onMyQrClick,
                            testTag = "pay_option_my_qr"
                        )
                        PaymentOptionTile(
                            icon = Icons.Default.Bolt,
                            title = "UPI Lite",
                            subtitle = "PIN-less wallet",
                            tint = ElegantGreen,
                            onClick = onUpiLiteClick,
                            testTag = "pay_option_upi_lite"
                        )
                    }
                }
            }
        }

        // Quick Payees
        item {
            Text(
                text = "Frequent & Verified Payees",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(quickPayees) { (name, vpa) ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPayContact(vpa, name) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x332D60FF))
                        ) {
                            Text(name.take(1), color = ElegantBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text(name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(vpa, color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { onPayContact(vpa, name) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Pay", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // NPCI Compliance Security Callout
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                    Column {
                        Text("NPCI Security Compliance", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "UPI PIN is required ONLY to debit funds and must never be shared or entered in non-NPCI screens.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = TextSecondary, fontSize = 10.sp)
    }
}
