# XA Transactions (2 Phase Commit)


**Java Transaction API**, more commonly known as `JTA`, is an API for managing transactions in Java. It allows us to start, commit and rollback transactions in a resource-agnostic way.

The true power of JTA lies in its ability to manage multiple resources (i.e. databases, messaging services) in a single transaction.


## Transaction Concepts

- A transaction consists of two or more actions, which must either all succeed or all fail. A successful outcome is a commit, and a failed outcome is a rollback. In a rollback, each member’s state is reverted to its state before the transaction attempted to commit.

- The typical standard for a well-designed transaction is that it is Atomic, Consistent, Isolated, and Durable (ACID).

## About ACID Properties for Transactions

ACID is an acronym which stands for `Atomicity`, `Consistency`, `Isolation`, and `Durability`. This terminology is usually used in the context of databases or transactional operations.

- Atomicity
    - For a transaction to be atomic, all transaction members must make the same decision. 
    - Either they all commit, or they all roll back. 
    - If atomicity is broken, what results is termed a heuristic outcome.

- Consistency
    - Consistency means that data written to the database is guaranteed to be valid data, in terms of the database schema. 
    - The database or other data source must always be in a consistent state. One example of an inconsistent state would be a field in which half of the data is written before an operation aborts. 
    - A consistent state would be if all the data were written, or the write were rolled back when it could not be completed.

- Isolation
    - Isolation means that data being operated on by a transaction must be locked before modification, to prevent processes outside the scope of the transaction from modifying the data.
    
- Durability
    - Durability means that in the event of an external failure after transaction members have been instructed to commit, all members will be able to continue committing the transaction when the failure is resolved. 
    - This failure can be related to hardware, software, network, or any other involved system.


## About the Transaction Coordinator or Transaction Manager

- The terms Transaction Coordinator and `Transaction Manager (TM)` are mostly interchangeable in terms of transactions with Application Servers. The term Transaction Coordinator is usually used in the context of distributed JTS transactions.

- In JTA transactions, the TM runs within Application Server and communicates with transaction participants during the `two-phase commit protocol`.

The TM tells transaction participants whether to commit or roll back their data, depending on the outcome of other transaction participants. In this way, it ensures that transactions adhere to the ACID standard.


## About Transaction Participants

- A transaction participant is any resource within a transaction that has the ability to commit or to roll back state. It is generally a `database` or a `JMS broker`, but by implementing the transaction interface, application code could also act as a transaction participant. 
- Each participant of a transaction independently decides whether it is able to commit or roll back its state, and only if all participants can commit does the transaction as a whole succeed. Otherwise, each participant rolls back its state, and the transaction as a whole fails. 
- The `TM` coordinates the commit or rollback operations and determines the outcome of the transaction.


## About Java Transaction API (JTA)

Java Transaction API (JTA) is part of Java Enterprise Edition specification. It is defined in JSR 907: [Java™ Transaction API (JTA)](https://jcp.org/en/jsr/detail?id=907).

Implementation of JTA is done using the TM, which is covered by project Narayana for JBoss EAP application server. The TM allows applications to assign various resources, for example, database or JMS brokers, through a single global transaction. The global transaction is referred as an XA transaction. Generally resources with XA capabilities are included in such transactions, but non-XA resources could also be part of global transactions. There are several optimizations which help non-XA resources to behave as XA capable resources. For more information, see LRCO Optimization for Single-phase Commit.

In this document, the term JTA refers to two things:

The Java Transaction API, which is defined by Java EE specification.
It indicates how the TM processes the transactions.
The TM works in JTA transactions mode, the data is shared in memory, and the transaction context is transferred by remote EJB calls. In JTS mode, the data is shared by sending Common Object Request Broker Architecture (CORBA) messages and the transaction context is transferred by IIOP calls. 

# Notes:

- What causes Arjuna 1603 (Could not find new XAResource to use for recovering non-serializable XAResource)
   - To get rid of the error, stop the jboss instance and remove the folder `$JBOSS/standalone/data/tx-object-store`

