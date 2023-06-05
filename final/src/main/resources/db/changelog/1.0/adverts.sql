create table if not exists adverts
(
    id            bigint generated always as identity,
    title         varchar(255),
    description   varchar(255),
    date          date,
    price         double precision,
    user_id       bigint,
    ban           boolean,
    premium_end   date,
    premium_start date,
    constraint advert_pkey
        primary key (id),
    constraint adverts_users_id_fk
        foreign key (user_id) references users
);

