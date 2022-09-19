CREATE TABLE IF NOT EXISTS ratings
(
    rating_id integer not null primary key,
    rating varchar(60)
);


CREATE TABLE IF NOT EXISTS users
(
    user_id integer not null primary key,
    login varchar(40) not null,
    name varchar(40) not null,
    email varchar(60) not null,
    birthday date not null
);

CREATE TABLE IF NOT EXISTS films
(
    film_id integer not null primary key,
    name varchar(100) not null,
    description varchar(200),
    release_date date not null,
    duration integer,
    rating_id integer not null references ratings
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id integer primary key,
    genre varchar(100) not null

);

CREATE TABLE IF NOT EXISTS genres_of_films
(
    film_id integer not null references films on delete cascade,
    genre_id integer not null references genres,
    primary key(film_id,genre_id)
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id integer not null references users on delete cascade,
    friend_or_follower_id integer not null references users on delete cascade,
    primary key(user_id,friend_or_follower_id)
);


CREATE TABLE IF NOT EXISTS likes
(
    film_id integer not null references films on delete cascade,
    user_id integer not null references users on delete cascade,
    primary key(film_id,user_id)
);



