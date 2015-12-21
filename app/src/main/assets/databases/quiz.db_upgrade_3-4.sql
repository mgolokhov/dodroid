DROP TABLE IF EXISTS questions;

CREATE TABLE IF NOT EXISTS statistics (
			 topic_num_id INTEGER NOT NULL,
             test_num_id INTEGER NOT NULL,
             question_num_id INTEGER NOT NULL,
             on_learning INTEGER,
             wrong_counter INTEGER,
             right_counter INTEGER,
             studied INTEGER,
             ignore INTEGER,
             PRIMARY KEY (topic_num_id, test_num_id, question_num_id))

ALTER TABLE statistics RENAME TO stat_tmp;

CREATE TABLE statistics (
			 topic_num_id INTEGER NOT NULL,
             test_num_id INTEGER NOT NULL,
             question_num_id INTEGER NOT NULL,
             on_learning INTEGER,
             wrong_counter INTEGER,
             right_counter INTEGER,
             studied INTEGER,
             ignore INTEGER,
             PRIMARY KEY (topic_num_id, test_num_id, question_num_id))

INSERT INTO statistics SELECT * FROM stat_tmp;