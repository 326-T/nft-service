CREATE TABLE resumes
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    applicant_uuid UUID NOT NULL,
    education TEXT NOT NULL,
    experience TEXT NOT NULL,
    skills TEXT NOT NULL,
    interests TEXT NOT NULL,
    urls TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT resumes_uuid_unique UNIQUE (uuid),
    CONSTRAINT resumes_applicant_uuid_foreign FOREIGN KEY (applicant_uuid) REFERENCES applicants (uuid) ON DELETE CASCADE
);