DROP TABLE IF EXISTS `project_document_buildings`;
CREATE TABLE IF NOT EXISTS `project_document_buildings` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_template_id` int,
  `name` varchar(255),
  `code` varchar(255),
  `placeholder` varchar(255),
  `object_type` varchar(255),
  `mandatory` boolean,
  `active` boolean,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);