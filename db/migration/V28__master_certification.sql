DROP TABLE IF EXISTS `master_certification_types`;
CREATE TABLE IF NOT EXISTS `master_certification_types` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_vendor_id` int,
  `certification_code` varchar(255),
  `certification_name` varchar(255),
  `active` boolean DEFAULT TRUE,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

INSERT INTO `master_certification_types`
VALUES
  (1, 1, 'new_building', 'New Building', true, NOW(), NOW(),'system');

ALTER TABLE master_templates
ADD `master_certification_type_id` int;

UPDATE master_templates
SET master_certification_type_id = (SELECT MIN(ID) FROM master_certification_types)
WHERE master_certification_type_id IS NULL;
