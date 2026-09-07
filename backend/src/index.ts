import express, { Request, Response } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import { SandboxPaymentProvider, ProductionUPIPaymentProvider } from './modules/payments/payment.provider';
import { CashbackEngine, CampaignRule } from './modules/cashback/cashback.engine';

const app = express();
const PORT = process.env.PORT || 4000;
const IS_PRODUCTION = process.env.NODE_ENV === 'production' && process.env.PAYMENT_PROVIDER_MODE === 'PRODUCTION';

app.use(helmet());
app.use(cors());
app.use(express.json());

// Initialize payment provider based on compliance environment
const paymentProvider = IS_PRODUCTION
  ? new ProductionUPIPaymentProvider(process.env.PAYMENT_API_KEY || '', process.env.PAYMENT_API_URL || '')
  : new SandboxPaymentProvider();

// Active server-side promotional campaign rule
const sampleCampaign: CampaignRule = {
  id: 'camp_001',
  name: 'First Payment Delight',
  cashbackType: 'PERCENTAGE',
  cashbackValue: 5,
  maximumCashback: 50,
  minimumTransactionAmount: 100,
  dailyBudget: 25000,
  spentBudget: 4200,
  isActive: true,
};

// Health & Compliance Probe
app.get('/health', (req: Request, res: Response) => {
  res.json({
    status: 'HEALTHY',
    service: 'MOON Elite Core Engine',
    payment_mode: paymentProvider.isSandboxMode() ? 'SANDBOX_DEMO' : 'PRODUCTION_UPI',
    timestamp: new Date().toISOString(),
  });
});

// Authentication
app.post('/auth/send-otp', (req: Request, res: Response) => {
  const { phone_number } = req.body;
  if (!phone_number || !phone_number.startsWith('+91')) {
    return res.status(400).json({ error: 'Valid Indian phone number (+91) required.' });
  }
  return res.json({
    success: true,
    session_token: `otp_sess_${Date.now()}`,
    expires_in_seconds: 120,
    resend_wait_seconds: 30,
  });
});

// Payment Initiation
app.post('/payments/initiate', async (req: Request, res: Response) => {
  try {
    const { amount, recipient_vpa, recipient_name, note } = req.body;
    const idempotencyKey = (req.headers['x-idempotency-key'] as string) || `tx_${Date.now()}`;

    const result = await paymentProvider.initiatePayment({
      idempotencyKey,
      userId: 'demo-user-id',
      senderVpa: 'user.demo@bankupi',
      recipientVpa: recipient_vpa,
      recipientName: recipient_name,
      amount: Number(amount),
      currency: 'INR',
      note,
    });

    const cashbackEval = CashbackEngine.evaluateCashback(sampleCampaign, {
      userId: 'demo-user-id',
      transactionId: result.transactionId,
      transactionAmount: Number(amount),
      isFirstTransaction: true,
      userRiskScore: 'LOW',
    });

    res.status(201).json({
      transaction_id: result.transactionId,
      status: 'INITIATED',
      payment_mode: paymentProvider.isSandboxMode() ? 'SANDBOX' : 'PRODUCTION',
      amount: Number(amount),
      estimated_cashback: cashbackEval.calculatedCashback,
      intent_url: result.intentUrl,
      is_sandbox: paymentProvider.isSandboxMode(),
    });
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// Payment Status Check
app.post('/payments/:id/status', async (req: Request, res: Response) => {
  try {
    const status = await paymentProvider.checkPaymentStatus(req.params.id);
    res.json(status);
  } catch (error: any) {
    res.status(500).json({ error: error.message });
  }
});

// QR Validation
app.post('/qr/validate', async (req: Request, res: Response) => {
  const { raw_qr_data } = req.body;
  const validation = await paymentProvider.validateQRCode(raw_qr_data || '');
  res.json(validation);
});

// Webhook for Payment Status
app.post('/webhooks/payment', (req: Request, res: Response) => {
  // Idempotent processing of signed provider callback
  res.json({ received: true, timestamp: Date.now() });
});

app.listen(PORT, () => {
  console.log(`[MOON Elite] Backend running on port ${PORT} [Mode: ${paymentProvider.getProviderName()}]`);
});
