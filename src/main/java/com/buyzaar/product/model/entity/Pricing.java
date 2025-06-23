package com.buyzaar.product.model.entity;

import java.util.List;

public class Pricing {
        private Double mrp;
        private Double sellingPrice;
        private String currencyCode;
        private List<PricingHistory> history;

        public Pricing() {
        }

        public Pricing(Double mrp, Double sellingPrice, String currencyCode, List<PricingHistory> history) {
            this.mrp = mrp;
            this.sellingPrice = sellingPrice;
            this.currencyCode = currencyCode;
            this.history = history;
        }

        public Double getMrp() {
            return mrp;
        }

        public void setMrp(Double mrp) {
            this.mrp = mrp;
        }

        public Double getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(Double sellingPrice) {
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
        
        @Override
    	public String toString() {
    		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" [");

    		var fields = getClass().getDeclaredFields();
    		for (int i = 0; i < fields.length; i++) {
    			var field = fields[i];
    			field.setAccessible(true);
    			sb.append(field.getName()).append("=");

    			try {
    				Object value = field.get(this);
    				sb.append(value != null ? value : "null");
    			} catch (IllegalAccessException e) {
    				sb.append("<access denied>");
    			}

    			if (i < fields.length - 1) {
    				sb.append(", ");
    			}
    		}
    		sb.append("]");
    		return sb.toString();
    	}
    }
