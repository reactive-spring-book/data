#!/usr/bin/env bash

sudo apt install postgresql postgresql-contrib
# sudo -i -u postgres
# sudo -u postgres psql
psql -c 'CREATE DATABASE orders;' -U postgres
psql -c 'create table customer( id  serial primary key, email varchar(255) not null);' -U postgres orders
