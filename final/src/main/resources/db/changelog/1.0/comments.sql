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

