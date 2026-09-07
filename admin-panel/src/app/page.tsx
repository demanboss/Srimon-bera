import React from 'react';

export default function AdminDashboardPage() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-8 font-sans">
      {/* Header */}
      <header className="flex justify-between items-center pb-6 border-b border-slate-800">
        <div>
          <div className="flex items-center gap-3">
            <span className="px-2.5 py-1 text-xs font-semibold uppercase tracking-wider bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded-full">
              Compliance-First
            </span>
            <span className="px-2.5 py-1 text-xs font-semibold uppercase tracking-wider bg-amber-500/20 text-amber-400 border border-amber-500/30 rounded-full">
              SANDBOX / DEMO ACTIVE
            </span>
          </div>
          <h1 className="text-3xl font-bold mt-2 text-white">MOON Elite Admin Console</h1>
          <p className="text-slate-400 text-sm">UPI Payment Rail Monitoring & Cashback Engine Control</p>
        </div>
        <div className="flex gap-4 items-center">
          <div className="text-right">
            <p className="text-sm font-semibold text-slate-200">Super Admin</p>
            <p className="text-xs text-slate-500">finance@moonelite.com</p>
          </div>
          <div className="h-10 w-10 rounded-full bg-emerald-600 flex items-center justify-center font-bold text-white shadow-lg">
            ME
          </div>
        </div>
      </header>

      {/* Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mt-8">
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Monthly GMV (UPI)</p>
          <p className="text-2xl font-bold mt-1 text-white">₹4,89,20,500</p>
          <span className="text-xs text-emerald-400 mt-2 inline-block">↑ 18.4% from last month</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Total Cashback Given</p>
          <p className="text-2xl font-bold mt-1 text-emerald-400">₹8,42,150</p>
          <span className="text-xs text-slate-400 mt-2 inline-block">Pending Ledger: ₹45,200</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Referral Rewards Disbursed</p>
          <p className="text-2xl font-bold mt-1 text-amber-400">₹3,12,500</p>
          <span className="text-xs text-slate-400 mt-2 inline-block">Qualified Referrals: 625</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-5 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Fraud & Risk Alerts</p>
          <p className="text-2xl font-bold mt-1 text-rose-400">3 Pending</p>
          <span className="text-xs text-rose-400/80 mt-2 inline-block">2 Multi-Account Flags</span>
        </div>
      </div>

      {/* Active Cashback Campaigns */}
      <div className="mt-10">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold text-white">Active Cashback Campaigns</h2>
          <button className="bg-emerald-500 hover:bg-emerald-600 text-slate-950 font-semibold px-4 py-2 rounded-lg text-sm transition">
            + New Campaign
          </button>
        </div>
        <div className="overflow-x-auto bg-slate-900 border border-slate-800 rounded-2xl">
          <table className="w-full text-left text-sm text-slate-300">
            <thead className="bg-slate-800/60 text-slate-400 uppercase text-xs">
              <tr>
                <th className="p-4">Campaign Name</th>
                <th className="p-4">Reward Type</th>
                <th className="p-4">Max Cashback</th>
                <th className="p-4">Min Txn</th>
                <th className="p-4">Daily Budget</th>
                <th className="p-4">Status</th>
                <th className="p-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              <tr>
                <td className="p-4 font-semibold text-white">First Payment Delight</td>
                <td className="p-4">5% Percentage</td>
                <td className="p-4">₹50</td>
                <td className="p-4">₹100</td>
                <td className="p-4">₹25,000 (Spent ₹4,200)</td>
                <td className="p-4"><span className="px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 text-xs font-semibold">ACTIVE</span></td>
                <td className="p-4"><button className="text-xs text-amber-400 hover:underline">Pause</button></td>
              </tr>
              <tr>
                <td className="p-4 font-semibold text-white">Merchant Super Saver</td>
                <td className="p-4">Flat ₹25</td>
                <td className="p-4">₹25</td>
                <td className="p-4">₹500</td>
                <td className="p-4">₹50,000 (Spent ₹18,400)</td>
                <td className="p-4"><span className="px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 text-xs font-semibold">ACTIVE</span></td>
                <td className="p-4"><button className="text-xs text-amber-400 hover:underline">Pause</button></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
