package com.buyzaar.product.constants;

import java.util.Set;

public class AppConstants {
	public static final String TAG_NAME = "name";
	public static final String TAG_DESCRIPTION = "description";
	public static final String TAG_POPULARITY_SCORE = "popularityScore";
	public static final String TAG_UPDATED_AT = "updatedAt";
	public static final String TAG_CREATED_AT = "createdAt";
	public static final String PRODUCT_ID = "productId";
	public static final String PRODUCT_DOES_NOT_EXIST = "Product does not exist with productId: ";
	public static final String PRODUCT_CATEGORY = "category";
	public static final String PRODUCT_DESCRPTION = "description";
	public static final String TAGIDS = "tagIds";
	public static final String PRODUCT_NAME = "name";
	public static final String PRODUCT_BRAND = "brand";
	public static final String PRODUCT_UPDATED_AT = "updatedAt";
	public static final String PRODUCT_CREATED_AT = "createdAt";
	
	public static final String TAG_ID="tagId";

	public static final Set<String> SELLER_DEFINED_TAGS = Set.of("new-arrival", "eco-friendly", "handmade",
			"lightweight", "durable", "refillable", "summer-collection", "winter-wear", "formalwear", "oversized-fit",
			"sustainable-fabric", "wireless", "bluetooth-5.0", "fast-charging", "noise-cancelling", "premium",
			"compact", "ergonomic", "waterproof", "made-in-india");

	public static final Set<String> SYSTEM_GENERATED_TAGS = Set.of("bestseller", "trending", "highly-rated",
			"frequently-bought", "wishlisted", "budget-pick", "flash-sale", "clearance", "featured", "diwali-deals",
			"back-to-school", "valentines-pick", "limited-stock", "newly-viewed");
}
