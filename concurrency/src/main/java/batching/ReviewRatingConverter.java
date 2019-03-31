package batching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
