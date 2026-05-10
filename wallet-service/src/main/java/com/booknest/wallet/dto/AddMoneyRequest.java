package com.booknest.wallet.dto;

import lombok.Data;

@Data
public class AddMoneyRequest {
    private double amount;
    private String remarks;
}
