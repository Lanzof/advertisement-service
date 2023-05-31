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

