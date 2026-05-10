package com.booknest.wallet.razorpay;

import com.booknest.wallet.entity.Wallet;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/razorpay")
@Tag(name = "Razorpay Payment", description = "Two-step Razorpay integration: create order → open popup → verify signature → credit wallet")
public class RazorpayController {

    @Autowired
    private RazorpayService razorpayService;

    @Operation(
        summary = "Step 1 — Create a Razorpay order",
        description = """
            Creates a Razorpay order on the Razorpay server and returns the `orderId` and `keyId`
            needed to open the Razorpay checkout popup on the frontend.
            
            **Call this BEFORE showing the Razorpay popup.**
            
            Request body:
            ```json
            {
              "amount": 500.0,
              "receipt": "user_5_topup"
            }
            ```
            
            Response includes `orderId`, `amount`, `currency`, and `keyId` — pass these directly
            to `new Razorpay(options)` in the frontend.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Razorpay order created",
                     content = @Content(schema = @Schema(implementation = RazorpayOrderResponse.class))),
        @ApiResponse(responseCode = "500", description = "Razorpay API error", content = @Content)
    })
    @PostMapping("/create-order")
    public ResponseEntity<RazorpayOrderResponse> createOrder(
            @RequestBody RazorpayOrderRequest request) {
        try {
            RazorpayOrderResponse response =
                razorpayService.createOrder(request.getAmount(), request.getReceipt());
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Step 2 — Verify payment and credit wallet",
        description = """
            Verifies the Razorpay payment signature using HMAC-SHA256, then credits the wallet.
            
            **Must be called AFTER the Razorpay popup returns a successful payment.**
            
            The signature is verified server-side:
            `HMAC_SHA256(razorpayOrderId + "|" + razorpayPaymentId, keySecret)`
            
            If the signature is valid, the wallet is credited with `amount` and a DEPOSIT statement
            is recorded. If invalid, a 500 error is returned — **wallet is NOT credited**.
            
            Request body:
            ```json
            {
              "userId": 5,
              "amount": 500.0,
              "razorpayOrderId":   "order_XXXXXXXXXXXXXXX",
              "razorpayPaymentId": "pay_YYYYYYYYYYYYYYY",
              "razorpaySignature": "<hmac-sha256-hex>",
              "remarks": "Wallet top-up via Razorpay"
            }
            ```
            
            **Test card:** 4111 1111 1111 1111 | Any future date | Any 3-digit CVV
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment verified and wallet credited",
                     content = @Content(schema = @Schema(implementation = Wallet.class))),
        @ApiResponse(responseCode = "500", description = "Signature mismatch — payment NOT credited", content = @Content)
    })
    @PostMapping("/verify")
    public ResponseEntity<Wallet> verifyPayment(
            @RequestBody RazorpayVerifyRequest request) {
        Wallet wallet = razorpayService.verifyAndCreditWallet(request);
        return ResponseEntity.ok(wallet);
    }
}
