CREATE TABLE user_cache (
    cache_id integer NOT NULL,
    user_id bigint,
    topic_id integer,
    question_id integer,
    fifty_fifty_remains integer,
    audience_help_remains integer,
    call_friend_remains integer,
    state character varying(50),
    delete_message_id integer,
    refresh_message_id integer
);

CREATE SEQUENCE user_cache_cache_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

ALTER TABLE user_cache ALTER COLUMN cache_id SET DEFAULT nextval('user_cache_cache_id_seq');

ALTER TABLE user_cache
    ADD CONSTRAINT user_cache_pkey PRIMARY KEY (cache_id);

ALTER TABLE user_cache
    ADD CONSTRAINT cache_users_pkey_ref
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE,

    ADD CONSTRAINT cache_topics_pkey_ref
    FOREIGN KEY (topic_id)
    REFERENCES topics(topic_id),

    ADD CONSTRAINT cache_questions_pkey_ref
    FOREIGN KEY (question_id)
    REFERENCES questions(question_id),

    ADD CONSTRAINT user_id_unique
    UNIQUE (user_id);


ALTER TABLE users
    ADD COLUMN cache_id integer,
    ADD CONSTRAINT users_user_cache_fkey_ref
    FOREIGN KEY (cache_id)
    REFERENCES user_cache(cache_id);
