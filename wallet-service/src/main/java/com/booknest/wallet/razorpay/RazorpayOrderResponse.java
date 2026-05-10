package com.booknest.wallet.razorpay;

public class RazorpayOrderResponse {
    private String orderId;     // Razorpay order id  e.g. order_XXXX
    private double amount;      // in rupees
    private String currency;
    private String keyId;       // frontend needs this to init Razorpay checkout
    private String receipt;

    public RazorpayOrderResponse() {}

    public RazorpayOrderResponse(String orderId, double amount,
                                  String currency, String keyId, String receipt) {
        this.orderId  = orderId;
        this.amount   = amount;
        this.currency = currency;
        this.keyId    = keyId;
        this.receipt  = receipt;
    }

    public String getOrderId()           { return orderId; }
    public void   setOrderId(String o)   { this.orderId = o; }
    public double getAmount()            { return amount; }
    public void   setAmount(double a)    { this.amount = a; }
    public String getCurrency()          { return currency; }
    public void   setCurrency(String c)  { this.currency = c; }
    public String getKeyId()             { return keyId; }
    public void   setKeyId(String k)     { this.keyId = k; }
    public String getReceipt()           { return receipt; }
    public void   setReceipt(String r)   { this.receipt = r; }
}
