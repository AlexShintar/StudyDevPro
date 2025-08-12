create sequence address_seq start with 1 increment by 1;
create sequence phone_seq start with 1 increment by 1;

create table address
(
    id bigint not null primary key,
    address_street varchar(50) not null
);

create table phone
(
    id bigint not null primary key,
    phone_number varchar(50) not null,
    client_id bigint,
    constraint fk_phone_client foreign key (client_id) references client(id)
);

alter table client add column address_id bigint;
alter table client add constraint fk_client_address foreign key (address_id) references address(id);
alter table client add constraint uq_client_address unique (address_id);
