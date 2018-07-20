create table if not exists Expenses (
    id int not null auto_increment,
    kind varchar(255) not null,
    description varchar(255),
    date date not null,
    amount double not null,
    deletedOn date default null,
    primary key (id)
) default charset=utf8;