drop table if exists customer ;
create table customer (
    id    serial  not null primary key,
    email varchar not null
);