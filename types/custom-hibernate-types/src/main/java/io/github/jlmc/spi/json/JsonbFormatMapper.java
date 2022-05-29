package io.github.jlmc.spi.json;

import org.hibernate.type.FormatMapper;
import org.hibernate.type.JsonBJsonFormatMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;

public class JsonbFormatMapper implements FormatMapper {

    private JsonBJsonFormatMapper chain;

    public JsonbFormatMapper() {
        this.chain = new JsonBJsonFormatMapper(JsonProducer.INSTANCE);
    }

    @Override
    public <T> T fromString(CharSequence charSequence,
                            JavaType<T> javaType,
                            WrapperOptions wrapperOptions) {
        return chain.fromString(charSequence, javaType, wrapperOptions);
    }

    @Override
    public <T> String toString(T value,
                               JavaType<T> javaType,
                               WrapperOptions wrapperOptions) {
        return chain.toString(value, javaType, wrapperOptions);
    }
}
