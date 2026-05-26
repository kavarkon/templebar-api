create table event (
    id bigserial primary key,
    title varchar(255) not null,
    image varchar(255),
    description text,
    scheduled_at timestamp with time zone not null
);

create table app_user (
    id bigserial primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null
);
