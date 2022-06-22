ALTER TABLE project_document_buildings
ADD `master_certification_type_id` int;

ALTER TABLE project_document_buildings
DROP column `master_template_id`;

UPDATE project_document_buildings
SET master_certification_type_id = (SELECT MIN(ID) FROM master_certification_types)
WHERE master_certification_type_id IS NULL;
