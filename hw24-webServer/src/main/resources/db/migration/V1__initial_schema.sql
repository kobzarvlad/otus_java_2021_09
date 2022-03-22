create table address
(
    id     bigserial    not null primary key,
    street varchar(200) not null
);

create table client
(
    id         bigserial   not null primary key,
    name       varchar(200) not null,
    login      varchar(50) not null unique,
    password   varchar(50) not null,
    address_id bigint references address
);

create table phone
(
    id        bigserial   not null primary key,
    number    varchar(20) not null,
    client_id bigint references client
);

insert into address (street)
values ('admins street');

insert into client (name, login, password, address_id)
values ('admin', 'admin', 'admin', 1);

insert into phone (number, client_id)
values ('+78887776655', 1), ('+79995554433', 1);
