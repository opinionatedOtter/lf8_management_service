create table hello
(
    id INT primary key GENERATED ALWAYS AS IDENTITY,
    message varchar(200) not null
);

insert into hello (message) VALUES('Hello World');
insert into hello (message) VALUES('interesting message');
insert into hello (message) VALUES('Hello World');
