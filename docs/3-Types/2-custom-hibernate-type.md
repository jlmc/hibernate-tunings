# Hibernate Custom Types

Choosing the appropriate database type for each persistent property can really make a difference in terms of data access performance.

Even if Hibernate supports many Types, the application developer is not limited to the default ones only, and new Types can be added without too much effort.


### For example:

Assuming we need to store IPv4 addresses in the CIDR (Classless Inter-Domain Routing) format (e.g. 192.168.123.231/24).

You can use a:
* A BIGINT or NUMERIC(15) to store all 5 bytes (4 for the IP address, 1 for the subnet mask).
* VARCHAR(18) column type which to accommodate between 9 (e.g.0.0.0.0/0) and 18 (e.g. 192.168.123.231/24) characters.
* Database-specific type (e.g. PostgreSQL can store IPv4 addresses in cidr or inet column types). The inet type requires 7 bytes.


Storing IPv4 addresses as PostgreSQL inet type

The PostgreSQL inet type is specially designed for IPv4 and IPv6 network addresses.

PostgreSQL also supports various network address specific operators (e.g. <, >, &&), and address transforming functions like host(inet) or netmask(inet).


---

A example how a custom type is implemented can be found 'io.costax.hibernatetunning.customtypes.CustomTypesTest'

* Our test uses an Machine entity which declares an ip attribute of the type Ipv4.
* The IPv4 object type holds the underlying IP address in a String property. However, it also exposes a method to get the associated Java InetAddress.
* we can see that the entity 'Machine' uses the @TypeDef annotation to specify a custom Hibernate UserType.
* The same configuration can be done at package-info.java file level.


* IPv4Type, which is responsible for handling the IPv4 Java Object type.
* The IPv4Type looks rather straightforward because the bulk of UserType. All methods are already implemented by the ImmutableType base class.

* Just like Java primitive wrappers, String or Enum types, it's much easier to deal with immutable entity attributes. This way, we don't have to worry about implementing deepCopy, assemble, disassemble, replace and many other UserType methods which are only relevant for mutable types.


* The IPv4Type implements just three methods. The sqlTypes declares the JDBC Types used when binding the PreparedStatement parameter values or when fetching them via a JDBC ResultSet.

** The get method is used to retrieve the IPv4 object from the JDBC ResultSet. Notice that we can fetch the IP address as a String, and if the address is not null, we can use it to build a new IPv4 Object.

** The set method is used to bind the PreparedStatement parameter value either to null or to a PostgreSQL-specific PGobject which, apart from taking the IP address String representation, it also declares the expected PostgreSQL column type.

