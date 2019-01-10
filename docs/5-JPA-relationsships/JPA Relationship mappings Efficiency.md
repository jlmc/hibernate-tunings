JPA Relationship mappings Efficiency.

The Rule: 
- Every relationship that is controlled by a @ManyToOne or client-side @OneToOne association is going to be efficient.



## OneToMany

- Efficient:
	
	- @ManyToOne
	- @OneToMany(mappedBy=...)

- Less Efficient

	- @OneToMany @JoinColumn
	- @OneToMany Set<Book>
	- @OneToMany @OrderColumn(name = ...)

- Least Efficient

	- @OneToMany List<Book> 

---

## OneToOne

- Efficient:
	
	- @OneToOne @MapsId
	- @OneToOne(mappedBy=...) BE

- Less Efficient

	- @OneToOne(mappedBy=...)


- Least Efficient

	- 	na		

## ManyToMany

- Efficient:
	
	- @ManyToMany Set<Abc>
	- @ManyToOne @OneToMany

- Less Efficient

	- @ManyToMany @OrderColumn(name=...) List<Abc>


- Least Efficient

	- 	@ManyToMany List<Abc>

---


# Equals and hashCode


- According to the Java specification, a good equals implementation must have the following properties:

	- Reflexive(e.g.x=x)
	- Symmetric (e.g. ifx = y, theny = x)
	- Transitive (e.g. ifx = zandy = z, thenx = y) 
	- Consistent

- While the first three properties are rather intuitive and easy to explain, ensuring consistency, in the context of JPA and Hibernate entities, is usually a tough challenge.

- Consistency means that an entity has to have the same hashCode and to be equal to itself for every possible entity state transition:
	
	- Transient-->Managed
	- Managed-->Detached
	- Detached-->Managed
	- Managed-->Removed


-This methods are needed when we are using the entity a key in a java.util.Map implementation or the entity is stored in a collection that implements java.util.Set.

	- HashSet is implemented on top of HashMap which spreads elements over multiple buckets so, searching for an element is a two-step process.

		1. the hashCode is Used to Locate the rigth bucket. 
		2. all the elements in the found bucket are checked to see if there is one entry which is equal to the reference object.

Different objects can share the same hashCode because one bucket can have multiple enties. However, all objects that are equals must have the same hashCode, as otherwise, we won't be able to locate the rigth bucket.

Also, equals is needed by a java.util.Collection to:
	
	- find elements,
	- remove elements		 

 
	