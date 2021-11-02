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

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
ALTER TABLE `master_admins`
  ADD COLUMN `dr_template_id` int AFTER `third_attachment`,
  ADD COLUMN `fa_template_id` int AFTER `dr_template_id`;
/*!40101 SET character_set_client = @saved_cs_client */;

-- LOCK TABLES `master_admins` WRITE;
/*!40000 ALTER TABLE `master_admins` DISABLE KEYS */;
UPDATE `master_admins` 
SET 
`dr_template_id` = (select `id` from `master_templates` limit 1),
`fa_template_id` = (select `id` from `master_templates` limit 1);