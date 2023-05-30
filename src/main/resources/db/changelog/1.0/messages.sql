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

