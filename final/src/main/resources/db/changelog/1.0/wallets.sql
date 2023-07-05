create table if not exists wallets
(
    user_id bigint not null,
    balance double precision default 0,
        constraint wallets_pk
            primary key (user_id),
    constraint wallets_users_id_fk
        foreign key (user_id) references users
            on delete cascade
);

