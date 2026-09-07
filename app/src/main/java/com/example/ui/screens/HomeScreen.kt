package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.OfferCampaign
import com.example.domain.model.PaymentMode
import com.example.domain.model.PaymentType
import com.example.domain.model.Transaction
import com.example.domain.model.UserProfile
import com.example.ui.components.ModeBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    currentMode: PaymentMode,
    transactions: List<Transaction>,
    offers: List<OfferCampaign>,
    onModeClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSupportClick: () -> Unit,
    onScanPayClick: () -> Unit,
    onSendMoneyClick: () -> Unit,
    onUpiIdClick: () -> Unit,
    onMyQrClick: () -> Unit,
    onRequestMoneyClick: () -> Unit,
    onUpiLiteClick: () -> Unit,
    onViewRewardsClick: () -> Unit,
    onShareReferralClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantCanvas)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(ElegantBlue, ElegantGreen)))
                            .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    ) {
                        val initials = userProfile.fullName
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .uppercase()
                        Text(
                            text = if (initials.isNotEmpty()) initials else "ME",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Column {
                        Text(
                            text = "GOOD EVENING",
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = userProfile.fullName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeBadge(mode = currentMode, onClick = onModeClick)

                    IconButton(
                        onClick = onSupportClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantSurface)
                            .border(1.dp, ElegantBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Support", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }

                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantSurface)
                            .border(1.dp, ElegantBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // 2. Hero Total Cashback Earned Card (Elegant Dark)
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
                    .clickable(onClick = onViewRewardsClick)
                    .testTag("rewards_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Total Cashback Earned",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "₹${"%.2f".format(userProfile.rewardPointsBalance)}",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2600C853))
                                .border(1.dp, Color(0x6600C853), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MOON ELITE",
                                color = ElegantGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ElegantGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Eligible for next payout (₹${"%.2f".format(userProfile.pendingCashbackBalance)} pending)",
                            color = ElegantGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Quick Action Grid (Scan & Pay Cobalt Hero + Elegant Dark Action Tiles)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ElegantQuickActionItem(
                        icon = Icons.Default.QrCodeScanner,
                        label = "Scan & Pay",
                        isPrimary = true,
                        onClick = onScanPayClick,
                        testTag = "action_scan_pay",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.Send,
                        label = "Send",
                        isPrimary = false,
                        onClick = onSendMoneyClick,
                        testTag = "action_send_money",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.AlternateEmail,
                        label = "UPI ID",
                        isPrimary = false,
                        onClick = onUpiIdClick,
                        testTag = "action_upi_id",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.QrCode,
                        label = "My QR",
                        isPrimary = false,
                        onClick = onMyQrClick,
                        testTag = "action_my_qr",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ElegantQuickActionItem(
                        icon = Icons.Default.CallReceived,
                        label = "Request",
                        isPrimary = false,
                        onClick = onRequestMoneyClick,
                        testTag = "action_request_money",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.Bolt,
                        label = "UPI Lite",
                        isPrimary = false,
                        onClick = onUpiLiteClick,
                        testTag = "action_upi_lite",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.CardGiftcard,
                        label = "Rewards",
                        isPrimary = false,
                        onClick = onViewRewardsClick,
                        testTag = "action_rewards_tab",
                        modifier = Modifier.weight(1f)
                    )
                    ElegantQuickActionItem(
                        icon = Icons.Default.GroupAdd,
                        label = "Refer",
                        isPrimary = false,
                        onClick = onShareReferralClick,
                        testTag = "action_refer_tab",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Referral Banner Card (Green Subtle Glow)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x1A00C853)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x3300C853))),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onShareReferralClick)
                    .testTag("referral_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300C853))
                        ) {
                            Icon(
                                Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = ElegantGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Refer & Earn ₹500",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Invite friends to Moon Elite",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onShareReferralClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("INVITE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        // 5. Offers Carousel
        item {
            Column {
                Text(
                    text = "Cashback Offers & Campaigns",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(offers) { offer ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorder)),
                            modifier = Modifier.width(220.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(offer.category.uppercase(), color = MoonGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(offer.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
                                Text(offer.discountDescription, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp), maxLines = 2)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Min txn: ₹${offer.minAmount.toInt()}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 6. Recent Verified Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "See All",
                    color = ElegantBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No transactions yet", color = TextSecondary, fontSize = 13.sp)
                        Text("Pay any merchant or UPI ID to start earning cashback", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(transactions) { txn ->
                TransactionRowItem(
                    transaction = txn,
                    onClick = { onTransactionClick(txn) }
                )
            }
        }
    }
}

@Composable
fun ElegantQuickActionItem(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isPrimary) ElegantBlue else ElegantSurface)
                .then(
                    if (!isPrimary) Modifier.border(1.dp, ElegantBorder, RoundedCornerShape(16.dp))
                    else Modifier
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) Color.White else ElegantBlue,
                modifier = Modifier.size(if (isPrimary) 26.dp else 22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    ElegantQuickActionItem(
        icon = icon,
        label = label,
        isPrimary = false,
        onClick = onClick,
        testTag = testTag
    )
}

@Composable
fun TransactionRowItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isSandbox = transaction.paymentMode == PaymentMode.SANDBOX
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.timestamp))

    val isFoodOrDrink = transaction.recipientName.contains("Swiggy", ignoreCase = true) ||
            transaction.recipientName.contains("Starbucks", ignoreCase = true) ||
            transaction.recipientName.contains("Bazaar", ignoreCase = true) ||
            transaction.paymentType == PaymentType.P2M_MERCHANT

    val avatarBg = if (isFoodOrDrink) Color(0x33FF9800) else Color(0x332D60FF)
    val avatarTextColor = if (isFoodOrDrink) Color(0xFFFF9800) else Color(0xFF2D60FF)
    val monogram = transaction.recipientName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifEmpty { "TX" }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ElegantSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(avatarBg)
                ) {
                    Text(
                        text = monogram,
                        color = avatarTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = transaction.recipientName,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        if (isSandbox) {
                            Text(
                                text = "DEMO",
                                color = SandboxAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SandboxAmberBg)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "UPI • $formattedDate",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-₹${"%.2f".format(transaction.amount)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (transaction.cashbackEarned > 0) {
                    Text(
                        text = "+₹${"%.2f".format(transaction.cashbackEarned)} Cashback",
                        color = ElegantGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
