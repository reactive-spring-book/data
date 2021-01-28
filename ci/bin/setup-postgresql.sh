#!/usr/bin/env bash
cat setup-postgresql.sql | PGPASSWORD=orders psql -U orders -h localhost orders
# sudo -i -u postgres
# sudo -u postgres psql
# psql -c  "CREATE DATABASE orders;" -U postgres
# psql -c  "CREATE user orders with password  'orders'; grant all  " -U postgres
# psql -c 'create table customer( id  serial primary key, email varchar(255) not null);' -U postgres orders
