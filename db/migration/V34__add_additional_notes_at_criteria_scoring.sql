ALTER TABLE criteria_scorings
ADD `additional_notes` text;

UPDATE criteria_scorings
  INNER JOIN master_criterias ON criteria_scorings.master_criteria_id = master_criterias.id 
SET 
  criteria_scorings.additional_notes = master_criterias.additional_notes;

