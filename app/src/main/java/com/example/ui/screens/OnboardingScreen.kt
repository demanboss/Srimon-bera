package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.OnboardingStep

@Composable
fun OnboardingScreen(
    currentStep: OnboardingStep,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    otpInput: String,
    onOtpChange: (String) -> Unit,
    nameInput: String,
    onNameChange: (String) -> Unit,
    referralInput: String,
    onReferralChange: (String) -> Unit,
    onGetOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onCompleteProfile: () -> Unit,
    onAcceptConsentAndFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantCanvas)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (currentStep) {
            is OnboardingStep.Intro -> {
                IntroStep(onGetStarted = onGetOtp)
            }
            is OnboardingStep.PhoneInput -> {
                PhoneInputStep(
                    phone = phoneInput,
                    onPhoneChange = onPhoneChange,
                    onContinue = onGetOtp
                )
            }
            is OnboardingStep.OtpVerification -> {
                OtpVerificationStep(
                    phone = phoneInput,
                    otp = otpInput,
                    onOtpChange = onOtpChange,
                    onVerify = onVerifyOtp
                )
            }
            is OnboardingStep.ProfileInput -> {
                ProfileSetupStep(
                    name = nameInput,
                    onNameChange = onNameChange,
                    referral = referralInput,
                    onReferralChange = onReferralChange,
                    onContinue = onCompleteProfile
                )
            }
            is OnboardingStep.LegalConsent -> {
                LegalConsentStep(onAccept = onAcceptConsentAndFinish)
            }
            else -> {}
        }
    }
}

@Composable
fun IntroStep(onGetStarted: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(ElegantBlue, Color(0xFF161B22))))
                .border(2.dp, ElegantBlue.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "MOON Elite",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 1.sp
        )
        Text(
            text = "Compliance-First UPI Payments & Rewards",
            fontSize = 14.sp,
            color = ElegantBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Experience seamless payments across India with instant verified promotional cashback, double-entry ledger security, and NPCI-compliant TPAP rails.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onGetStarted,
            colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("intro_get_started_btn")
        ) {
            Text("Get Started", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun PhoneInputStep(
    phone: String,
    onPhoneChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Enter Mobile Number", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "We'll send a one-time verification code via SMS to verify your bank-linked mobile number.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Mobile Number") },
            singleLine = true,
            leadingIcon = {
                Text("🇮🇳", fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = ElegantBlue,
                unfocusedBorderColor = ElegantBorderSubtle,
                focusedContainerColor = ElegantSurface,
                unfocusedContainerColor = ElegantSurface
            ),
            modifier = Modifier.fillMaxWidth().testTag("onboarding_phone_input")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("get_otp_btn")
        ) {
            Text("Send Verification OTP", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OtpVerificationStep(
    phone: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Verify OTP Code", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Enter the 6-digit code sent to $phone",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) onOtpChange(it) },
            label = { Text("6-Digit OTP (e.g. 123456)") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = ElegantBlue,
                unfocusedBorderColor = ElegantBorderSubtle,
                focusedContainerColor = ElegantSurface,
                unfocusedContainerColor = ElegantSurface
            ),
            modifier = Modifier.fillMaxWidth().testTag("onboarding_otp_input")
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expires in 1:45", color = TextSecondary, fontSize = 12.sp)
            Text("Resend Code", color = ElegantBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onOtpChange("123456") })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerify,
            colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("verify_otp_btn")
        ) {
            Text("Verify & Continue", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileSetupStep(
    name: String,
    onNameChange: (String) -> Unit,
    referral: String,
    onReferralChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Profile Setup", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Set your display name and enter an optional referral code to unlock welcome rewards.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Legal Name (as in bank records)") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = ElegantBlue,
                unfocusedBorderColor = ElegantBorderSubtle,
                focusedContainerColor = ElegantSurface,
                unfocusedContainerColor = ElegantSurface
            ),
            modifier = Modifier.fillMaxWidth().testTag("onboarding_name_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = referral,
            onValueChange = onReferralChange,
            label = { Text("Referral Code (Optional, e.g. MOON777)") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = ElegantBlue,
                unfocusedBorderColor = ElegantBorderSubtle,
                focusedContainerColor = ElegantSurface,
                unfocusedContainerColor = ElegantSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("profile_continue_btn")
        ) {
            Text("Continue to Consent", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LegalConsentStep(onAccept: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Consent & Disclosures", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Please review and agree to our compliance terms to activate your MOON Elite account.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElegantBorderSubtle)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("1. UPI & TPAP Operations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("MOON Elite facilitates payments exclusively via authorized partner PSP banks or in sandbox mode. We never store or capture your secret UPI PIN.", color = TextSecondary, fontSize = 11.sp)

                Divider(color = ElegantBorderSubtle)

                Text("2. Rewards Balance Disclosure", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Cashback and reward points are promotional non-bank credits governed by promotional campaign terms, not interest-bearing deposits.", color = TextSecondary, fontSize = 11.sp)

                Divider(color = ElegantBorderSubtle)

                Text("3. Privacy (DPDP Act & PMLA)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("By continuing, you agree to our Privacy Policy and acknowledge that statutory transaction audit logs are retained for 5 years per RBI guidelines.", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(containerColor = ElegantBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("accept_terms_btn")
        ) {
            Text("I Agree & Enter MOON Elite", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
