
create database test /** Creating Database **/
 
use test /** Selecting Database **/
 
create table users(
   id int(11) primary key auto_increment,
   unique_id varchar(23) not null unique,
   name varchar(50) not null,
   email varchar(100) not null unique,
   encrypted_password varchar(80) not null,
   salt varchar(10) not null,
   created_at datetime,
   updated_at datetime null
); /** Creating Users Table **/

create table Courses(
    id int(11) primary key auto_increment,
    title varchar(100),
    description text
);

create table Course_Favorite(
    user_id varchar(23),
    course_id int
);