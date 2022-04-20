ALTER TABLE `master_exercises`
ADD `score_modifier` float DEFAULT '0.00';

ALTER TABLE `exercise_assessments`
ADD `score_modifier` float DEFAULT '0.00';

ALTER TABLE `project_assessments`
ADD `score_modifier` float DEFAULT '0.00';