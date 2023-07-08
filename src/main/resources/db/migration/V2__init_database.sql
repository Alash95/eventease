CREATE TABLE IF NOT EXISTS user
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    first_name   VARCHAR(255)          NULL,
    last_name    VARCHAR(255)          NULL,
    email        VARCHAR(255)          NULL,
    phone_number VARCHAR(255)          NULL,
    password     VARCHAR(255)          NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    event_name    VARCHAR(255)          NULL,
    `description` VARCHAR(255)          NULL,
    start_date    datetime              NULL,
    end_date      datetime              NULL,
    location      VARCHAR(255)          NULL,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event_user
(
    event_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    CONSTRAINT pk_event_user PRIMARY KEY (event_id, user_id)
);

CREATE TABLE IF NOT EXISTS user_role
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    CONSTRAINT pk_userrole PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

ALTER TABLE user_role
    ADD CONSTRAINT uc_userrole_name UNIQUE (name);

ALTER TABLE event_user
    ADD CONSTRAINT fk_eveuse_on_event FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE event_user
    ADD CONSTRAINT fk_eveuse_on_user FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user_role FOREIGN KEY (role_id) REFERENCES user_role (id);