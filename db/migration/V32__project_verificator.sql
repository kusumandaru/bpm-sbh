DROP TABLE IF EXISTS `project_verificators`;
CREATE TABLE IF NOT EXISTS `project_verificators` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` varchar(255),
  `invited_by` varchar(255),
  `process_instance_id` varchar(255),
  `group_id` varchar(255)
);