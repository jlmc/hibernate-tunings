# Hibernate maven enhance plugin - enable Lazy Initialization 


if we are using the JDK 11 we need to specify the arg argument -Dnet.bytebuddy.experimental=true like the following:
```
  mvn clean install -rf :hibernate-open-source-custom-types-project -Dmaven.test.skip=true -X -Dnet.bytebuddy.experimental=true
```  

This project aims to demonstrate how:
 
* Solve the N + 1 problem in @OneToOne relationship
* Impacts of enhance plugin on @ManyToOne relationship when this is marked with fetch = LAZY


Other notes:
 - https://www.thoughts-on-java.org/best-practices-many-one-one-many-associations-mappings/