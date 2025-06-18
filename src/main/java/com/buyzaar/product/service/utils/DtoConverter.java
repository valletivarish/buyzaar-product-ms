package com.buyzaar.product.service.utils;

import com.buyzaar.product.model.dto.TagDTO;
import com.buyzaar.product.model.entity.Tag;

public class DtoConverter {
	public static Tag convertTagDtoToTag(TagDTO tagDto) {
		return new Tag(null, tagDto.getTagId(), tagDto.getName(), tagDto.getCategory(), tagDto.getDescription(),
				tagDto.getPopularityScore(), null, null);
	}
}
