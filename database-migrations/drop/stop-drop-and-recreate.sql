SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'postgresdemos';

DROP DATABASE IF EXISTS postgresdemos;

CREATE DATABASE postgresdemos WITH OWNER=postgres ENCODING ='UTF8';