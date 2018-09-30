#!/usr/bin/env bash
psql -c 'CREATE DATABASE orders;' -U postgres
psql -c 'create table customer( id  serial primary key, email varchar(255) not null);' -U postgres orders
