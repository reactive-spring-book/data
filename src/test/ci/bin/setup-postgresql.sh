#!/usr/bin/env bash
psql -c 'CREATE DATABASE orders;' -U postgres
psql -c 'CREATE TABLE customers( id bigint not null, email varchar(255) not null );' -U postgres orders
