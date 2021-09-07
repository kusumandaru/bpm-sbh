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

--
-- Table structure for table `master_admins`
--

DROP TABLE IF EXISTS `master_admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `master_admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `manager_name` varchar(255) COLLATE utf8_unicode_ci,
  `manager_signature` varchar(255) COLLATE utf8_unicode_ci,
  `registration_letter` varchar(255) COLLATE utf8_unicode_ci,
  `first_attachment` varchar(255) COLLATE utf8_unicode_ci,
  `second_attachment` varchar(255) COLLATE utf8_unicode_ci,
  `third_attachment` varchar(255) COLLATE utf8_unicode_ci,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `created_by` VARCHAR(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

-- LOCK TABLES `master_admins` WRITE;
/*!40000 ALTER TABLE `master_admins` DISABLE KEYS */;
INSERT INTO `master_admins`
VALUES
  (1, null, null, null, null, null, null, NOW(), NOW(),'system');