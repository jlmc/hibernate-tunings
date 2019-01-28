package io.costax.hibernatetuning.entity.options;

import io.costax.hibernatetuning.entity.EnterpriseOption;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.EnumSet;
import java.util.Set;

@Converter(autoApply = true)
public class OptionsConverter implements AttributeConverter<Options<EnterpriseOption>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final Options<EnterpriseOption> attribute) {
        if (attribute == null) return null;

        final Set<EnterpriseOption> values = attribute.getValues();

        Integer acumulator = 0;

        for (EnterpriseOption value : values) {
            acumulator = acumulator | value.getWise();
        }

        return acumulator;
    }

    @Override
    public Options<EnterpriseOption> convertToEntityAttribute(final Integer dbData) {
        if (dbData == null) return null;

        final EnterpriseOption[] values = EnterpriseOption.values();

        final EnumSet<EnterpriseOption> enterpriseOptions = EnumSet.noneOf(EnterpriseOption.class);
        for (EnterpriseOption x : values) {
            if ((x.getWise() & dbData) == x.getWise()) {
                enterpriseOptions.add(x);
            }
        }

        return new Options<>(enterpriseOptions);
    }
}
