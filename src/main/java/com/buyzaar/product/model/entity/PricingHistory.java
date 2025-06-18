package com.buyzaar.product.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PricingHistory {
        private BigDecimal price;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;

        public PricingHistory() {
        }

        public PricingHistory(BigDecimal price, LocalDateTime fromDate, LocalDateTime toDate) {
            this.price = price;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
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
