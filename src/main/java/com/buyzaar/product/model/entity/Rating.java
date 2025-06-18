package com.buyzaar.product.model.entity;

import java.util.Map;

public class Rating {
        private Double average;
        private Integer count;
        private Map<Integer, Integer> distribution;

        public Rating() {
        }

        public Rating(Double average, Integer count, Map<Integer, Integer> distribution) {
            this.average = average;
            this.count = count;
            this.distribution = distribution;
        }

        public Double getAverage() {
            return average;
        }

        public void setAverage(Double average) {
            this.average = average;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Map<Integer, Integer> getDistribution() {
            return distribution;
        }

        public void setDistribution(Map<Integer, Integer> distribution) {
            this.distribution = distribution;
        }
    }