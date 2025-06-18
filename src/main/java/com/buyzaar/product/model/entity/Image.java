package com.buyzaar.product.model.entity;
 public class Image {
        private String url;
        private String altText;
        private Integer order;

        public Image() {
        }

        public Image(String url, String altText, Integer order) {
            this.url = url;
            this.altText = altText;
            this.order = order;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAltText() {
            return altText;
        }

        public void setAltText(String altText) {
            this.altText = altText;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }
    }