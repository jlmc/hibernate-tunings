@TypeDefs({

        @TypeDef(name = "string-array", defaultForType = String[].class, typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", defaultForType = int[].class, typeClass = IntArrayType.class),

})
package io.costax.hibernatetunings.entities;

import io.costax.hibernatetunings.type.array.IntArrayType;
import io.costax.hibernatetunings.type.array.StringArrayType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;