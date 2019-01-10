create sequence timesheet_id_sequence start 1 increment 4;

CREATE TABLE timesheet
(
    id bigint PRIMARY KEY NOT NULL,
    developer_id bigint NOT NULL,
    start timestamp with time zone NOT NULL,
    until timestamp with time zone NOT NULL,
    description text,
    CONSTRAINT timesheet_developer_id_fk FOREIGN KEY (developer_id) REFERENCES public.developer (id)
);
CREATE UNIQUE INDEX timesheet_id_uindex ON public.timesheet (id);