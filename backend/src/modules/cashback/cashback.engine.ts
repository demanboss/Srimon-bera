// MOON Elite - Server-Side Cashback Engine & Double-Entry Ledger Calculator
// Compliance rule: Never trust client-supplied cashback amounts.

export interface CampaignRule {
  id: string;
  name: string;
  cashbackType: 'FIXED' | 'PERCENTAGE' | 'TIERED' | 'RANDOMIZED_PROMOTIONAL';
  cashbackValue: number;
  maximumCashback: number;
  minimumTransactionAmount: number;
  dailyBudget: number;
  spentBudget: number;
  isActive: boolean;
}

export interface CashbackEvaluationInput {
  userId: string;
  transactionId: string;
  transactionAmount: number;
  merchantCategory?: string;
  isFirstTransaction: boolean;
  userRiskScore: 'LOW' | 'MEDIUM' | 'HIGH' | 'BLOCKED';
}

export interface CashbackEvaluationResult {
  eligible: boolean;
  campaignId?: string;
  campaignName?: string;
  calculatedCashback: number;
  status: 'PENDING' | 'AVAILABLE' | 'REJECTED' | 'FRAUD_REVIEW';
  reason: string;
  idempotencyKey: string;
}

export class CashbackEngine {
  /**
   * Evaluates cashback eligibility based strictly on active server campaigns,
   * user risk score, budget limits, and minimum transaction thresholds.
   */
  static evaluateCashback(
    campaign: CampaignRule,
    input: CashbackEvaluationInput
  ): CashbackEvaluationResult {
    const idempotencyKey = `CB_${input.userId}_${input.transactionId}`;

    // 1. Risk check
    if (input.userRiskScore === 'BLOCKED' || input.userRiskScore === 'HIGH') {
      return {
        eligible: false,
        calculatedCashback: 0,
        status: 'FRAUD_REVIEW',
        reason: 'Account flagged for high risk or velocity anomalies.',
        idempotencyKey,
      };
    }

    // 2. Minimum amount check
    if (input.transactionAmount < campaign.minimumTransactionAmount) {
      return {
        eligible: false,
        calculatedCashback: 0,
        status: 'REJECTED',
        reason: `Transaction amount ₹${input.transactionAmount} is below campaign threshold ₹${campaign.minimumTransactionAmount}.`,
        idempotencyKey,
      };
    }

    // 3. Campaign budget check
    if (campaign.spentBudget >= campaign.dailyBudget) {
      return {
        eligible: false,
        calculatedCashback: 0,
        status: 'REJECTED',
        reason: 'Daily campaign budget reached for this promotional pool.',
        idempotencyKey,
      };
    }

    // 4. Calculate reward amount
    let reward = 0;
    if (campaign.cashbackType === 'PERCENTAGE') {
      reward = (input.transactionAmount * campaign.cashbackValue) / 100;
    } else if (campaign.cashbackType === 'FIXED') {
      reward = campaign.cashbackValue;
    } else if (campaign.cashbackType === 'RANDOMIZED_PROMOTIONAL') {
      // Deterministic pseudo-randomness within configured bounds
      const minReward = 10;
      const maxReward = campaign.maximumCashback;
      reward = Math.floor(minReward + Math.random() * (maxReward - minReward + 1));
    }

    // 5. Cap by maximum permitted reward
    const finalCashback = Math.min(reward, campaign.maximumCashback);

    return {
      eligible: true,
      campaignId: campaign.id,
      campaignName: campaign.name,
      calculatedCashback: Math.round(finalCashback * 100) / 100,
      status: 'PENDING', // Matures to AVAILABLE after settlement verification
      reason: 'Eligible cashback calculated successfully.',
      idempotencyKey,
    };
  }

  /**
   * Double-entry ledger reversal: If transaction is refunded or reversed,
   * automatically reverse or deduct corresponding cashback.
   */
  static generateReversalEntry(originalLedgerEntry: {
    id: string;
    userId: string;
    amount: number;
  }) {
    return {
      reversalLedgerId: `REV_${originalLedgerEntry.id}`,
      userId: originalLedgerEntry.userId,
      amount: -originalLedgerEntry.amount,
      status: 'REVERSED',
      timestamp: new Date(),
    };
  }
}
