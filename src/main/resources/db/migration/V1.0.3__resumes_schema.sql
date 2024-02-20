CREATE TABLE resumes
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    applicant_uuid UUID NOT NULL,
    education TEXT,
    experience TEXT,
    skills TEXT,
    interests TEXT,
    urls TEXT,
    picture TEXT,
    mint_status_id INTEGER NOT NULL DEFAULT 0,
    minimum_price FLOAT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT resumes_uuid_unique UNIQUE (uuid),
    CONSTRAINT resumes_applicant_uuid_foreign FOREIGN KEY (applicant_uuid) REFERENCES applicants (uuid) ON DELETE CASCADE
);