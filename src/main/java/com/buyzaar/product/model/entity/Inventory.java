package com.buyzaar.product.model.entity;
public class Inventory {
        private Integer stockQuantity;
        private Boolean inStock;
        private Integer reservedQuantity;
        private String warehouseLocation;

        public Inventory() {
        }

        public Inventory(Integer stockQuantity, Boolean inStock, Integer reservedQuantity, String warehouseLocation) {
            this.stockQuantity = stockQuantity;
            this.inStock = inStock;
            this.reservedQuantity = reservedQuantity;
            this.warehouseLocation = warehouseLocation;
        }

        public Integer getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

        public Boolean getInStock() {
            return inStock;
        }

        public void setInStock(Boolean inStock) {
            this.inStock = inStock;
        }

        public Integer getReservedQuantity() {
            return reservedQuantity;
        }

        public void setReservedQuantity(Integer reservedQuantity) {
            this.reservedQuantity = reservedQuantity;
        }

        public String getWarehouseLocation() {
            return warehouseLocation;
        }

        public void setWarehouseLocation(String warehouseLocation) {
            this.warehouseLocation = warehouseLocation;
        }
    }