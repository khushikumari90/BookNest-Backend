package com.booknest.wallet.dto;

import lombok.Data;

@Data
public class PayMoneyRequest {
    private double amount;
    private Long orderId;
    private String remarks;
}
