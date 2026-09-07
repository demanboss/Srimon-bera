-- =====================================================================
-- MOON Elite: Legal, Compliance-First UPI Payments & Rewards Platform
-- Comprehensive PostgreSQL Schema (Production & Sandbox Compliant)
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------------------
-- 1. App Configuration & Feature Flags
-- ---------------------------------------------------------------------
CREATE TABLE app_settings (
    key VARCHAR(100) PRIMARY KEY,
    value JSONB NOT NULL,
    description TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feature_flags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flag_key VARCHAR(100) UNIQUE NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    target_environment VARCHAR(20) DEFAULT 'ALL', -- 'SANDBOX', 'PRODUCTION', 'ALL'
    rules JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 2. Users, Authentication, Devices & Sessions
-- ---------------------------------------------------------------------
CREATE TYPE user_status AS ENUM ('PENDING_VERIFICATION', 'ACTIVE', 'SUSPENDED', 'DELETION_REQUESTED', 'ANONYMIZED');
CREATE TYPE kyc_status AS ENUM ('NOT_STARTED', 'MINIMUM_KYC', 'FULL_KYC_VERIFIED', 'REJECTED');

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    full_name VARCHAR(120),
    email VARCHAR(255) UNIQUE,
    status user_status DEFAULT 'PENDING_VERIFICATION',
    kyc_status kyc_status DEFAULT 'NOT_STARTED',
    primary_upi_id VARCHAR(100),
    referral_code VARCHAR(20) UNIQUE NOT NULL,
    referred_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    reward_points_balance NUMERIC(12, 2) DEFAULT 0.00 CHECK (reward_points_balance >= 0),
    risk_score VARCHAR(20) DEFAULT 'LOW', -- 'LOW', 'MEDIUM', 'HIGH', 'BLOCKED'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_fingerprint VARCHAR(255) NOT NULL,
    device_model VARCHAR(100),
    os_version VARCHAR(50),
    app_version VARCHAR(20),
    fcm_push_token TEXT,
    is_emulator BOOLEAN DEFAULT FALSE,
    is_rooted BOOLEAN DEFAULT FALSE,
    trust_score NUMERIC(5, 2) DEFAULT 100.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_device UNIQUE (user_id, device_fingerprint)
);

CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device_id UUID REFERENCES devices(id) ON DELETE CASCADE,
    refresh_token_hash VARCHAR(255) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE otp_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_number VARCHAR(15) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    purpose VARCHAR(50) DEFAULT 'LOGIN',
    attempts INT DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 3. Bank Accounts & Linked Payment Instruments
