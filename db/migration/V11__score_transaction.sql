ALTER TABLE `project_assessments`
  CHANGE `possible_score` `submitted_score` float,
  CHANGE `temporary_score` `approved_score` float;

ALTER TABLE `criteria_scorings`
  CHANGE `potential_score` `submitted_score` float,
  CHANGE `score` `approved_score` float;
  
ALTER TABLE `criteria_scorings`
  ADD COLUMN `exercise_assessment_id` int after `project_assessment_id`;

ALTER TABLE `exercise_assessments`
  ADD COLUMN `submitted_score` float AFTER `selected`,
  ADD COLUMN `approved_score` float AFTER `submitted_score`;