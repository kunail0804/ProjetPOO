-- MySQL dump 10.13  Distrib 5.7.24, for osx11.1 (x86_64)
--
-- Host: mysql-groupe10.alwaysdata.net    Database: groupe10_projetpoo
-- ------------------------------------------------------
-- Server version	5.5.5-10.11.15-MariaDB

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
-- Table structure for table `AGENT`
--

DROP TABLE IF EXISTS `AGENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AGENT` (
  `id_utilisateur` int(11) NOT NULL,
  `type_agent` enum('PARTICULIER','PROFESSIONNEL') NOT NULL,
  `rib` varchar(34) DEFAULT NULL,
  `nom_societe` varchar(255) DEFAULT NULL,
  `note_moyenne` float DEFAULT 0,
  PRIMARY KEY (`id_utilisateur`),
  CONSTRAINT `fk_agent_user` FOREIGN KEY (`id_utilisateur`) REFERENCES `UTILISATEUR` (`id_utilisateur`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ASSURANCE`
--

DROP TABLE IF EXISTS `ASSURANCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ASSURANCE` (
  `id_assurance` int(11) NOT NULL AUTO_INCREMENT,
  `nom_assurance` varchar(100) NOT NULL,
  `description_couverture` text DEFAULT NULL,
  `prix_journalier` float NOT NULL,
  PRIMARY KEY (`id_assurance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CAMION`
--

DROP TABLE IF EXISTS `CAMION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CAMION` (
  `id_louable` int(11) NOT NULL,
  `volume_m3` int(11) NOT NULL,
  PRIMARY KEY (`id_louable`),
  CONSTRAINT `fk_camion_veh` FOREIGN KEY (`id_louable`) REFERENCES `VEHICULE` (`id_louable`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONTRAT`
--

DROP TABLE IF EXISTS `CONTRAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONTRAT` (
  `id_contrat` int(11) NOT NULL AUTO_INCREMENT,
  `id_loueur` int(11) NOT NULL,
  `id_agent` int(11) NOT NULL,
  `id_louable` int(11) NOT NULL,
  `id_assurance` int(11) NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime NOT NULL,
  `date_signature` datetime DEFAULT current_timestamp(),
  `etat` enum('EN_ATTENTE','VALIDE','REFUSE','TERMINE','ANNULE') DEFAULT 'EN_ATTENTE',
  `chemin_pdf` varchar(500) DEFAULT NULL,
  `prix_final` float DEFAULT NULL,
  PRIMARY KEY (`id_contrat`),
  KEY `fk_contrat_loueur` (`id_loueur`),
  KEY `fk_contrat_agent` (`id_agent`),
  KEY `fk_contrat_louable` (`id_louable`),
  KEY `fk_contrat_assu` (`id_assurance`),
  CONSTRAINT `fk_contrat_agent` FOREIGN KEY (`id_agent`) REFERENCES `AGENT` (`id_utilisateur`),
  CONSTRAINT `fk_contrat_assu` FOREIGN KEY (`id_assurance`) REFERENCES `ASSURANCE` (`id_assurance`),
  CONSTRAINT `fk_contrat_louable` FOREIGN KEY (`id_louable`) REFERENCES `LOUABLE` (`id_louable`),
  CONSTRAINT `fk_contrat_loueur` FOREIGN KEY (`id_loueur`) REFERENCES `LOUEUR` (`id_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DISPONIBILITE`
--

DROP TABLE IF EXISTS `DISPONIBILITE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DISPONIBILITE` (
  `id_disponibilite` int(11) NOT NULL AUTO_INCREMENT,
  `id_louable` int(11) NOT NULL,
  `id_agent` int(11) NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime NOT NULL,
  `prix_journalier` float NOT NULL,
  `est_reservee` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id_disponibilite`),
  KEY `fk_dispo_louable` (`id_louable`),
  KEY `fk_dispo_agent` (`id_agent`),
  CONSTRAINT `fk_dispo_agent` FOREIGN KEY (`id_agent`) REFERENCES `AGENT` (`id_utilisateur`),
  CONSTRAINT `fk_dispo_louable` FOREIGN KEY (`id_louable`) REFERENCES `LOUABLE` (`id_louable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ENTREPRISE_ENTRETIEN`
--

DROP TABLE IF EXISTS `ENTREPRISE_ENTRETIEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ENTREPRISE_ENTRETIEN` (
  `id_utilisateur` int(11) NOT NULL,
  `siret` varchar(14) NOT NULL,
  `description_service` text DEFAULT NULL,
  PRIMARY KEY (`id_utilisateur`),
  UNIQUE KEY `siret` (`siret`),
  CONSTRAINT `fk_entreprise_user` FOREIGN KEY (`id_utilisateur`) REFERENCES `UTILISATEUR` (`id_utilisateur`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EVALUATION`
--

DROP TABLE IF EXISTS `EVALUATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EVALUATION` (
  `id_evaluation` int(11) NOT NULL AUTO_INCREMENT,
  `id_louable` int(11) NOT NULL,
  `id_loueur` int(11) NOT NULL,
  `note` int(11) NOT NULL CHECK (`note` between 1 and 5),
  `commentaire` text DEFAULT NULL,
  `date_avis` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_evaluation`),
  KEY `fk_eval_louable` (`id_louable`),
  KEY `fk_eval_loueur` (`id_loueur`),
  CONSTRAINT `fk_eval_louable` FOREIGN KEY (`id_louable`) REFERENCES `LOUABLE` (`id_louable`),
  CONSTRAINT `fk_eval_loueur` FOREIGN KEY (`id_loueur`) REFERENCES `LOUEUR` (`id_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `INTERVENTION_ENTRETIEN`
--

DROP TABLE IF EXISTS `INTERVENTION_ENTRETIEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `INTERVENTION_ENTRETIEN` (
  `id_intervention` int(11) NOT NULL AUTO_INCREMENT,
  `id_entreprise` int(11) NOT NULL,
  `id_agent` int(11) NOT NULL,
  `id_louable` int(11) NOT NULL,
  `date_intervention` datetime NOT NULL,
  `type_intervention` varchar(255) DEFAULT NULL,
  `prix_intervention` float DEFAULT NULL,
  PRIMARY KEY (`id_intervention`),
  KEY `fk_inter_entreprise` (`id_entreprise`),
  KEY `fk_inter_agent` (`id_agent`),
  KEY `fk_inter_louable` (`id_louable`),
  CONSTRAINT `fk_inter_agent` FOREIGN KEY (`id_agent`) REFERENCES `AGENT` (`id_utilisateur`),
  CONSTRAINT `fk_inter_entreprise` FOREIGN KEY (`id_entreprise`) REFERENCES `ENTREPRISE_ENTRETIEN` (`id_utilisateur`),
  CONSTRAINT `fk_inter_louable` FOREIGN KEY (`id_louable`) REFERENCES `LOUABLE` (`id_louable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LOUABLE`
--

DROP TABLE IF EXISTS `LOUABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LOUABLE` (
  `id_louable` int(11) NOT NULL AUTO_INCREMENT,
  `id_proprietaire` int(11) NOT NULL,
  `ville_disponibilite` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `etat_general` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_louable`),
  KEY `fk_louable_agent` (`id_proprietaire`),
  CONSTRAINT `fk_louable_agent` FOREIGN KEY (`id_proprietaire`) REFERENCES `AGENT` (`id_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LOUEUR`
--

DROP TABLE IF EXISTS `LOUEUR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LOUEUR` (
  `id_utilisateur` int(11) NOT NULL,
  `numero_permis` varchar(50) NOT NULL,
  `date_obtention_permis` date NOT NULL,
  `note_moyenne` float DEFAULT 0,
  PRIMARY KEY (`id_utilisateur`),
  UNIQUE KEY `numero_permis` (`numero_permis`),
  CONSTRAINT `fk_loueur_user` FOREIGN KEY (`id_utilisateur`) REFERENCES `UTILISATEUR` (`id_utilisateur`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MESSAGE`
--

DROP TABLE IF EXISTS `MESSAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MESSAGE` (
  `id_message` int(11) NOT NULL AUTO_INCREMENT,
  `id_expediteur` int(11) NOT NULL,
  `id_destinataire` int(11) NOT NULL,
  `contenu` text NOT NULL,
  `date_envoi` datetime DEFAULT current_timestamp(),
  `est_lu` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id_message`),
  KEY `fk_msg_exp` (`id_expediteur`),
  KEY `fk_msg_dest` (`id_destinataire`),
  CONSTRAINT `fk_msg_dest` FOREIGN KEY (`id_destinataire`) REFERENCES `UTILISATEUR` (`id_utilisateur`),
  CONSTRAINT `fk_msg_exp` FOREIGN KEY (`id_expediteur`) REFERENCES `UTILISATEUR` (`id_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MOTO`
--

DROP TABLE IF EXISTS `MOTO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MOTO` (
  `id_louable` int(11) NOT NULL,
  `cylindree` int(11) DEFAULT NULL,
  `a_top_case` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id_louable`),
  CONSTRAINT `fk_moto_veh` FOREIGN KEY (`id_louable`) REFERENCES `VEHICULE` (`id_louable`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OPTION_PAYANTE`
--

DROP TABLE IF EXISTS `OPTION_PAYANTE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OPTION_PAYANTE` (
  `id_option` int(11) NOT NULL AUTO_INCREMENT,
  `nom_option` varchar(100) NOT NULL,
  `prix_mensuel` float NOT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`id_option`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `POSSEDE_ASSURANCE`
--

DROP TABLE IF EXISTS `POSSEDE_ASSURANCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `POSSEDE_ASSURANCE` (
  `id_agent` int(11) NOT NULL,
  `id_assurance` int(11) NOT NULL,
  PRIMARY KEY (`id_agent`,`id_assurance`),
  KEY `fk_possede_assu` (`id_assurance`),
  CONSTRAINT `fk_possede_agent` FOREIGN KEY (`id_agent`) REFERENCES `AGENT` (`id_utilisateur`),
  CONSTRAINT `fk_possede_assu` FOREIGN KEY (`id_assurance`) REFERENCES `ASSURANCE` (`id_assurance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SOUSCRIT`
--

DROP TABLE IF EXISTS `SOUSCRIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SOUSCRIT` (
  `id_agent` int(11) NOT NULL,
  `id_option` int(11) NOT NULL,
  `date_souscription` datetime NOT NULL DEFAULT current_timestamp(),
  `est_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id_agent`,`id_option`,`date_souscription`),
  KEY `fk_souscrit_opt` (`id_option`),
  CONSTRAINT `fk_souscrit_agent` FOREIGN KEY (`id_agent`) REFERENCES `AGENT` (`id_utilisateur`),
  CONSTRAINT `fk_souscrit_opt` FOREIGN KEY (`id_option`) REFERENCES `OPTION_PAYANTE` (`id_option`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UTILISATEUR`
--

DROP TABLE IF EXISTS `UTILISATEUR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UTILISATEUR` (
  `id_utilisateur` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  `adresse` text DEFAULT NULL,
  `date_inscription` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_utilisateur`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VEHICULE`
--

DROP TABLE IF EXISTS `VEHICULE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VEHICULE` (
  `id_louable` int(11) NOT NULL,
  `marque` varchar(100) NOT NULL,
  `modele` varchar(100) NOT NULL,
  `immatriculation` varchar(20) NOT NULL,
  PRIMARY KEY (`id_louable`),
  UNIQUE KEY `immatriculation` (`immatriculation`),
  CONSTRAINT `fk_vehicule_parent` FOREIGN KEY (`id_louable`) REFERENCES `LOUABLE` (`id_louable`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VOITURE`
--

DROP TABLE IF EXISTS `VOITURE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VOITURE` (
  `id_louable` int(11) NOT NULL,
  `nb_places` int(11) NOT NULL,
  `type_carburant` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_louable`),
  CONSTRAINT `fk_voiture_veh` FOREIGN KEY (`id_louable`) REFERENCES `VEHICULE` (`id_louable`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'groupe10_projetpoo'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-08 14:25:04
