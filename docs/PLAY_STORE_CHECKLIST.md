# MOON Elite — Google Play Store Deployment & Compliance Checklist

This step-by-step guide prepares the MOON Elite application for Google Play submission in compliance with Google Play Developer Policies and Financial Services regulations.

---

### Step 1: Google Play Developer Account
- [ ] Register organization developer account (D-U-N-S verified for enterprise entities).
- [ ] Complete identity verification with company legal registration documents.

### Step 2: App Identity & Metadata Review
- [ ] **App Title:** "MOON Elite" (30 characters or fewer, no buzzwords, no emojis).
- [ ] **Short Description:** "UPI Payments, Cashback Rewards & Offers" (Honest, accurate description).
- [ ] **Full Description:** Disclose that payment services operate in partnership with authorized payment entities or sandbox environment.
- [ ] **App Icon:** 512x512 PNG, 32-bit color.
- [ ] **Feature Graphic:** 1024x500 JPG/PNG highlighting key capabilities (UPI payments, offers, rewards).

### Step 3: Financial Features & Data Safety Declaration
- [ ] **Financial Features Declaration:** Complete the Google Play Financial Services form:
  - Select "Personal finance / Payments".
  - Disclose partner PSP Bank or Payment Aggregator credentials (or clearly state Sandbox stage if launching testing track).
- [ ] **Data Safety Form:**
  - Financial info: Disclose collection of Purchase history, User payment info (VPA/transaction ID).
  - Personal info: Name, Phone number, Email.
  - Device IDs: Device fingerprinting for fraud prevention.
  - Data encryption: All traffic transmitted over HTTPS/TLS; encrypted local storage.
  - Data deletion mechanism: Provide explicit in-app account deletion workflow and link.

### Step 4: Permissions & Hardware Access
- [ ] `android.permission.CAMERA`: Justified under "Scan & Pay" optical QR recognition.
- [ ] No broad external storage permissions requested (Zero-permission Photo Picker used if receipts uploaded).
- [ ] `android.permission.INTERNET` & `ACCESS_NETWORK_STATE` declared.

### Step 5: Android App Bundle (AAB) & Signing
- [ ] Generate production release key:
  ```bash
  keytool -genkey -v -keystore moon-elite-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias moonelite
  ```
- [ ] Build release bundle:
  ```bash
  gradle :app:bundleRelease
  ```
- [ ] Output artifact: `app/build/outputs/bundle/release/app-release.aab`

### Step 6: Testing Tracks & Staged Rollout
- [ ] Upload to **Internal Testing** track for team validation.
- [ ] Run **Closed Testing** with minimum 20 testers for 14 days (Google Play requirement for personal accounts).
- [ ] Review pre-launch report in Google Play Console (no crashes, accessibility passes).
- [ ] Promote to **Production Track** with staged 10% -> 25% -> 50% -> 100% rollout.
