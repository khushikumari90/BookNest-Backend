package com.booknest.wallet.razorpay;

public class RazorpayOrderRequest {
    private double amount;   // in rupees — we convert to paise inside
    private String currency; // default: INR
    private String receipt;  // optional reference e.g. "user_5_topup"

    public RazorpayOrderRequest() {}

    public double getAmount()        { return amount; }
    public void   setAmount(double a){ this.amount = a; }
    public String getCurrency()      { return currency; }
    public void   setCurrency(String c){ this.currency = c; }
    public String getReceipt()       { return receipt; }
    public void   setReceipt(String r){ this.receipt = r; }
}
