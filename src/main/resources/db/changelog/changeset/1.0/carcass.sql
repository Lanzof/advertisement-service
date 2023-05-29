create table if not exists users
(
    id          bigint generated by default as identity,
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

alter table users
    owner to postgres;

create table if not exists adverts
(
    id            bigint generated by default as identity,
    date          timestamp(0),
    description   varchar(255),
    premium       boolean default false,
    price         double precision,
    title         varchar(255),
    user_id       bigint,
    ban           boolean,
    premium_end   date,
    premium_start timestamp(0),
    constraint advert_pkey
        primary key (id),
    constraint adverts_users_id_fk
        foreign key (user_id) references users
);

alter table adverts
    owner to postgres;

create table if not exists comments
(
    id        bigint generated always as identity,
    advert_id integer      not null,
    user_id   integer      not null,
    date      timestamp(0) not null,
    text      text         not null,
    ban       boolean,
    constraint comment_pk
        primary key (id),
    constraint comments_adverts_id_fk
        foreign key (advert_id) references adverts,
    constraint comments_users_id_fk
        foreign key (user_id) references users
);

alter table comments
    owner to postgres;

create table if not exists votes
(
    author_id integer      not null,
    date      timestamp(0) not null,
    vote      integer      not null,
    advert_id bigint       not null,
    constraint vote_pk
        primary key (author_id, advert_id),
    constraint vote_users_id_fk
        foreign key (author_id) references users,
    constraint vote_advert_id_fk
        foreign key (advert_id) references adverts
);

alter table votes
    owner to postgres;

create trigger update_user_rating_trigger
    after insert or update or delete
    on votes
    for each row
execute procedure update_user_rating();

create table if not exists chats
(
    id        bigint generated always as identity,
    advert_id integer not null,
    buyer_id  integer not null,
    constraint chat_pk
        primary key (id),
    constraint chats_adverts_id_fk
        foreign key (advert_id) references adverts,
    constraint chats_users_id_fk
        foreign key (buyer_id) references users
);

alter table chats
    owner to postgres;

create unique index if not exists chat_id_uindex
    on chats (id);

create table if not exists messages
(
    chat_id   integer      not null,
    text      text         not null,
    date      timestamp(0) not null,
    id        bigint generated always as identity,
    sender_id integer,
    constraint message_pkey
        primary key (id),
    constraint message_id_key
        unique (id),
    constraint message_chat_null_fk
        foreign key (chat_id) references chats,
    constraint messages_users_id_fk
        foreign key (sender_id) references users
);

alter table messages
    owner to postgres;

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
    constraint wallets_pk2
        unique (user_id),
    constraint uk_sswfdl9fq40xlkove1y5kc7kv
        unique (user_id),
    constraint wallets_users_id_fk
        foreign key (user_id) references users
);

alter table wallets
    owner to postgres;

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

alter table transactions_history
    owner to postgres;

create table if not exists services
(
    id          bigserial,
    description varchar(255),
    duration    integer,
    price       double precision,
    constraint services_pkey
        primary key (id)
);

alter table services
    owner to postgres;

