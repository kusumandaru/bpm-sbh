ALTER TABLE project_document_buildings
ADD `project_document_category_id` int;

UPDATE project_document_buildings
SET project_document_category_id = (SELECT MIN(ID) FROM project_document_categories)
WHERE project_document_category_id IS NULL;