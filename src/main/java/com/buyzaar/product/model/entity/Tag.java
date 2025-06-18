package com.buyzaar.product.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tags")
@CompoundIndex(name = "name_category_idx", def = "{'name': 1, 'category': 1}")
public class Tag {

	@Id
	private String id;
	@Indexed(unique = true)
	private String tagId;
	@Indexed(name = "name_unique_idx", unique = true)
	private String name;
	private String category;
	private String description;
	private Integer popularityScore;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Tag() {
	}

	public Tag(String id, String tagId, String name, String category, String description, Integer popularityScore,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.tagId = tagId;
		this.name = name;
		this.category = category;
		this.description = description;
		this.popularityScore = popularityScore;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPopularityScore() {
		return popularityScore;
	}

	public void setPopularityScore(Integer popularityScore) {
		this.popularityScore = popularityScore;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
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