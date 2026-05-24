insert into app_user (
    username,
    password_hash,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    display_name,
    email
) values
    ('sysadmin', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'System Administrator', null),
    ('principal', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Principal', null),
    ('registrar', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Registrar', null),
    ('finance', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Finance Office', null),
    ('cashier', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Cashier', null),
    ('counselor', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Guidance Counselor', null),
    ('guardian', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Parent Guardian', null),
    ('student', '{bcrypt}$2a$10$CFmAr2baQUZC..fp1JvU2OxSG0idFSEovXSZOoMAAOp8Wck0Aex3e', true, true, true, true, 'Student', null);

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'SYSTEM_ADMIN'
where u.username = 'sysadmin';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'SCHOOL_ADMIN'
where u.username = 'principal';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'SCHOOL_STAFF'
where u.username = 'registrar';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'SCHOOL_FINANCE'
where u.username = 'finance';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'SCHOOL_CASHIER'
where u.username = 'cashier';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'GUIDANCE_COUNSELOR'
where u.username = 'counselor';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'PARENT_GUARDIAN'
where u.username = 'guardian';

insert into app_user_role (user_id, role_id)
select u.id, r.id
from app_user u
join app_role r on r.code = 'STUDENT'
where u.username = 'student';
