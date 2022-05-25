ALTER TABLE answers
    DROP CONSTRAINT answers_questions_pkey_ref,
    ADD CONSTRAINT answers_questions_pkey_ref
        FOREIGN KEY (question_id)
        REFERENCES questions(question_id)
        ON DELETE CASCADE;

ALTER TABLE question_stats
    DROP CONSTRAINT stats_questions_pkey_ref,
    ADD CONSTRAINT stats_questions_pkey_ref
        FOREIGN KEY (question_id)
        REFERENCES questions(question_id)
        ON DELETE CASCADE;

ALTER TABLE question_stats
    DROP CONSTRAINT stats_answers_pkey_ref,
    ADD CONSTRAINT stats_answers_pkey_ref
        FOREIGN KEY (answer_id)
        REFERENCES answers(answer_id)
        ON DELETE CASCADE;

ALTER TABLE question_stats
    DROP CONSTRAINT stats_users_pkey_ref,
    ADD CONSTRAINT stats_users_pkey_ref
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE;

ALTER TABLE questions
    DROP CONSTRAINT questions_topics_pkey_ref,
    ADD CONSTRAINT questions_topics_pkey_ref
        FOREIGN KEY (topic_id)
        REFERENCES topics(topic_id)
        ON DELETE CASCADE;

ALTER TABLE scores
    DROP CONSTRAINT scores_users_pkey_ref,
    ADD CONSTRAINT scores_users_pkey_ref
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE;