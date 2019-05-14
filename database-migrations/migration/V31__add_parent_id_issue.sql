alter table issue
    add parent_id bigint references issue (id);