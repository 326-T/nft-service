CREATE VIEW offer_detail_view AS
    SELECT
        o.id,
        o.uuid,
        o.resume_uuid,
        o.company_uuid,
        o.price,
        o.message,
        o.status_id,
        o.created_at,
        o.updated_at,
        c.name AS company_name,
    FROM offers o
    JOIN companies c ON o.company_uuid = c.uuid
    ORDER BY o.status_id ASC, o.updated_at ASC;