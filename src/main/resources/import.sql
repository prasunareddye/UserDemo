-- ROLES
insert into roles(name) values('USER');
insert into roles(name) values('ADMIN');

-- USERS
insert into users (id, first_name, last_name, username, password) values ('11111111-1111-1111-1111-111111111111', 'Phil', 'Ingwell', 'PhilIngwell', '$100801$C9/3Y1hA2+6eJTEALhV+5A==$ipi5s0djpXWe0EvTi9lFtU2SnCwAyy1bHoMVpZzV16U=');
insert into users (id, first_name, last_name, username, password) values ('22222222-2222-2222-2222-222222222222', 'Anna', 'Conda', 'AnnaConda', '$100801$v7DjKKDDQVtC3V2IhTBD1Q==$PQhrjGolsmANlzC11FFbMixaY4OqjLs3CAaz06rup6A=');

-- USER_ROLES
insert into user_roles(user_id, role_name) values ('11111111-1111-1111-1111-111111111111', 'USER');
insert into user_roles(user_id, role_name) values ('11111111-1111-1111-1111-111111111111', 'ADMIN');
insert into user_roles(user_id, role_name) values ('22222222-2222-2222-2222-222222222222', 'USER');