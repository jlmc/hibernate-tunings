@TypeDefs({

        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),


        // ****
        // My implementation custom types
        // ******
        @TypeDef(name = "ipv4", defaultForType = IPv4.class, typeClass = IPv4Type.class),
        @TypeDef(name = "macaddr", defaultForType = MacAddr.class, typeClass = MacAddrType.class)
})
package io.costax.hibernatetunings.entities;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import io.costax.hibernatetunings.customtype.IPv4;
import io.costax.hibernatetunings.customtype.IPv4Type;
import io.costax.hibernatetunings.customtype.MacAddr;
import io.costax.hibernatetunings.customtype.MacAddrType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;