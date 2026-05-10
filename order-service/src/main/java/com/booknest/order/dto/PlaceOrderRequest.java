package com.booknest.order.dto;

import com.booknest.order.entity.Address;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    private Long userId;
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private double amountPaid;
    private String modeOfPayment;   // "COD" or "WALLET"
    private Address address;
}
