create table categories
(
    id         bigint generated always as identity,
    created_at timestamp(6) not null,
    deleted    boolean      not null,
    deleted_at timestamp(6),
    name       varchar(255) not null,
    updated_at timestamp(6),
    primary key (id)
);

create table images
(
    id            bigint generated always as identity,
    archive_name  varchar(255) not null,
    content_type  varchar(255) not null,
    created_at    timestamp(6) not null,
    deleted       boolean      not null,
    deleted_at    timestamp(6),
    original_name varchar(255) not null,
    size          bigint       not null,
    url           varchar(255) not null,
    primary key (id)
);

create table products
(
    id          bigint generated always as identity,
    created_at  timestamp(6)   not null,
    deleted     boolean        not null,
    deleted_at  timestamp(6),
    description varchar(255)   not null,
    name        varchar(255)   not null,
    price       numeric(38, 2) not null,
    seller_id   bigint         not null,
    updated_at  timestamp(6),
    image_id    bigint,
    primary key (id)
);

create table products_categories
(
    product_id  bigint not null,
    category_id bigint not null,
    primary key (product_id, category_id)
);
