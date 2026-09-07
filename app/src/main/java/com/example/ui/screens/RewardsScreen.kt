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
import com.example.domain.model.CashbackRewardItem
import com.example.domain.model.LedgerStatus
import com.example.domain.model.PaymentMode
import com.example.ui.components.ModeBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RewardsScreen(
    currentMode: PaymentMode,
    rewardBalance: Double,
    pendingBalance: Double,
    cashbackLedger: List<CashbackRewardItem>,
    onModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasScratched by remember { mutableStateOf(false) }

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
                    text = "Cashback & Rewards",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                ModeBadge(mode = currentMode, onClick = onModeClick)
            }
        }

        // Ledger Balance Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E232E), Color(0xFF161B22))
                        )
                    )
                    .testTag("rewards_ledger_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Promotional Rewards Ledger", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Double-Entry Validated", color = ElegantGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${"%.2f".format(rewardBalance)}",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantSurfaceVariant)
                            .border(1.dp, ElegantBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Pending Maturity", color = TextSecondary, fontSize = 10.sp)
                            Text("₹${"%.2f".format(pendingBalance)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Expiry Policy", color = TextSecondary, fontSize = 10.sp)
                            Text("90 Days Active", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Redemption", color = TextSecondary, fontSize = 10.sp)
                            Text("Recharges / Bill Pay", color = ElegantBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Disclaimer: Cashback balances are promotional platform rewards and do NOT constitute bank deposits or escrowed funds.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Promotional Scratch Card Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!hasScratched) Color(0xFF281E0E) else CardSurface
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MoonGold.copy(alpha = 0.5f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { hasScratched = true }
                    .testTag("scratch_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!hasScratched) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MoonGold, MoonGoldDark)))
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to Scratch & Reveal", color = MoonGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Promotional reward for your verified UPI transaction", color = TextSecondary, fontSize = 12.sp)
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🎉 You Won ₹25.00 Cashback!", color = SuccessGreen, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Recorded in ledger: CB_PROMO_VERIFIED", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Active Admin Campaigns
        item {
            Text(
                text = "Active Admin Campaigns & Rules",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("First Payment Delight", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("5% up to ₹50", color = MoonGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text("Min transaction: ₹100 • Eligible: Grocery, Food, Recharge • Controlled by Admin Engine", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderStroke)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Merchant Super Saver", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Flat ₹25", color = EmeraldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text("Min transaction: ₹500 • Partner retail merchants • Subject to daily campaign budget", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Immutable Ledger Activity
        item {
            Text(
                text = "Double-Entry Ledger Transactions",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (cashbackLedger.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No rewards entries yet", color = TextSecondary, fontSize = 13.sp)
                        Text("Make eligible UPI payments above ₹100 to trigger cashback", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(cashbackLedger) { item ->
                LedgerRowItem(item = item)
            }
        }
    }
}

@Composable
fun LedgerRowItem(item: CashbackRewardItem) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.createdAt))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.campaignName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(
                        text = item.status.name,
                        color = if (item.status == LedgerStatus.AVAILABLE) ElegantGreen else MoonGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (item.status == LedgerStatus.AVAILABLE) Color(0x2600C853) else Color(0x26FFB74D))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Text(
                    text = "Ref: ${item.id.take(12)}... • $formattedDate",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Text(
                text = "+₹${"%.2f".format(item.amount)}",
                color = ElegantGreen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
    }
}
