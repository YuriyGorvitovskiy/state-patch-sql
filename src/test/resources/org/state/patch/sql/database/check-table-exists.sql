SELECT EXISTS (
   SELECT 1
       FROM  pg_tables
       WHERE   schemaname = 'public'
           AND tablename = ?
);