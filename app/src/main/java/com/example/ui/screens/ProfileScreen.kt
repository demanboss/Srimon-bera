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
import com.example.domain.model.UserProfile
import com.example.ui.components.ModeBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    currentMode: PaymentMode,
    onModeClick: () -> Unit,
    onOpenUpiLite: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenAccountDeletion: () -> Unit,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "Profile & Settings",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                ModeBadge(mode = currentMode, onClick = onModeClick)
            }
        }

        // Profile Identity Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorder)),
                modifier = Modifier.fillMaxWidth().testTag("profile_identity_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x332D60FF))
                            .border(1.dp, ElegantBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Text(userProfile.fullName.take(1), color = ElegantBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(userProfile.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(userProfile.phoneNumber, color = TextSecondary, fontSize = 12.sp)
                        Text("VPA: ${userProfile.primaryVpa}", color = ElegantBlue, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x2600C853))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("KYC OK", color = ElegantGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Banking & Payment Rails Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Linked Bank Accounts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    ProfileItemRow(
                        icon = Icons.Default.AccountBalance,
                        title = "HDFC Bank (Primary)",
                        subtitle = "A/C •••• 4812 • arjunsharma@okhdfcbank",
                        tint = ElegantBlue,
                        onClick = {}
                    )

                    ProfileItemRow(
                        icon = Icons.Default.AccountBalance,
                        title = "State Bank of India",
                        subtitle = "A/C •••• 9134 • arjunsharma@oksbi",
                        tint = TextSecondary,
                        onClick = {}
                    )

                    Divider(color = ElegantBorderSubtle)

                    ProfileItemRow(
                        icon = Icons.Default.Bolt,
                        title = "UPI Lite Settings",
                        subtitle = "On-device wallet for instant PIN-less pay",
                        tint = ElegantGreen,
                        onClick = onOpenUpiLite
                    )
                }
            }
        }

        // Help & Support Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Help & Customer Support", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    ProfileItemRow(
                        icon = Icons.Default.SupportAgent,
                        title = "24/7 Dispute & Grievance Support",
                        subtitle = "Raise a ticket for failed transactions or cashback",
                        tint = MoonGold,
                        onClick = onOpenSupport
                    )

                    ProfileItemRow(
                        icon = Icons.Default.Email,
                        title = "Grievance Officer Contact",
                        subtitle = "grievance@moonelite.com (RBI Compliance)",
                        tint = TextSecondary,
                        onClick = {}
                    )
                }
            }
        }

        // Legal, Privacy & Compliance Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Legal & Compliance Disclosures", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    ProfileItemRow(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy (DPDP Act 2023)",
                        subtitle = "How financial data & device signals are protected",
                        tint = TextSecondary,
                        onClick = onOpenPrivacyPolicy
                    )

                    ProfileItemRow(
                        icon = Icons.Default.Description,
                        title = "Terms & Conditions & UPI Disclaimer",
                        subtitle = "TPAP disclosures and non-deposit reward rules",
                        tint = TextSecondary,
                        onClick = onOpenTerms
                    )

                    ProfileItemRow(
                        icon = Icons.Default.Tune,
                        title = "Payment Environment Switcher",
                        subtitle = "Current: ${currentMode.name} (NPCI Compliance)",
                        tint = if (currentMode == PaymentMode.SANDBOX) SandboxAmber else ElegantGreen,
                        onClick = onModeClick
                    )
                }
            }
        }

        // Danger Zone: Account Deletion
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElegantSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Account Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    ProfileItemRow(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete My Account",
                        subtitle = "Anonymize PII; statutory 5-yr PMLA transaction retention",
                        tint = ErrorRed,
                        onClick = onOpenAccountDeletion
                    )

                    ProfileItemRow(
                        icon = Icons.Default.Refresh,
                        title = "Replay App Onboarding",
                        subtitle = "Walk through mobile verification & consent flow",
                        tint = TextMuted,
                        onClick = onResetOnboarding
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("MOON Elite v1.0.0 (Build 100)", color = TextMuted, fontSize = 11.sp)
                Text("NPCI TPAP Compliance-First Architecture", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ProfileItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}
