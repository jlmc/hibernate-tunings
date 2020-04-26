# XA Transactions, and PostgreSQL

In brief steps for the solution are:

1. Search for postgresql.conf in PostgreSQL installation folder. Generally it will be available in [PostgreSQL installation folder]\data\
2. Open that file in a text editor and search for max_prepared_transactions.
3. If that property is commented (#`max_prepared_transactions`), then remove # symbol and enter some value (say 20).
    - Sets the maximum number of transactions that can be in the "prepared" state simultaneously (see PREPARE TRANSACTION). 
    - Setting this parameter to zero (which is the default) disables the prepared-transaction feature. 
    - This parameter can only be set at server start.
    
    - If you are not planning to use prepared transactions, this parameter should be set to zero to prevent accidental creation of prepared transactions. 
    - If you are using prepared transactions, you will probably want `max_prepared_transactions` to be at least as large as `max_connections`, so that every session can have a prepared transaction pending.
    - When running a standby server, you must set this parameter to the same or higher value than on the master server. Otherwise, queries will not be allowed in the standby server.
    - _Most applications do not use XA prepared transactions, so should set this parameter to 0. If you do require prepared transactions, you should set this equal to max_connections to avoid blocking. May require increasing kernel memory parameters._
    
4. Save that file
5. Restart PostgreSQL


## References: 

- [Enable postgresql xa transaction](http://techierg.blogspot.com/2015/01/glassfish-postgresql-xa-transaction.html)
- [Atomic Commit of Distributed Transactions](https://wiki.postgresql.org/wiki/Atomic_Commit_of_Distributed_Transactions)
- [Postgrres PREPARE TRANSACTION](https://www.postgresql.org/docs/9.3/sql-prepare-transaction.html)

- [Recommended way to configure max_prepared_transactions in Postgres on Kubernetes](https://stackoverflow.com/questions/45793501/recommended-way-to-configure-max-prepared-transactions-in-postgres-on-kubernetes)
- [postgresql.conf max_prepared_transactions doesn't work](https://stackoverflow.com/questions/13617192/postgresql-conf-max-prepared-transactions-doesnt-work)
- [Postgres Prepared Transactions vs Prepared Statements](https://stackoverflow.com/questions/32108592/postgres-prepared-transactions-vs-prepared-statements)
- [Postgresql XA Transaction Exception](https://stackoverflow.com/questions/45867536/postgresql-xa-transaction-exception)
- [Transaction roll back not working in Postgresql](https://stackoverflow.com/questions/21130094/transaction-roll-back-not-working-in-postgresql)