package io.costax.batching;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter(autoApply = true)
public class ReviewRatingConverter implements AttributeConverter<Review.Rating, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRatingConverter.class);

    @Override
    public Integer convertToDatabaseColumn(final Review.Rating attribute) {
        LOGGER.info("=== convertToDatabaseColumn {}", attribute);

        return attribute.getCode();
    }

    @Override
    public Review.Rating convertToEntityAttribute(final Integer dbData) {
        LOGGER.info("=== convertToEntityAttribute {}", dbData);
        return Review.Rating.statusOf(dbData);
    }
}
