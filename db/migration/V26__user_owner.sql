INSERT INTO `ACT_ID_GROUP` (ID_, REV_, NAME_)
VALUES ('superuser', 1, 'Super User');

INSERT INTO `ACT_ID_GROUP` (ID_, REV_, NAME_)
VALUES ('verificator', 1, 'Verificator');

ALTER TABLE `ACT_ID_USER`
ADD (`TENANT_OWNER_` boolean DEFAULT true);

DROP TABLE IF EXISTS `project_users`;
CREATE TABLE IF NOT EXISTS `project_users` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `user_id` varchar(255),
  `invited_by` varchar(255),
  `tenant_id` varchar(255),
  `process_instance_id` varchar(255),
  `owner` boolean DEFAULT false
);