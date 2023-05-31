create table if not exists transactions_history
(
    id          bigserial,
    date        timestamp(0) not null,
    description varchar(255),
    operation   varchar(255),
    sum         double precision,
    wallet_id   bigint,
    constraint transactions_history_pkey
        primary key (id),
    constraint transactions_history_wallets_id_fk
        foreign key (wallet_id) references wallets
);

