@TypeDefs({
        @TypeDef(name = "ipv4", defaultForType = IPv4.class, typeClass = IPv4Type.class),
        @TypeDef(name = "macaddr", defaultForType = MacAddr.class, typeClass = MacAddrType.class)
})
package io.costa.hibernatetunings.entities;

import io.costa.hibernatetunings.customtype.IPv4;
import io.costa.hibernatetunings.customtype.IPv4Type;
import io.costa.hibernatetunings.customtype.MacAddr;
import io.costa.hibernatetunings.customtype.MacAddrType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;