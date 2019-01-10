do
$$
begin
  if not exists (select column_name
                 from information_schema.columns
                 where table_schema='tasks' and table_name='todo_comment' and column_name='attachment') then

   alter table tasks.todo_comment add attachment bytea;

  else
    raise notice 'tasks.todo_comment.attachment already exists';
  end if;
end
$$
