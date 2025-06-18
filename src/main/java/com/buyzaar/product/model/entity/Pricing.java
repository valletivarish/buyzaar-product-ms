package com.buyzaar.product.model.entity;

import java.math.BigDecimal;
import java.util.List;

public class Pricing {
        private BigDecimal mrp;
        private BigDecimal sellingPrice;
        private String currency;
        private List<PricingHistory> history;

        public Pricing() {
        }

        public Pricing(BigDecimal mrp, BigDecimal sellingPrice, String currency, List<PricingHistory> history) {
            this.mrp = mrp;
            this.sellingPrice = sellingPrice;
            this.currency = currency;
            this.history = history;
        }

        public BigDecimal getMrp() {
            return mrp;
        }

        public void setMrp(BigDecimal mrp) {
            this.mrp = mrp;
        }

        public BigDecimal getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(BigDecimal sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public List<PricingHistory> getHistory() {
            return history;
        }

        public void setHistory(List<PricingHistory> history) {
            this.history = history;
        }
    }