package com.booknest.wallet.razorpay;

public class RazorpayVerifyRequest {
    private Long   userId;
    private double amount;              // rupees — to credit into wallet
    private String razorpayOrderId;    // from Razorpay order created
    private String razorpayPaymentId;  // from frontend after successful payment
    private String razorpaySignature;  // HMAC-SHA256 signature from frontend
    private String remarks;            // e.g. "Wallet top-up via Razorpay"

    public RazorpayVerifyRequest() {}

    public Long   getUserId()                   { return userId; }
    public void   setUserId(Long u)             { this.userId = u; }
    public double getAmount()                   { return amount; }
    public void   setAmount(double a)           { this.amount = a; }
    public String getRazorpayOrderId()          { return razorpayOrderId; }
    public void   setRazorpayOrderId(String o)  { this.razorpayOrderId = o; }
    public String getRazorpayPaymentId()        { return razorpayPaymentId; }
    public void   setRazorpayPaymentId(String p){ this.razorpayPaymentId = p; }
    public String getRazorpaySignature()        { return razorpaySignature; }
    public void   setRazorpaySignature(String s){ this.razorpaySignature = s; }
    public String getRemarks()                  { return remarks; }
    public void   setRemarks(String r)          { this.remarks = r; }
}
