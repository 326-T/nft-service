CREATE TABLE resumes
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    applicant_id UUID NOT NULL,
    education TEXT NOT NULL,
    experience TEXT NOT NULL,
    skills TEXT NOT NULL,
    interests TEXT NOT NULL,
    urls TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT resumes_applicant_id_fk FOREIGN KEY (applicant_id) REFERENCES applicants (id) ON DELETE CASCADE
);