# The Open Source hibernate Types project

This is a open source project that provide some custom User Types implementations

## Supports a wide range of Hibernate ORM versions:
• 5.2
• 5.1 and 5.0 • 4.3
• 4.2 and 4.1

## Supports the Types
• JsonNode
• Type-safe Java objects to JSON
• PostgreSQL ARRAY
• NullableCharacter
• ImmutableType base class
• PostgreSQLEnumType


## Description

Depending on the Hibernate version employed in our project, we need to match the rigth hibernate -types dependency.


* For the Hibernate 5.2: we should use the **hibernate-types-52**

* For the Hibernate 5 and 5.1: **hibernate-types-5**   

* For the Hibernate 4.3: **hibernate-types-43**   

* For the Hibernate 4.2 and 4.1: **hibernate-types-4** 

Older version of Hibernate are not supported... :(


## Major feature:

* The JsonNodeBinaryType: allows to map a jackson JsonNode to a **binary JSON column type**

* The JsonBinaryType: allows to map any Java object as a **json binary column type**. So unlike Jackson JsonNode, the Java object retains a well defined object structure.

* The JsonStringType: allows to map any Java object as JSON string column. 


** The deference between a **string JSON column** and **binary JSON column** is that the string-based one will save the JSON as-is and the parsing at query time, so it's more efficient for writing data. 
** The binary JSON column does the parsing at insert time, so it's more efficient for reading data, since the JSON column does not need to be parsed on every query execution.



## NOTES About the 

* JavaTypeDescriptor is responsible for wrapping the Object coming from the JDBC ResultSet or for unwrapping the java Object so that it can be bound as a JDBC PreparedStatement parameter.

* SqlTypeDescriptor is responsible for binding the unwrapped Object as a JDBC PreparedStatement parameter or for fetching the JSON object from the underlying JDBC ResultSet.

