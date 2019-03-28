package io.costax.batching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ReportStatusConverter implements AttributeConverter<Report.Status, Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportStatusConverter.class);

    @Override
    public Integer convertToDatabaseColumn(final Report.Status attribute) {
        LOGGER.info("=== convertToDatabaseColumn {}", attribute);

        return attribute.getCode();
    }

    @Override
    public Report.Status convertToEntityAttribute(final Integer dbData) {
        LOGGER.info("=== convertToEntityAttribute {}", dbData);
        return Report.Status.statusOf(dbData);
    }
}
