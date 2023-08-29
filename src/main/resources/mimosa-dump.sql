CREATE DATABASE  IF NOT EXISTS `eclipse` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `eclipse`;
-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: localhost    Database: eclipse
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `achievements`
--

DROP TABLE IF EXISTS `achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `achievements` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL,
  `title` varchar(45) NOT NULL,
  `description` varchar(450) NOT NULL,
  `points` int(11) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch` int(11) NOT NULL,
  `challenge` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `assignment.batch.fk_idx` (`batch`),
  KEY `assignment.challenge.fk_idx` (`challenge`),
  CONSTRAINT `assignment.batch.fk` FOREIGN KEY (`batch`) REFERENCES `batches` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assignment.challenge.fk` FOREIGN KEY (`challenge`) REFERENCES `challenges` (`url`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attempts`
--

DROP TABLE IF EXISTS `attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attempts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `points` int(11) NOT NULL DEFAULT '0',
  `status` varchar(45) NOT NULL DEFAULT 'In Progress',
  `submission` varchar(4500) DEFAULT NULL,
  `date_submitted` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user` varchar(45) NOT NULL,
  `challenge` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attempt.user.fk_idx` (`user`),
  KEY `attempt.challenge.fk_idx` (`challenge`),
  CONSTRAINT `attempt.challenge.fk` FOREIGN KEY (`challenge`) REFERENCES `challenges` (`url`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `attempt.user.fk` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5714 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batches`
--

DROP TABLE IF EXISTS `batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `year` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `open` tinyint(1) NOT NULL DEFAULT '1',
  `lecturer` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batch.lecturer.fk_idx` (`lecturer`),
  CONSTRAINT `batch.lecturer.fk` FOREIGN KEY (`lecturer`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenges`
--

DROP TABLE IF EXISTS `challenges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `challenges` (
  `url` varchar(45) NOT NULL,
  `title` varchar(45) NOT NULL,
  `description` varchar(4500) NOT NULL,
  `points` int(11) NOT NULL,
  `difficulty` varchar(45) NOT NULL DEFAULT 'unknown',
  `category` varchar(45) NOT NULL DEFAULT 'Uknown',
  `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_on` datetime DEFAULT NULL,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contracts`
--

DROP TABLE IF EXISTS `contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contracts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `hint` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `contract.user.fk_idx` (`user`),
  KEY `contract.hint.fk_idx` (`hint`),
  CONSTRAINT `contract.hint.fk` FOREIGN KEY (`hint`) REFERENCES `hints` (`url`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `contract.user.fk` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `games`
--

DROP TABLE IF EXISTS `games`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `games` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lobby` int(6) NOT NULL,
  `challenge` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `games.lobbies.fk_idx` (`lobby`),
  KEY `games.challenge.fk_idx` (`challenge`),
  CONSTRAINT `games.lobbies.fk` FOREIGN KEY (`lobby`) REFERENCES `lobbies` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `games.challenge.fk` FOREIGN KEY (`challenge`) REFERENCES `challenges` (`url`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hints`
--

DROP TABLE IF EXISTS `hints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hints` (
  `url` varchar(50) NOT NULL,
  `hint` varchar(1000) NOT NULL,
  `cost` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  `challenge` varchar(45) NOT NULL,
  PRIMARY KEY (`url`),
  KEY `hint.challenge.fk_idx` (`challenge`),
  CONSTRAINT `hint.challenge.fk` FOREIGN KEY (`challenge`) REFERENCES `challenges` (`url`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lobbies`
--

DROP TABLE IF EXISTS `lobbies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lobbies` (
  `code` int(6) NOT NULL,
  `year` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `lecturer` varchar(45) DEFAULT NULL,
  `duration` int(10) NOT NULL,
  `expire` datetime NOT NULL,
  PRIMARY KEY (`code`),
  KEY `lobbies.lecturer.fk_idx` (`lecturer`),
  CONSTRAINT `lobbies.lecturer.fk` FOREIGN KEY (`lecturer`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notices`
--

DROP TABLE IF EXISTS `notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `notice.user.fk_idx` (`user`),
  CONSTRAINT `notice.user.fk` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quiz` varchar(45) NOT NULL,
  `user` varchar(45) NOT NULL,
  `datetime_joined` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `players.lobbies.fk_idx` (`code`),
  KEY `players.user.fk_idx` (`user`),
  CONSTRAINT `players.lobbies.fk` FOREIGN KEY (`code`) REFERENCES `lobbies` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `players.user.fk` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unlocks`
--

DROP TABLE IF EXISTS `unlocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unlocks` (
  `id` int(11) NOT NULL,
  `user` varchar(45) NOT NULL,
  `achievement` int(11) NOT NULL,
  `date_submitted` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `unlock.user.fk_idx` (`user`),
  KEY `unlock.achievement.fk_idx` (`achievement`),
  CONSTRAINT `unlock.achievement.fk` FOREIGN KEY (`achievement`) REFERENCES `achievements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `unlock.user.fk` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(45) NOT NULL,
  `password` char(60) NOT NULL,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '0',
  `role` varchar(45) NOT NULL DEFAULT 'student',
  `email` varchar(60) NOT NULL,
  `admin_no` varchar(15) DEFAULT NULL,
  `created_on` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  `batch` int(11) DEFAULT NULL,
  `lobby` int(6) DEFAULT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0',
  `uuid` char(36) DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `admin_no_UNIQUE` (`admin_no`),
  KEY `user.batch.fk_idx` (`batch`),
  KEY `user.lobby.fk_idx` (`lobby`),
  CONSTRAINT `user.batch.fk` FOREIGN KEY (`batch`) REFERENCES `batches` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user.lobby.fk` FOREIGN KEY (`lobby`) REFERENCES `lobbies` (`code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-06 22:39:40
