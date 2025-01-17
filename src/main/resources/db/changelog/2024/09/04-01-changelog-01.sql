-- liquibase formatted sql
-- changeset Add entities tables
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS client (
    client_uuid UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS account (
    account_uuid UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    client_uuid UUID REFERENCES client (client_uuid) NOT NULL,
    account_type VARCHAR(16),
    balance DECIMAL(19,2),
    status VARCHAR(16) NOT NULL,
    frozen_amount DECIMAL(19, 2)
);

CREATE TABLE IF NOT EXISTS tbl_transaction (
    transaction_uuid UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    account_uuid UUID REFERENCES account (account_uuid) NOT NULL,
    amount DECIMAL(19,2),
    status VARCHAR(16) NOT NULL,
    created TIMESTAMPTZ
);
