DROP DATABASE IF EXISTS COURSE_MANAGEMENT_TEST;

CREATE DATABASE COURSE_MANAGEMENT_TEST;

USE COURSE_MANAGEMENT_TEST;

create table ADDRESS (
                         ID integer not null auto_increment,
                         STREET varchar(255),
                         ZIP varchar(255),
                         primary key (ID)
) engine=InnoDB;

create table COURSE (
                        ID integer not null auto_increment,
                        YEAR integer,
                        NAME varchar(255),
                        primary key (ID)
) engine=InnoDB;

create table GENRE (
                       ID integer not null auto_increment,
                       DESCRIPTION varchar(255),
                       primary key (ID)
) engine=InnoDB;

create table STUDENT (
                         ADDRESS integer,
                         BORN_ON date,
                         GENRE integer,
                         ID integer not null auto_increment,
                         FIRST_NAME varchar(255),
                         LAST_NAME varchar(255),
                         primary key (ID)
) engine=InnoDB;

create table STUDENT_COURSE (
                                COURSE integer not null,
                                STUDENT integer not null,
                                primary key (STUDENT, COURSE)
) engine=InnoDB;

alter table STUDENT
    add constraint UKciuu3iamsfp7tj25pgym2ncfi unique (ADDRESS);

alter table STUDENT
    add constraint FKktj1p7d6y3fw09dx3smwav3u3
        foreign key (ADDRESS)
            references ADDRESS (ID);

alter table STUDENT
    add constraint FK48tp1lwr575aqu9fm1r87lm3w
        foreign key (GENRE)
            references GENRE (ID);

alter table STUDENT_COURSE
    add constraint FK7xfylc8mnnlu8mv1dv8obaa5j
        foreign key (COURSE)
            references COURSE (ID);

alter table STUDENT_COURSE
    add constraint FK2npgckoeg9mggtxixkdislxk0
        foreign key (STUDENT)
            references STUDENT (ID);