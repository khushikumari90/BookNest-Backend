package com.booknest.wallet.razorpay;

import com.booknest.wallet.dto.AddMoneyRequest;
import com.booknest.wallet.entity.Wallet;
import com.booknest.wallet.service.WalletService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private WalletService walletService;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.currency}")
    private String currency;

    /**
     * Step 1: Create a Razorpay order.
     * Called BEFORE showing Razorpay checkout popup in the frontend.
     */
    public RazorpayOrderResponse createOrder(double amountInRupees, String receipt)
            throws RazorpayException {

        // Razorpay requires amount in PAISE (1 INR = 100 paise)
        int amountInPaise = (int) Math.round(amountInRupees * 100);

        JSONObject options = new JSONObject();
        options.put("amount",   amountInPaise);
        options.put("currency", currency);
        options.put("receipt",  receipt != null ? receipt : "booknest_topup");
        options.put("payment_capture", 1); // auto-capture

        Order order = razorpayClient.orders.create(options);

        return new RazorpayOrderResponse(
                order.get("id"),
                amountInRupees,
                currency,
                keyId,
                receipt
        );
    }

    /**
     * Step 2: Verify Razorpay payment signature, then credit wallet.
     * HMAC-SHA256( razorpayOrderId + "|" + razorpayPaymentId, keySecret )
     * must match the razorpaySignature sent by the frontend.
     */
    public Wallet verifyAndCreditWallet(RazorpayVerifyRequest req) {

        // ── Signature verification (security critical) ──────────────────────
        String payload   = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
        String generated = new HmacUtils("HmacSHA256", keySecret).hmacHex(payload);

        if (!generated.equals(req.getRazorpaySignature())) {
            throw new RuntimeException(
                "Payment verification failed: signature mismatch. " +
                "Do not credit wallet without valid signature."
            );
        }

        // ── Signature OK → Credit wallet ─────────────────────────────────────
        AddMoneyRequest addReq = new AddMoneyRequest();
        addReq.setAmount(req.getAmount());
        addReq.setRemarks(
            req.getRemarks() != null
                ? req.getRemarks()
                : "Razorpay top-up | Payment ID: " + req.getRazorpayPaymentId()
        );

        return walletService.addMoney(req.getUserId(), addReq);
    }
}
