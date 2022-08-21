DROP TABLE IF EXISTS `project_document_categories`;
CREATE TABLE IF NOT EXISTS `project_document_categories` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(255),
  `name` varchar(255),
  `description` varchar(255),
  `active` boolean,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

INSERT INTO `project_document_categories` (id, code, name, description, active, created_at, updated_at, created_by )
VALUES 
(1, 'eligibility', 'Eligibility Document', 'Eligibility Document for Project Registration', true, NOW(), NOW(), 'system'),
(2, 'registered_project', 'Registered Project Document', 'Registered Project Document', true, NOW(), NOW(), 'system'),
(3, 'evaluation_project', 'Evaluation Project Document', 'Evaluation Project Document', true, NOW(), NOW(), 'system');
