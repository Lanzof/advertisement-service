create table if not exists users
(
    id          bigint generated always as identity,
    ban         boolean default false,
    description varchar(255),
    email       varchar(255),
    first_name  varchar(255),
    last_name   varchar(255),
    password    varchar(255),
    phone       varchar(255),
    rating      real,
    role        varchar(255),
    constraint users_pkey
        primary key (id),
    constraint users_email_uindex
        unique (email)
);

