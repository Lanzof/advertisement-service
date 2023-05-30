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

create unique index if not exists chat_id_uindex
    on chats (id);

