package com.buyzaar.product.model.entity;

import java.awt.Image;
import java.util.List;
import java.util.Map;

public class Variant {
        private String variantId;
        private Map<String, String> attributes;
        private List<Image> images;
        private Pricing pricing;
        private Inventory inventory;

        public Variant() {
        }

        public Variant(String variantId, Map<String, String> attributes, List<Image> images,
                       Pricing pricing, Inventory inventory) {
            this.variantId = variantId;
            this.attributes = attributes;
            this.images = images;
            this.pricing = pricing;
            this.inventory = inventory;
        }

        public String getVariantId() {
            return variantId;
        }

        public void setVariantId(String variantId) {
            this.variantId = variantId;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public Pricing getPricing() {
            return pricing;
        }

        public void setPricing(Pricing pricing) {
            this.pricing = pricing;
        }

        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }