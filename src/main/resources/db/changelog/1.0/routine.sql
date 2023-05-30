create function update_user_rating() returns trigger
    language plpgsql
as
$$
BEGIN
    UPDATE users
    SET rating = (
        SELECT AVG(votes.vote)
        FROM votes
                 JOIN adverts ON adverts.id = votes.advert_id
        WHERE adverts.user_id = users.id
    )
    WHERE users.id IN (
        SELECT adverts.user_id
        FROM adverts
                 JOIN votes ON votes.advert_id = adverts.id
        WHERE votes.advert_id = NEW.advert_id
    );

    RETURN NULL;
END;
$$;

alter function update_user_rating() owner to postgres;

create trigger update_user_rating_trigger
    after insert or update or delete
    on votes
    for each row
execute procedure update_user_rating();

