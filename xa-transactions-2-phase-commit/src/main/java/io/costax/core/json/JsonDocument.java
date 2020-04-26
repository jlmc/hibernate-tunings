package io.costax.core.json;


import javax.json.bind.annotation.JsonbAnnotation;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbVisibility;
import javax.json.bind.config.PropertyOrderStrategy;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@JsonbAnnotation
@JsonbNillable(true)
@JsonbPropertyOrder(PropertyOrderStrategy.LEXICOGRAPHICAL)
@JsonbVisibility(FieldAccessStrategy.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ANNOTATION_TYPE, TYPE, PACKAGE})
public @interface JsonDocument {
}
