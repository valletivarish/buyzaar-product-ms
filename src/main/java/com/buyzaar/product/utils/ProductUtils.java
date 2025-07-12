package com.buyzaar.product.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.buyzaar.product.model.entity.Specification;

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

	public static String generateAltText(String name, List<Specification> specifications) {
		StringBuilder altText = new StringBuilder("Image of ");
		altText.append(name);

		if (specifications != null && !specifications.isEmpty()) {
			altText.append(" - ");
			altText.append(specifications.stream().map(Specification::getValue).collect(Collectors.joining(", ")));
		}

		return altText.toString();
	}

}
