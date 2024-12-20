package app.entities;

import java.sql.Timestamp;

public class Receipt {

    private int receiptId;
    private Timestamp paidDate;
    private int price;
    private int orderId;

    public Receipt(int receiptId, Timestamp paidDate, int price) {
        this.receiptId = receiptId;
        this.paidDate = paidDate;
        this.price = price;
    }

    public Receipt(int receiptId, Timestamp paidDate, int price, int orderId) {
        this.receiptId = receiptId;
        this.paidDate = paidDate;
        this.price = price;
        this.orderId = orderId;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public Timestamp getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Timestamp paidDate) {
        this.paidDate = paidDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

}
