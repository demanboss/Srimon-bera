# MOON Elite — REST API Specification
**Version:** 1.0.0 (Production & Sandbox Compliant)  
**Base URL:** `https://api.moonelite.com/v1` (Sandbox: `https://sandbox-api.moonelite.com/v1`)

---

## 1. Authentication & Session Security
All non-auth endpoints require `Authorization: Bearer <access_token>`.

### `POST /auth/send-otp`
Sends a one-time passcode to the Indian phone number (+91).
- **Request:**
  ```json
  {
    "phone_number": "+919876543210",
    "device_fingerprint": "a9f8b7e6d5c4...",
    "app_signature": "XYZ12345"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "session_token": "otp_sess_9f81a2...",
    "expires_in_seconds": 120,
    "resend_wait_seconds": 30
  }
  ```

### `POST /auth/verify-otp`
Validates the OTP and returns tokens. Never returns plain-text OTPs.
- **Request:**
  ```json
  {
    "phone_number": "+919876543210",
    "otp": "482910",
    "session_token": "otp_sess_9f81a2...",
    "device_model": "Pixel 8",
    "is_emulator": false
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "success": true,
    "is_new_user": false,
    "access_token": "jwt.eyJhbGciOi...",
    "refresh_token": "jwt.refresh.eyJhb...",
    "user": {
      "id": "u-1234-uuid",
      "phone_number": "+919876543210",
      "full_name": "Arjun Sharma",
      "referral_code": "MOON777",
      "kyc_status": "FULL_KYC_VERIFIED"
    }
  }
  ```

---

## 2. Payments & Compliance Architecture
Supports dual modes: `SANDBOX` and `PRODUCTION`.

### `POST /payments/initiate`
Initiates a UPI transaction. The server computes eligibility and registers idempotency.
- **Headers:** `X-Idempotency-Key: <unique-uuid>`
- **Request:**
  ```json
  {
    "payment_mode": "SANDBOX", 
    "payment_type": "P2M_MERCHANT",
    "amount": 499.00,
    "currency": "INR",
    "recipient_vpa": "bigbazaar.demo@bankupi",
    "recipient_name": "BigBazaar Retail",
    "sender_vpa": "user@bankupi",
    "note": "Groceries purchase"
  }
  ```
- **Response `201 Created`:**
  ```json
  {
    "transaction_id": "tx_9921_uuid",
    "status": "INITIATED",
    "payment_mode": "SANDBOX",
    "amount": 499.00,
    "estimated_cashback": 24.95,
    "intent_url": "upi://pay?pa=bigbazaar.demo@bankupi&pn=BigBazaar&am=499.00&tr=tx_9921_uuid",
    "compliance_disclaimer": "DEMO / SANDBOX: No real bank funds are debited."
  }
  ```

### `POST /payments/:id/status`
Server queries authorized PSP/NPCI rails to verify the final status.
- **Response `200 OK`:**
  ```json
  {
    "transaction_id": "tx_9921_uuid",
    "status": "SUCCESS",
    "bank_reference_number": "426189102938",
    "provider_transaction_id": "PSP_YES_88291",
    "completed_at": "2026-09-06T10:30:00Z",
    "cashback_awarded": {
      "ledger_id": "cb_ledger_11",
      "amount": 24.95,
      "status": "PENDING"
    }
  }
  ```

---

## 3. QR Validation & Processing

### `POST /qr/validate`
Validates an NPCI-compliant UPI QR payload before proceeding to payment.
- **Request:**
  ```json
  {
    "raw_qr_data": "upi://pay?pa=merchant.demo@icici&pn=FreshMart&mc=5411&am=250.00&cu=INR"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "is_valid": true,
    "vpa": "merchant.demo@icici",
    "name": "FreshMart",
    "merchant_code": "5411",
    "suggested_amount": 250.00,
    "is_verified_merchant": true,
    "risk_level": "LOW"
  }
  ```

---

## 4. Cashback Ledger & Double-Entry Engine

### `GET /cashback/balance`
Retrieves the user's non-bank reward balance breakdown.
- **Response `200 OK`:**
  ```json
  {
    "total_earned": 1420.00,
    "pending_cashback": 75.00,
    "available_rewards_points": 1345.00,
    "currency": "INR_REWARD_PTS",
    "disclaimer": "Reward points are promotional credits and not bank deposits."
  }
  ```

### `GET /cashback/history`
- **Response `200 OK`:**
  ```json
  {
    "ledger_items": [
      {
        "id": "cb_001",
        "source_transaction_id": "tx_9921",
        "campaign_name": "First Payment Delight",
        "amount": 24.95,
        "status": "AVAILABLE",
        "created_at": "2026-09-05T12:00:00Z",
        "expiry_at": "2026-12-05T12:00:00Z"
      }
    ]
  }
  ```

---

## 5. Referral Engine

### `GET /referral`
- **Response `200 OK`:**
  ```json
  {
    "referral_code": "MOON777",
    "invite_link": "https://moonelite.com/invite/MOON777",
    "max_reward_per_referral": 500.00,
    "referee_welcome_bonus": 50.00,
    "qualification_criteria": "Friend registers and completes a qualifying payment of ₹199+",
    "stats": {
      "total_invites": 8,
      "registered": 6,
      "qualified": 4,
      "total_earned": 2000.00
    }
  }
  ```

---

## 6. Webhooks (PSP / Bank Callbacks)
**Headers:** `X-Provider-Signature: <sha256-hmac>`

### `POST /webhooks/payment`
Receives asynchronous payment notifications from authorized payment partners.
- **Payload:**
  ```json
  {
    "event_id": "evt_88921",
    "event_type": "PAYMENT_SUCCESS",
    "provider_ref": "PSP_YES_88291",
    "bank_rrn": "426189102938",
    "transaction_id": "tx_9921_uuid",
    "status": "SUCCESS",
    "timestamp": 1788739200
  }
  ```
