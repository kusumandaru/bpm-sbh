ALTER TABLE master_levels
ADD `master_template_id` int;

UPDATE master_levels set minimum_score = 56, percentage = 73 where id = 1;
UPDATE master_levels set minimum_score = 43, percentage = 57 where id = 2;
UPDATE master_levels set minimum_score = 35, percentage = 46 where id = 3;
UPDATE master_levels set minimum_score = 27, percentage = 35 where id = 4;

UPDATE master_levels
SET master_template_id = (SELECT MIN(ID) FROM master_templates)
WHERE master_template_id IS NULL;

INSERT INTO `master_levels` (id, name, minimum_score, percentage, active, created_at, updated_at, created_by, master_template_id)
VALUES
  (5, 'PLATINUM', 74, 73, 1, NOW(), NOW(),'system', 2),
  (6, 'GOLD', 58, 57, 1, NOW(), NOW(),'system', 2),
  (7, 'SILVER', 46, 46, 1, NOW(), NOW(),'system', 2),
  (8, 'BRONZE', 35, 35, 1, NOW(), NOW(),'system', 2);
