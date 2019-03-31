# Generating Primary Keys


How to generate primary keys is an important decision for each and every application. And it’s not just about uniqueness and ease of use. You need to create a lot of primary keys and should also consider the performance impacts of the different approaches.


- Primary key identifies entities

    @Id

- One or more columns
- Numerical most memory efficient
- Provided by application or generated


```
@GeneratedValue(strategy = GenerationType.SEQUENCE)
```


## JPA generation strategies

- **AUTO** - Depends on SQL dialect
- **IDENTITY** - Uses an auto-incremented column
- **SEQUENCE** - Uses a database sequence
- **TABLE** - Uses a table to emulate a sequence


## Identity

- Database generates the key on insert
    - Not available before Hibernate executes the INSERT
    - Can’t be used with Batching
    
    ```
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ```
    
## Sequence


- Preferred strategy
- Requires a database sequence
- Hibernate gets id before the INSERT
    - Can be used with Batching
    
    ```
    @Id
    @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "author_generator")
    @SequenceGenerator(
        name="author_generator", 
        sequenceName = "author_seq")
    
    ```
    
    
## Table

- Requires an additional table
- Can be used, if database doesn’t support sequences
- Table holds next numeric primary key value
    - One row for each entity

  ```
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  ```  


## Optimizations


Hibernate offers a great optimization called Enhanced-sequence that should be used whenever possible.

- Enhanced-sequence strategy
- Hibernate specific
- Should be used with new generators
    - Default in Hibernate 5
    - Requires activation with Hibernate 4

```
<property name="hibernate.id.new_generator_mappings" value="true"/>
```


#### Advantages

- Better database portability
    - Uses sequence, if supported
    - Hibernate decides based on dialect if a sequence or table is used
    - Uses table as fallback
- Supports different optimizers to reduce database roundtrips

We can see one example of configuration below GenericGenerator

```
@Id
@GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "author_generator"
)
@GenericGenerator(
    name = "author_generator",
    strategy = "enhanced-sequence",
    parameters = {
        @Parameter(name = "sequence_name", value = "author_seq"),
        @Parameter(name = "increment_size", value = "10"),
        @Parameter(name = "optimizer", value = "pooled-lo") 
    }
)
```

