#!/usr/bin/env bash
cat setup-postgresql.sql | PGPASSWORD=orders psql -U orders -h localhost orders
