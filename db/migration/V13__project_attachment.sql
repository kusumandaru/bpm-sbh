DROP TABLE IF EXISTS `project_attachments`;
CREATE TABLE IF NOT EXISTS `project_attachments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `process_instance_id` varchar(255),
  `file_type` varchar(255),
  `filename` varchar(255),
  `blob_url` varchar(255),
  `link` varchar(255),
  `version` int,
  `uploader_id` varchar(255),
  `role` varchar(255),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);