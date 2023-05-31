create table if not exists services
(
    id          bigserial,
    description varchar(255),
    duration    integer,
    price       double precision,
    constraint services_pkey
        primary key (id)
);

