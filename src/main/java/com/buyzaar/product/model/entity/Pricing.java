package com.buyzaar.product.model.entity;

import java.math.BigDecimal;
import java.util.List;

public class Pricing {
        private BigDecimal mrp;
        private BigDecimal sellingPrice;
        private String currencyCode;
        private List<PricingHistory> history;

        public Pricing() {
        }

        public Pricing(BigDecimal mrp, BigDecimal sellingPrice, String currencyCode, List<PricingHistory> history) {
            this.mrp = mrp;
            this.sellingPrice = sellingPrice;
            this.currencyCode = currencyCode;
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

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currency) {
            this.currencyCode = currency;
        }

        public List<PricingHistory> getHistory() {
            return history;
        }

        public void setHistory(List<PricingHistory> history) {
            this.history = history;
        }
    }
