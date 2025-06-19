package com.buyzaar.product.utils;

import java.util.Collection;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ProductUtils {
	
	public static Query createQuery(String fieldName, Object value) {
		Criteria criteria;

		if (value instanceof Collection) {
			criteria = Criteria.where(fieldName).in((Collection<?>) value);
		} else if (value instanceof Boolean) {
			criteria = Criteria.where(fieldName).is((Boolean) value);
		} else if (value instanceof Integer) {
			criteria = Criteria.where(fieldName).is((Integer) value);
		} else if (value instanceof Long) {
			criteria = Criteria.where(fieldName).is((Long) value);
		} else if (value instanceof String) {
			criteria = Criteria.where(fieldName).is((String) value);
		} else {
			criteria = Criteria.where(fieldName).is(value);
		}

		return new Query(criteria);
	}

}
