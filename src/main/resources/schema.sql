CREATE TABLE IF NOT EXISTS tags (
                                    id BIGINT PRIMARY KEY,
                                    name VARCHAR(255) UNIQUE NOT NULL,
                                    color VARCHAR(7)
);

CREATE TABLE IF NOT EXISTS tasks (
                                     id BIGINT PRIMARY KEY,
                                     custom_id VARCHAR(255) UNIQUE NOT NULL,
                                     title VARCHAR(255) NOT NULL,
                                     description TEXT,
                                     priority VARCHAR(20),
                                     status VARCHAR(20),
                                     created_at TIMESTAMP,
                                     completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_tags (
                                         task_id BIGINT,
                                         tag_id BIGINT,
                                         PRIMARY KEY (task_id, tag_id),
                                         FOREIGN KEY (task_id) REFERENCES tasks(id),
                                         FOREIGN KEY (tag_id) REFERENCES tags(id)
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT PRIMARY KEY,
                                        content TEXT,
                                        created_at TIMESTAMP,
                                        task_id BIGINT,
                                        FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS task_references (
                                               from_task_id BIGINT,
                                               to_task_id BIGINT,
                                               PRIMARY KEY (from_task_id, to_task_id),
                                               FOREIGN KEY (from_task_id) REFERENCES tasks(id),
                                               FOREIGN KEY (to_task_id) REFERENCES tasks(id)
);