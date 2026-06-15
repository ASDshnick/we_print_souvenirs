create table users (
    id bigint primary key generated always as identity,
    name varchar(255) not null,
    username varchar(255) not null,
    password varchar(255) not null,
    email varchar(255),
    phone varchar(255),
    role varchar(255) not null default 'USER',
    admin_note text,
    registered_at timestamp default current_timestamp,
    last_login_at timestamp default current_timestamp
);

create table orders (
    id bigint primary key generated always as identity,
    user_id bigint not null references users(id) on delete cascade,
    customer_username varchar(255),
    customer_email varchar(255),
    customer_phone varchar(255),
    total_amount bigint,
    status varchar(50) default 'NEW',
    created_at timestamp default current_timestamp,
    payment_method varchar(50) not null default 'CARD',
    payment_status varchar(50) not null default 'NOT_PAID',
    admin_note text
);

create table products (
    id bigint primary key generated always as identity,
    name varchar(255),
    description text,
    price bigint
);

create table order_items (
    id bigint primary key generated always as identity,
    order_id bigint not null references orders(id) on delete cascade,
    product_id bigint not null references products(id),
    quantity int default 5,
    size varchar(50) default 'SMALL',
    color varchar(50) default 'WHITE',
    price_per_item bigint,
    comment varchar(1000)
);

create table cart (
    id bigint primary key generated always as identity,
    user_id bigint not null references users(id) on delete cascade,
    product_id bigint not null references products(id),
    quantity int default 5,
    size varchar(50) default 'SMALL',
    color varchar(50) default 'WHITE',
    price_per_item bigint,
    comment varchar(1000)
);

create table pricing_rules (
    id bigserial primary key,
    product_id bigint not null references products(id),
    rule_type varchar(20) not null,
    key_value varchar(50) not null,
    max_value varchar(50),
    adjustment int not null
);

create table chat_messages (
    id bigserial primary key,
    order_id bigint not null references orders(id),
    sender_username varchar(255) not null,
    content text not null,
    sent_at timestamp,
    from_admin boolean
);

insert into products (name, description, price) values
('Индивидуальный заказ', 'Изготовление модели на заказ', 650),
('Разработка сувенирной продукции', 'Исследование вашего региона, разработка концепций для утверждения. Формирование макетов и их печать', 450),
('Карта города', 'Создание модели по заданному месту, утверждение и печать', 900);

insert into pricing_rules (product_id, rule_type, key_value, adjustment) values
(1, 'SIZE', 'MEDIUM', 350),
(1, 'SIZE', 'BIG', 550),
(1, 'SIZE', 'LARGE', 1150),
(2, 'SIZE', 'MEDIUM', 350),
(2, 'SIZE', 'BIG', 550),
(2, 'SIZE', 'LARGE', 1150);

insert into pricing_rules (product_id, rule_type, key_value, adjustment) values
(1, 'COLOR', 'GRAY', 10),
(1, 'COLOR', 'OTHERS', 60),
(2, 'COLOR', 'GRAY', 10),
(2, 'COLOR', 'OTHERS', 60),
(3, 'COLOR', 'COLORED', 900),
(3, 'COLOR', 'BLACK', 0);

insert into pricing_rules (product_id, rule_type, key_value, max_value, adjustment) values
(1, 'QUANTITY', '6', '20', 100),
(1, 'QUANTITY', '21', '50', 200),
(1, 'QUANTITY', '51', '100', 300),
(1, 'QUANTITY', '101', '200', 350),
(1, 'QUANTITY', '201', NULL, 400);

insert into pricing_rules (product_id, rule_type, key_value, max_value, adjustment) values
(2, 'QUANTITY', '6', '20', 100),
(2, 'QUANTITY', '21', '50', 100),
(2, 'QUANTITY', '51', '100', 150),
(2, 'QUANTITY', '101', '200', 120),
(2, 'QUANTITY', '201', NULL, 140);

insert into pricing_rules (product_id, rule_type, key_value, max_value, adjustment) values
(3, 'QUANTITY', '11', NULL, 100);
