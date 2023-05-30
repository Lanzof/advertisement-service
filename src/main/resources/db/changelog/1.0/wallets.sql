create table if not exists wallets
(
    id      bigserial,
    balance double precision default 0,
    user_id bigint not null,
    constraint wallets_pkey
        primary key (id),
    constraint wallets_user_id_uindex
        unique (user_id),
    constraint wallets_pk
        unique (user_id),
    constraint wallets_users_id_fk
        foreign key (user_id) references users
);

