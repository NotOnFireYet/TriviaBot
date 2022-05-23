CREATE TABLE answers (
    answer_id integer NOT NULL,
    is_correct boolean,
    percent_picked integer NOT NULL,
    text character varying(100),
    question_id integer
);

CREATE SEQUENCE answers_answer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE question_stats (
    stats_id integer NOT NULL,
    answer_id integer,
    question_id integer,
    user_id bigint
);

CREATE SEQUENCE question_stats_stats_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE questions (
    question_id integer NOT NULL,
    correct_answer_reaction character varying(500),
    number_in_topic integer NOT NULL,
    text character varying(500),
    topic_id integer
);

CREATE SEQUENCE questions_number_in_topic_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE questions_question_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE scores (
    score_id integer NOT NULL,
    answered_questions integer NOT NULL,
    gained_money integer NOT NULL,
    is_successful boolean NOT NULL,
    user_id bigint
);

CREATE SEQUENCE scores_score_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE topics (
    topic_id integer NOT NULL,
    title character varying(100)
);

CREATE SEQUENCE topics_topic_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE users (
    user_id bigint NOT NULL,
    name character varying(255),
    username character varying(255)
);

ALTER TABLE answers ALTER COLUMN answer_id SET DEFAULT nextval('answers_answer_id_seq');

ALTER TABLE question_stats ALTER COLUMN stats_id SET DEFAULT nextval('question_stats_stats_id_seq');

ALTER TABLE questions ALTER COLUMN question_id SET DEFAULT nextval('questions_question_id_seq');

ALTER TABLE scores ALTER COLUMN score_id SET DEFAULT nextval('scores_score_id_seq');

ALTER TABLE topics ALTER COLUMN topic_id SET DEFAULT nextval('topics_topic_id_seq');

 ALTER TABLE answers
    ADD CONSTRAINT answers_pkey PRIMARY KEY (answer_id);

ALTER TABLE question_stats
    ADD CONSTRAINT question_stats_pkey PRIMARY KEY (stats_id);

ALTER TABLE questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (question_id);

ALTER TABLE scores
    ADD CONSTRAINT scores_pkey PRIMARY KEY (score_id);

ALTER TABLE topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (topic_id);

 ALTER TABLE questions
    ADD CONSTRAINT correct_reaction_unique UNIQUE (correct_answer_reaction);

ALTER TABLE questions
    ADD CONSTRAINT text_unique UNIQUE (text);

ALTER TABLE users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);

ALTER TABLE answers
    ADD CONSTRAINT answers_questions_pkey_ref FOREIGN KEY (question_id) REFERENCES questions(question_id);

ALTER TABLE question_stats
    ADD CONSTRAINT stats_questions_pkey_ref FOREIGN KEY (question_id) REFERENCES questions(question_id);

 ALTER TABLE questions
    ADD CONSTRAINT questions_topics_pkey_ref FOREIGN KEY (topic_id) REFERENCES topics(topic_id);

ALTER TABLE question_stats
    ADD CONSTRAINT stats_answers_pkey_ref FOREIGN KEY (answer_id) REFERENCES answers(answer_id);

 ALTER TABLE question_stats
    ADD CONSTRAINT stats_users_pkey_ref FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE scores
    ADD CONSTRAINT scores_users_pkey_ref FOREIGN KEY (user_id) REFERENCES users(user_id);