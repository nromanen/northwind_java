create table if not exists categories (id bigserial primary key, avatar varchar not null, title varchar not null UNIQUE);
create table if not exists clubs (id bigserial primary key,title varchar not null,description varchar not null,image_url varchar,category_id int8 references categories(id));
create table if not exists children (id bigserial primary key,first_name varchar not null,last_name varchar not null,birth_date date);
create table if not exists club_child(club_id int8 references clubs(id),child_id int8 references children(id));