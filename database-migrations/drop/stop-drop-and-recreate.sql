SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'postgres-demos';

DROP DATABASE IF EXISTS postgres-demos;

CREATE DATABASE postgres-demos WITH OWNER=postgres ENCODING ='UTF8’;