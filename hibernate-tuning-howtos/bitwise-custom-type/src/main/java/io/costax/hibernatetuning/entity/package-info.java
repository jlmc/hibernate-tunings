@TypeDefs({
        @TypeDef(name = "bitwise", defaultForType = Bitwise.class, typeClass = BitwiseType.class),
})
package io.costax.hibernatetuning.entity;

import io.costax.hibernatetuning.bitwise.Bitwise;
import io.costax.hibernatetuning.bitwise.type.BitwiseType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;