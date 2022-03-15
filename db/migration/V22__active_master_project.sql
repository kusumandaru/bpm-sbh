ALTER TABLE `master_vendors`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER `description` ;

ALTER TABLE `master_templates`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER project_version;

ALTER TABLE `master_evaluations`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER `name`;

ALTER TABLE `master_exercises`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER max_score;

ALTER TABLE `master_criterias`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER not_available;

ALTER TABLE `master_documents`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER `name`;

ALTER TABLE `master_criteria_blockers`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER blocker_id;

ALTER TABLE `master_levels`
ADD COLUMN `active` boolean DEFAULT TRUE AFTER `percentage`;

