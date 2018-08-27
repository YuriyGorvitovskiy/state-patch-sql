CREATE DATABASE state_patch_sql;
CREATE USER state_patch_sql WITH ENCRYPTED PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE state_patch_sql TO state_patch_sql;