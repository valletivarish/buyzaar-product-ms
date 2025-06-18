package com.buyzaar.product.model.dto;

import java.time.LocalDateTime;

public class TagDTO {
    private String tagId;
    private String name;
    private String category;
    private String description;
    private Integer popularityScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TagDTO() {}

    public TagDTO(String tagId, String name, String category, String description,
                  Integer popularityScore, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tagId = tagId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.popularityScore = popularityScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPopularityScore() { return popularityScore; }
    public void setPopularityScore(Integer popularityScore) { this.popularityScore = popularityScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
