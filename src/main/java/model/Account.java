package model;

import java.math.BigDecimal;

public class Account {
    private int id;
    private String cardNumber;
    private String holderName;
    private BigDecimal balance;

    public Account() {
    }

    public Account(int id, String cardNumber, String holderName, BigDecimal balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
