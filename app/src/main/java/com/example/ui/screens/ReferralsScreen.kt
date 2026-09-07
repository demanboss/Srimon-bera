package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PaymentMode
import com.example.domain.model.ReferralInfo
import com.example.ui.components.ModeBadge
import com.example.ui.theme.*

@Composable
fun ReferralsScreen(
    referralInfo: ReferralInfo,
    currentMode: PaymentMode,
    onModeClick: () -> Unit,
    onCopyCode: () -> Unit,
    onShareLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    var copiedToast by remember { mutableStateOf(false) }

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
                    text = "Refer & Earn",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                ModeBadge(mode = currentMode, onClick = onModeClick)
            }
        }

        // Hero Referral Code Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorder)),
                modifier = Modifier.fillMaxWidth().testTag("referral_hero_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your Unique Referral Code", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantSurfaceVariant)
                            .border(1.dp, ElegantBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                copiedToast = true
                                onCopyCode()
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = referralInfo.referralCode,
                            color = ElegantBlue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElegantBlue, modifier = Modifier.size(18.dp))
                    }

                    if (copiedToast) {
                        Text("Code copied to clipboard!", color = ElegantGreen, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onShareLink,
                        colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("share_referral_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Referral Link", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Referral Stats Grid
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Referral Performance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatTile(value = referralInfo.totalInvited.toString(), label = "Invited", color = Color.White)
                        StatTile(value = referralInfo.registered.toString(), label = "Registered", color = ElegantBlue)
                        StatTile(value = referralInfo.qualifiedCount.toString(), label = "Qualified", color = ElegantGreen)
                        StatTile(value = "₹${referralInfo.totalRewardsEarned.toInt()}", label = "Earned", color = ElegantGreen)
                    }
                }
            }
        }

        // How Referral Works (Compliance Qualification Ladder)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Referral Qualification Ladder", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    StepRow(step = "1", title = "Share Invite Link", desc = "Send your link or referral code to friends.")
                    StepRow(step = "2", title = "Friend Signs Up", desc = "Your friend completes mobile verification and links their bank.")
                    StepRow(step = "3", title = "First Qualifying Payment", desc = "Friend completes an eligible transaction of min ₹199.")
                    StepRow(step = "4", title = "Earn up to ₹500 Reward", desc = "Both you and your friend receive promotional cashback rewards.")
                }
            }
        }

        // Anti-Abuse & Legal Disclosure
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MoonGold, modifier = Modifier.size(22.dp))
                    Column {
                        Text("Terms & Anti-Fraud Policy", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "Self-referrals, emulator spoofing, and multi-accounting violate terms and trigger automated reward forfeiture. Qualifying transactions must be legitimate P2P or merchant payments.",
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
fun StatTile(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun StepRow(step: String, title: String, desc: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.2f))
                .border(1.dp, EmeraldPrimary, CircleShape)
        ) {
            Text(step, color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Column {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(desc, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
