package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;

public class PricingHistory {
        private Double price;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;

        public PricingHistory() {
        }

        public PricingHistory(Double price, LocalDateTime fromDate, LocalDateTime toDate) {
            this.price = price;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public LocalDateTime getFromDate() {
            return fromDate;
        }

        public void setFromDate(LocalDateTime fromDate) {
            this.fromDate = fromDate;
        }

        public LocalDateTime getToDate() {
            return toDate;
        }

        public void setToDate(LocalDateTime toDate) {
            this.toDate = toDate;
        }
    }