-- Note: Sensitive credentials/PINs are NEVER stored. Only masked references.
-- ---------------------------------------------------------------------
CREATE TABLE bank_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bank_name VARCHAR(100) NOT NULL,
    ifsc_prefix VARCHAR(10) NOT NULL,
    masked_account_number VARCHAR(20) NOT NULL,
    account_holder_name VARCHAR(120) NOT NULL,
    vpa_address VARCHAR(100) NOT NULL,
    provider_token_reference VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_upi_lite_enabled BOOLEAN DEFAULT FALSE,
    upi_lite_balance NUMERIC(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 4. Merchants & Stores
-- ---------------------------------------------------------------------
CREATE TABLE merchants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_code VARCHAR(50) UNIQUE NOT NULL,
    business_name VARCHAR(150) NOT NULL,
    legal_name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL, -- 'GROCERY', 'FOOD', 'RECHARGE', 'SHOPPING', 'TRAVEL', 'UTILITIES'
    contact_email VARCHAR(255),
    contact_phone VARCHAR(15),
    vpa_address VARCHAR(100) NOT NULL,
    qr_payload TEXT NOT NULL,
    kyc_status VARCHAR(20) DEFAULT 'VERIFIED',
    settlement_status VARCHAR(20) DEFAULT 'ACTIVE',
    commission_rate NUMERIC(5, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 5. Payment Transactions & Lifecycle
-- Fully compliance-first: strict status tracking, provider reference, idempotency
-- ---------------------------------------------------------------------
CREATE TYPE payment_mode AS ENUM ('SANDBOX', 'PRODUCTION');
CREATE TYPE transaction_status AS ENUM ('INITIATED', 'PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'EXPIRED', 'REFUNDED', 'REVERSED');
CREATE TYPE payment_type AS ENUM ('P2P_TRANSFER', 'P2M_MERCHANT', 'QR_PAYMENT', 'UPI_LITE', 'BILL_PAY');

CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    merchant_id UUID REFERENCES merchants(id),
    payment_mode payment_mode DEFAULT 'SANDBOX',
    payment_type payment_type NOT NULL,
    status transaction_status DEFAULT 'INITIATED',
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) DEFAULT 'INR',
    sender_vpa VARCHAR(100) NOT NULL,
    recipient_vpa VARCHAR(100) NOT NULL,
    recipient_name VARCHAR(150) NOT NULL,
    note VARCHAR(255),
    provider_name VARCHAR(50) NOT NULL, -- e.g., 'SANDBOX_PROVIDER', 'YES_BANK_PSP', 'AXIS_TPAP', 'RAZORPAY_UPI'
    provider_transaction_id VARCHAR(100),
    bank_reference_number VARCHAR(100), -- NPCI RRN (12 digits)
    provider_status_code VARCHAR(50),
    error_message TEXT,
    is_disputed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE payment_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID NOT NULL REFERENCES payment_transactions(id) ON DELETE CASCADE,
    attempt_number INT NOT NULL,
    raw_request_headers JSONB,
    provider_response JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_callbacks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID REFERENCES payment_transactions(id) ON DELETE SET NULL,
    provider_name VARCHAR(50) NOT NULL,
    signature_verified BOOLEAN DEFAULT FALSE,
    raw_payload JSONB NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 6. Cashback Engine & Double-Entry Ledger
-- ---------------------------------------------------------------------
CREATE TYPE cashback_type AS ENUM ('FIXED', 'PERCENTAGE', 'TIERED', 'RANDOMIZED_PROMOTIONAL');
CREATE TYPE ledger_status AS ENUM ('PENDING', 'AVAILABLE', 'EXPIRED', 'REVERSED', 'CANCELLED');

CREATE TABLE cashback_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    campaign_type VARCHAR(50) DEFAULT 'GENERAL',
    cashback_type cashback_type NOT NULL,
    cashback_value NUMERIC(10, 2) NOT NULL, -- percentage or fixed amount
    maximum_cashback NUMERIC(10, 2) NOT NULL,
    minimum_transaction_amount NUMERIC(10, 2) NOT NULL DEFAULT 10.00,
    eligible_categories JSONB DEFAULT '[]'::jsonb,
    eligible_merchants JSONB DEFAULT '[]'::jsonb,
    new_user_only BOOLEAN DEFAULT FALSE,
    first_transaction_only BOOLEAN DEFAULT FALSE,
    daily_budget NUMERIC(12, 2) DEFAULT 10000.00,
    monthly_budget NUMERIC(12, 2) DEFAULT 250000.00,
    total_campaign_budget NUMERIC(14, 2) DEFAULT 1000000.00,
    spent_budget NUMERIC(14, 2) DEFAULT 0.00,
    per_user_limit INT DEFAULT 3,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL REFERENCES cashback_campaigns(id) ON DELETE CASCADE,
    tier_min_amount NUMERIC(10, 2) NOT NULL,
    tier_max_amount NUMERIC(10, 2) NOT NULL,
    reward_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashback_ledger (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(120) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    source_transaction_id UUID NOT NULL REFERENCES payment_transactions(id),
    campaign_id UUID NOT NULL REFERENCES cashback_campaigns(id),
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    status ledger_status DEFAULT 'PENDING',
    settlement_reference VARCHAR(100),
    note VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expiry_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- ---------------------------------------------------------------------
-- 7. Referral Program & Multi-Condition Qualification
-- ---------------------------------------------------------------------
CREATE TYPE referral_status AS ENUM (
    'REFERRED',
    'REGISTERED',
    'VERIFIED',
    'QUALIFIED',
    'REWARD_PENDING',
    'REWARD_CREDITED',
    'REWARD_REVERSED',
    'FRAUD_REVIEW'
);

CREATE TABLE referral_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    referrer_reward NUMERIC(10, 2) NOT NULL DEFAULT 500.00,
    new_user_reward NUMERIC(10, 2) NOT NULL DEFAULT 50.00,
    min_qualifying_transaction NUMERIC(10, 2) NOT NULL DEFAULT 199.00,
    max_referrals_per_day INT DEFAULT 10,
    max_total_reward_per_user NUMERIC(12, 2) DEFAULT 5000.00,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referrals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referrer_user_id UUID NOT NULL REFERENCES users(id),
    referee_user_id UUID NOT NULL REFERENCES users(id),
    referral_code_used VARCHAR(20) NOT NULL,
    status referral_status DEFAULT 'REGISTERED',
    qualifying_transaction_id UUID REFERENCES payment_transactions(id),
    referrer_reward_amount NUMERIC(10, 2) DEFAULT 0.00,
    referee_reward_amount NUMERIC(10, 2) DEFAULT 0.00,
    risk_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    qualified_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uq_referee UNIQUE (referee_user_id)
);

-- ---------------------------------------------------------------------
-- 8. Anti-Fraud & Risk Engine
-- ---------------------------------------------------------------------
CREATE TABLE fraud_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    event_type VARCHAR(50) NOT NULL, -- 'MULTIPLE_ACCOUNTS_SAME_DEVICE', 'SELF_REFERRAL_ATTEMPT', 'RAPID_VELOCITY', 'EMULATOR_DETECTED'
    risk_level VARCHAR(20) NOT NULL, -- 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    ip_address INET,
    device_fingerprint VARCHAR(255),
    details JSONB,
    action_taken VARCHAR(50) DEFAULT 'FLAGGED_FOR_REVIEW',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fraud_scores (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    velocity_score NUMERIC(5, 2) DEFAULT 0.00,
    device_reputation_score NUMERIC(5, 2) DEFAULT 100.00,
    referral_loop_risk NUMERIC(5, 2) DEFAULT 0.00,
    aggregate_risk VARCHAR(20) DEFAULT 'LOW',
    last_evaluated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 9. Customer Support Desk & Ticketing
-- ---------------------------------------------------------------------
CREATE TYPE ticket_status AS ENUM ('OPEN', 'IN_PROGRESS', 'WAITING_FOR_USER', 'RESOLVED', 'CLOSED');
CREATE TYPE ticket_category AS ENUM (
    'PAYMENT_FAILED',
    'PAYMENT_PENDING',
    'WRONG_DEBIT',
    'REFUND',
    'CASHBACK_MISSING',
    'REFERRAL_REWARD',
    'ACCOUNT_ISSUE',
    'UPI_LITE',
    'MERCHANT_ISSUE',
    'OTHER'
);

CREATE TABLE support_tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_number VARCHAR(30) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    transaction_id UUID REFERENCES payment_transactions(id),
    category ticket_category NOT NULL,
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status ticket_status DEFAULT 'OPEN',
    assigned_admin_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE support_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    sender_type VARCHAR(20) NOT NULL, -- 'USER', 'ADMIN', 'SYSTEM'
    sender_id UUID NOT NULL,
    message TEXT NOT NULL,
    attachment_urls JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 10. Notifications (FCM / In-App)
-- ---------------------------------------------------------------------
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    body TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- 'PAYMENT_SUCCESS', 'CASHBACK_CREDITED', 'SECURITY_ALERT'
    payload JSONB DEFAULT '{}'::jsonb,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 11. Admin Role-Based Access Control & Audit Logs
-- ---------------------------------------------------------------------
CREATE TABLE admin_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(50) NOT NULL, -- 'SUPER_ADMIN', 'FINANCE_ADMIN', 'SUPPORT_ADMIN', 'CAMPAIGN_ADMIN', 'RISK_ADMIN', 'ANALYST'
    two_factor_secret VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES admin_users(id),
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(100) NOT NULL,
    target_id VARCHAR(100),
    before_state JSONB,
    after_state JSONB,
    ip_address INET,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- 12. Indexes for Performance & Anti-Fraud Lookups
-- ---------------------------------------------------------------------
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_referral_code ON users(referral_code);
CREATE INDEX idx_transactions_user ON payment_transactions(user_id, created_at DESC);
CREATE INDEX idx_transactions_status ON payment_transactions(status);
CREATE INDEX idx_transactions_provider_ref ON payment_transactions(provider_transaction_id);
CREATE INDEX idx_cashback_user ON cashback_ledger(user_id, status);
CREATE INDEX idx_cashback_source ON cashback_ledger(source_transaction_id);
CREATE INDEX idx_referrals_referrer ON referrals(referrer_user_id);
CREATE INDEX idx_fraud_events_user ON fraud_events(user_id);
CREATE INDEX idx_devices_fingerprint ON devices(device_fingerprint);
CREATE INDEX idx_support_tickets_user ON support_tickets(user_id, status);

-- ---------------------------------------------------------------------
-- 13. Seed Baseline Campaigns & Mock Merchants (Compliant Sandbox Data)
-- ---------------------------------------------------------------------
INSERT INTO cashback_campaigns (
    id, name, description, cashback_type, cashback_value, maximum_cashback,
    minimum_transaction_amount, eligible_categories, start_at, end_at, is_active
) VALUES (
    'a1111111-1111-1111-1111-111111111111',
    'First Payment Delight',
    'Pay ₹100 or more via UPI and receive 5% cashback up to ₹50 on your first payment.',
    'PERCENTAGE', 5.00, 50.00, 100.00,
    '["GROCERY", "FOOD", "RECHARGE", "SHOPPING", "UTILITIES"]'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '90 days', TRUE
), (
    'b2222222-2222-2222-2222-222222222222',
    'Merchant Super Saver',
    'Flat ₹25 cashback on transactions above ₹500 at verified partner merchants.',
    'FIXED', 25.00, 25.00, 500.00,
    '["GROCERY", "SHOPPING"]'::jsonb,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', TRUE
);

INSERT INTO merchants (
    id, merchant_code, business_name, legal_name, category, vpa_address, qr_payload
) VALUES (
    'c3333333-3333-3333-3333-333333333333',
    'MERCH_BIGBAZAR_01',
    'BigBazaar Retail Store',
    'Future Retail Ltd.',
    'GROCERY',
    'bigbazaar.demo@bankupi',
    'upi://pay?pa=bigbazaar.demo@bankupi&pn=BigBazaar&mc=5411&tid=DEMO_TXN_01'
), (
    'd4444444-4444-4444-4444-444444444444',
    'MERCH_SWIGGY_02',
    'Swiggy Gourmet Express',
    'Bundl Technologies Pvt Ltd',
    'FOOD',
    'swiggy.demo@bankupi',
    'upi://pay?pa=swiggy.demo@bankupi&pn=Swiggy&mc=5812&tid=DEMO_TXN_02'
);
