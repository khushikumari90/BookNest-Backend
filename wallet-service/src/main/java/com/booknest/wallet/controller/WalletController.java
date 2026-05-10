package com.booknest.wallet.controller;

import com.booknest.wallet.dto.AddMoneyRequest;
import com.booknest.wallet.dto.PayMoneyRequest;
import com.booknest.wallet.entity.Statement;
import com.booknest.wallet.entity.Wallet;
import com.booknest.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "E-wallet lifecycle, top-up via Internal or Razorpay, deductions, and transaction statements")
@SecurityRequirement(name = "BearerAuth")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Operation(summary = "Create a new wallet for a user",
               description = "Auto-creates a wallet with zero balance. Called immediately after user registration.")
    @ApiResponse(responseCode = "201", description = "Wallet created", content = @Content(schema = @Schema(implementation = Wallet.class)))
    @PostMapping("/create/{userId}")
    public ResponseEntity<Wallet> createWallet(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return new ResponseEntity<>(walletService.createWallet(userId), HttpStatus.CREATED);
    }

    @Operation(summary = "Get wallet by user ID",
               description = "Returns wallet details including current balance and complete statement history.")
    @ApiResponse(responseCode = "200", description = "Wallet with statements returned", content = @Content(schema = @Schema(implementation = Wallet.class)))
    @GetMapping("/user/{userId}")
    public ResponseEntity<Wallet> getWalletByUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUser(userId));
    }

    @Operation(summary = "Get wallet by wallet ID")
    @ApiResponse(responseCode = "200", description = "Wallet returned")
    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletById(
            @Parameter(description = "Wallet ID", example = "1") @PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    @Operation(summary = "Get wallet balance only",
               description = "Returns just the current balance as a double. Lightweight alternative to full wallet fetch.")
    @ApiResponse(responseCode = "200", description = "Current balance in INR")
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<Double> getBalance(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUser(userId).getCurrentBalance());
    }

    @Operation(summary = "Get all wallets (Admin)", description = "Returns wallets for all users. Admin only.")
    @ApiResponse(responseCode = "200", description = "All wallets list")
    @GetMapping("/all")
    public ResponseEntity<List<Wallet>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @Operation(summary = "Delete a wallet (Admin)")
    @ApiResponse(responseCode = "200", description = "Wallet deleted")
    @DeleteMapping("/{walletId}")
    public ResponseEntity<String> deleteWallet(
            @Parameter(description = "Wallet ID", example = "1") @PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.ok("Wallet deleted successfully.");
    }

    @Operation(summary = "Add money to wallet (Internal top-up)",
               description = "Deposits money into wallet and records a DEPOSIT statement.\n\nBody: `{ \"amount\": 1000.0, \"remarks\": \"Wallet top-up\" }`\n\nFor Razorpay top-up use `POST /razorpay/verify` instead.")
    @ApiResponse(responseCode = "200", description = "Money added, updated wallet returned")
    @PostMapping("/user/{userId}/add-money")
    public ResponseEntity<Wallet> addMoney(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @RequestBody AddMoneyRequest request) {
        return ResponseEntity.ok(walletService.addMoney(userId, request));
    }

    @Operation(summary = "Deduct money from wallet (Purchase payment)",
               description = "Deducts amount for order payment. Validates balance first — throws 400 if insufficient.\n\nBody: `{ \"amount\": 499.0, \"orderId\": 1, \"remarks\": \"Payment for Clean Code\" }`")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment deducted, updated wallet returned"),
        @ApiResponse(responseCode = "400", description = "Insufficient balance", content = @Content)
    })
    @PostMapping("/user/{userId}/pay")
    public ResponseEntity<Wallet> payMoney(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @RequestBody PayMoneyRequest request) {
        return ResponseEntity.ok(walletService.payMoney(userId, request));
    }

    @Operation(summary = "Get transaction statements for a user",
               description = "Returns full transaction history sorted by most recent. Each entry has type (DEPOSIT/WITHDRAW), amount, timestamp, and remarks.")
    @ApiResponse(responseCode = "200", description = "List of statements")
    @GetMapping("/user/{userId}/statements")
    public ResponseEntity<List<Statement>> getStatements(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getStatementsByUser(userId));
    }

    @Operation(summary = "Filter statements by transaction type",
               description = "Returns statements filtered by type. Valid values: `DEPOSIT` or `WITHDRAW`")
    @ApiResponse(responseCode = "200", description = "Filtered statements")
    @GetMapping("/statements/type/{type}")
    public ResponseEntity<List<Statement>> getByType(
            @Parameter(description = "Transaction type: DEPOSIT or WITHDRAW", example = "DEPOSIT") @PathVariable String type) {
        return ResponseEntity.ok(walletService.getStatementsByType(type));
    }
}
