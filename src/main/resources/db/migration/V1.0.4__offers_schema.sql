CREATE TABLE offers
(
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    resume_uuid UUID NOT NULL,
    company_uuid UUID NOT NULL,
    price FLOAT NOT NULL,
    message TEXT,
    status_id INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT offers_uuid_unique UNIQUE (uuid),
    CONSTRAINT offers_resume_uuid_foreign FOREIGN KEY (resume_uuid) REFERENCES resumes (uuid) ON DELETE CASCADE,
    CONSTRAINT offers_company_uuid_foreign FOREIGN KEY (company_uuid) REFERENCES companies (uuid) ON DELETE CASCADE
);