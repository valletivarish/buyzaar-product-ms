package com.buyzaar.product.model.entity;
public class SellerInfo {
        private String sellerId;
        private String sellerName;
        private String contactEmail;
        private String contactPhone;
        private Double sellerRating;
        private Integer totalRatings;

        public SellerInfo() {
        }

        public SellerInfo(String sellerId, String sellerName, String contactEmail, String contactPhone,
                          Double sellerRating, Integer totalRatings) {
            this.sellerId = sellerId;
            this.sellerName = sellerName;
            this.contactEmail = contactEmail;
            this.contactPhone = contactPhone;
            this.sellerRating = sellerRating;
            this.totalRatings = totalRatings;
        }

        public String getSellerId() {
            return sellerId;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }

        public String getSellerName() {
            return sellerName;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public Double getSellerRating() {
            return sellerRating;
        }

        public void setSellerRating(Double sellerRating) {
            this.sellerRating = sellerRating;
        }

        public Integer getTotalRatings() {
            return totalRatings;
        }

        public void setTotalRatings(Integer totalRatings) {
            this.totalRatings = totalRatings;
        }
    }