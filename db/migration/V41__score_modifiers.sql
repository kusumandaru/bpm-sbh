DROP TABLE IF EXISTS `master_score_modifiers`;
CREATE TABLE `master_score_modifiers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_exercise_id` int,
  `title` varchar(255),
  `description` text,
  `score_modifier` float DEFAULT '0.00',
  `active` boolean,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255)
);

DROP TABLE IF EXISTS `exercise_score_modifiers`;
CREATE TABLE `exercise_score_modifiers` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_score_modifier_id` int,
  `project_assessment_id` int,
  `exercise_assessment_id` int,
  `score_modifier` float DEFAULT '0.00',
  `enabled` boolean,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(255)
);
