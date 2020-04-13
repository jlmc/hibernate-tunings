@TypeDefs({
        @TypeDef(name = "ipv4", defaultForType = IPv4.class, typeClass = IPv4Type.class),
        @TypeDef(name = "macaddr", defaultForType = MacAddr.class, typeClass = MacAddrType.class),

        //@TypeDef(name = "string-array", defaultForType = String[].class, typeClass = StringArrayType.class),
        @TypeDef(name = "string-array", defaultForType = String[].class, typeClass = ImmutableStringArrayType.class),
        @TypeDef(name = "int-array", defaultForType = int[].class, typeClass = IntArrayType.class),
})
package io.costax.hibernatetunings.entities;

import io.costax.hibernatetunings.arrays.ImmutableStringArrayType;
import io.costax.hibernatetunings.arrays.IntArrayType;
import io.costax.hibernatetunings.customtype.IPv4;
import io.costax.hibernatetunings.customtype.IPv4Type;
import io.costax.hibernatetunings.customtype.MacAddr;
import io.costax.hibernatetunings.customtype.MacAddrType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;