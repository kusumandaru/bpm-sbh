/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

INSERT INTO `master_levels`
VALUES
  (1, 'PLATINUM', 74, 73, NOW(), NOW(),'system'),
  (2, 'GOLD', 58, 57, NOW(), NOW(),'system'),
  (3, 'SILVER', 46, 46, NOW(), NOW(),'system'),
  (4, 'BRONZE', 35, 35, NOW(), NOW(),'system');

RENAME TABLE exercise_assessment TO exercise_assessments;

DROP TABLE IF EXISTS `document_files`;
CREATE TABLE IF NOT EXISTS `document_files` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `master_document_id` int,
  `criteria_scoring_id` int,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

DROP TABLE IF EXISTS `attachments`;
CREATE TABLE IF NOT EXISTS `attachments` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `document_file_id` int,
  `filename` varchar(255),
  `link` varchar(255),
  `uploader_id` varchar(255),
  `role` varchar(255),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255)
);

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
ALTER TABLE `criteria_scorings`
  ADD COLUMN `approval_status` int AFTER `potential_score`;
/*!40101 SET character_set_client = @saved_cs_client */;  

ALTER TABLE `comments`
  change user_id user_id varchar(255);