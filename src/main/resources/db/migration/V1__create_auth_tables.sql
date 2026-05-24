create table app_user (
    id bigint not null auto_increment,
    username varchar(100) not null,
    password_hash varchar(255) not null,
    enabled boolean not null default true,
    account_non_expired boolean not null default true,
    account_non_locked boolean not null default true,
    credentials_non_expired boolean not null default true,
    display_name varchar(150) null,
    email varchar(150) null,
    last_login_at datetime null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    constraint pk_app_user primary key (id),
    constraint uk_app_user_username unique (username)
);

create table app_role (
    id bigint not null auto_increment,
    code varchar(100) not null,
    name varchar(150) not null,
    description varchar(255) null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    constraint pk_app_role primary key (id),
    constraint uk_app_role_code unique (code)
);

create table app_user_role (
    user_id bigint not null,
    role_id bigint not null,
    constraint pk_app_user_role primary key (user_id, role_id),
    constraint fk_app_user_role_user foreign key (user_id) references app_user (id),
    constraint fk_app_user_role_role foreign key (role_id) references app_role (id)
);

create index idx_app_user_role_role_id on app_user_role (role_id);

insert into app_role (code, name, description) values
    ('SYSTEM_ADMIN', 'System Admin', 'Full system-wide administrative access.'),
    ('SCHOOL_ADMIN', 'School Admin', 'School-level operational administration access.'),
    ('SCHOOL_STAFF', 'School Staff', 'Registrar and staff access for records and academics.'),
    ('SCHOOL_FINANCE', 'School Finance', 'Finance office access for billing and clearance.'),
    ('SCHOOL_CASHIER', 'School Cashier', 'Cashier access for payment posting and receipts.'),
    ('GUIDANCE_COUNSELOR', 'Guidance Counselor', 'Counselor access for attendance and limited student records.'),
    ('PARENT_GUARDIAN', 'Parent Guardian', 'Family portal access for guardians.'),
    ('STUDENT', 'Student', 'Student-facing access.');
