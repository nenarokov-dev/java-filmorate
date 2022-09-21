# Схема базы данных:
![Untitled (4)](https://user-images.githubusercontent.com/102414108/191617480-fcc91177-a7aa-4c27-a517-a956540f63b1.png)

# ratings
**rating_id** - идентификатор возростного рейтинга фильма, первичный ключ;<br/>
**rating** - возростной рейтинга фильма;<br/>

# films
**film_id** - идентификатор фильма, первичный ключ;<br/>
**name** - название фильма;<br/>
**description** - описание фильма;<br/>
**release_date** - дата релиза фильма;<br/>
**duration** - продолжительность фильма;<br/>
**rating_id** - идентификатор рейтинга, внешний ключ к таблице **ratings**;<br/>

# users
**user_id** - идентификатор пользователя, первичный ключ;<br/>
**login** - логин пользователя;<br/>
**name** - имя пользователя;<br/>
**email** - почта пользователя;<br/>
**birthday** - дата рождения пользователя;<br/>

# friendship
**user_id** - идентификатор пользователя;<br/>
**friend_or_follower_id** - идентификатор друзей/подписчиков пользователя;<br/>
Комбинация **user_id** и **friend_or_follower_id** - первичный ключ<br/>

# likes
**film_id** - идентификатор фильма, которому ставится лайк;<br/>
**user_id** - идентификатор пользователя, который ставит лайк;<br/>
Комбинация **film_id** и **user_id** - первичный ключ<br/>

# genres
**genre_id** - идентификатор жанра фильма;<br/>
**genre** - жанр фильма;<br/>

# genres_of_films
**film_id** - идентификатор фильма, который хранит жанр;<br/>
**genre_id** - идентификатор жанра, который хранит фильм;<br/>
Комбинация **film_id** и **genre_id** - первичный ключ<br/>

```sql
    CREATE TABLE IF NOT EXISTS ratings (
      rating_id integer not null primary key, 
      rating varchar(60) 
    ); 
    
    CREATE TABLE IF NOT EXISTS users (
      user_id integer not null primary key,
      login varchar(40) not null,
      name varchar(40) not null,
      email varchar(60) not null,
      birthday date not null
    );
    
    CREATE TABLE IF NOT EXISTS films (
      film_id integer not null primary key,
      name varchar(100) not null,
      description varchar(200),
      release_date date not null,
      duration integer,
      rating_id integer not null references ratings
    );
    
    CREATE TABLE IF NOT EXISTS genres (
      genre_id integer primary key,
      genre varchar(100) not null
    );
    
    CREATE TABLE IF NOT EXISTS genres_of_films (
      film_id integer not null references films on delete cascade,
      genre_id integer not null references genres,
      primary key(film_id,genre_id)
    );
    
    CREATE TABLE IF NOT EXISTS friendship (
      user_id integer not null references users on delete cascade,
      friend_or_follower_id integer not null references users on delete cascade,
      primary key(user_id,friend_or_follower_id)
    );
    
    CREATE TABLE IF NOT EXISTS likes (
      film_id integer not null references films on delete cascade,
      user_id integer not null references users on delete cascade,
      primary key(film_id,user_id)
    );
```
# Примеры запросов к базе данных
**Получение списка общих друзей**
```sql
    SELECT f.friend_or_follower_id as user_id,u.login,u.name,u.email,u.birthday 
    FROM friendship as f 
    LEFT JOIN users as u ON u.user_id = f.friend_or_follower_id 
    WHERE f.user_id=? AND f.friend_or_follower_id IN  
    (SELECT friend_or_follower_id FROM friendship WHERE user_id=?);
```
**Получение списка всех пользователей**
```sql
    SELECT * FROM users;
```
**Получение списка всех фильмов**
```sql
    SELECT * FROM films AS f
    LEFT JOIN genres AS g ON f.genre_id=g.renre_id;
```
**Получение списка популярных фильмов**
```sql
    SELECT f.film_id,f.name,f.description,f.release_date,f.duration,f.rating_id,r.rating,count(user_id) AS likes 
    FROM likes AS l 
    LEFT JOIN films AS f ON l.film_id=f.film_id 
    LEFT JOIN ratings as r ON f.rating_id=r.rating_id 
    group by f.film_id,r.rating_id 
    ORDER BY c DESC 
    LIMIT ?;
```









