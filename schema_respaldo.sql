-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: plataforma_prestamos
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `avisos`
--

DROP TABLE IF EXISTS `avisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avisos` (
  `id_aviso` int NOT NULL AUTO_INCREMENT,
  `estado_envio` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `tipo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_reserva` int DEFAULT NULL,
  PRIMARY KEY (`id_aviso`),
  KEY `FKh8gaj6ygfj601ga3gyp21bx0a` (`id_reserva`),
  CONSTRAINT `FKh8gaj6ygfj601ga3gyp21bx0a` FOREIGN KEY (`id_reserva`) REFERENCES `reservas` (`id_reserva`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avisos`
--

LOCK TABLES `avisos` WRITE;
/*!40000 ALTER TABLE `avisos` DISABLE KEYS */;
INSERT INTO `avisos` VALUES (1,'ENVIADO','2026-11-24 23:48:13.146814','CONFIRMACION',1);
/*!40000 ALTER TABLE `avisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `incidentes`
--

DROP TABLE IF EXISTS `incidentes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `incidentes` (
  `id_incidente` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `id_auxiliar` int DEFAULT NULL,
  `id_mantenimiento` int DEFAULT NULL,
  `id_prestamo` int DEFAULT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `resuelto` bit(1) NOT NULL,
  PRIMARY KEY (`id_incidente`),
  KEY `FKd9i1cpnfvx8xhepfuwqus1dft` (`id_auxiliar`),
  KEY `FK4hw060o7liemerfj99aik8lud` (`id_mantenimiento`),
  KEY `FKj43div1rtaiv2657b3m3ycmyr` (`id_prestamo`),
  CONSTRAINT `FK4hw060o7liemerfj99aik8lud` FOREIGN KEY (`id_mantenimiento`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKd9i1cpnfvx8xhepfuwqus1dft` FOREIGN KEY (`id_auxiliar`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKj43div1rtaiv2657b3m3ycmyr` FOREIGN KEY (`id_prestamo`) REFERENCES `prestamos` (`id_prestamo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incidentes`
--

LOCK TABLES `incidentes` WRITE;
/*!40000 ALTER TABLE `incidentes` DISABLE KEYS */;
INSERT INTO `incidentes` VALUES (1,'Daño reportado en devolución por auxiliar.',NULL,NULL,NULL,1,'2026-11-25 13:00:00.000000',_binary '\0');
/*!40000 ALTER TABLE `incidentes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prestamos`
--

DROP TABLE IF EXISTS `prestamos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prestamos` (
  `id_prestamo` int NOT NULL AUTO_INCREMENT,
  `condicion_devolucion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `condicion_entrega` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_hora_devolucion` datetime(6) DEFAULT NULL,
  `fecha_hora_retiro` datetime(6) DEFAULT NULL,
  `tardio` bit(1) NOT NULL,
  `id_reserva` int DEFAULT NULL,
  `estado_prestamo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `observaciones_devolucion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_auxiliar_entrega` int NOT NULL,
  `id_auxiliar_recepcion` int DEFAULT NULL,
  PRIMARY KEY (`id_prestamo`),
  UNIQUE KEY `UKqfi4dtwjv0xu7ot1kxxw6pv0c` (`id_reserva`),
  KEY `FKfba5jupnk4ixgytu0v5kv0qe2` (`id_auxiliar_entrega`),
  KEY `FKfjv94x7fsg2lkvmf244vuvgf7` (`id_auxiliar_recepcion`),
  CONSTRAINT `FKdc31rspf0xofnmyltyw27rgvd` FOREIGN KEY (`id_reserva`) REFERENCES `reservas` (`id_reserva`),
  CONSTRAINT `FKfba5jupnk4ixgytu0v5kv0qe2` FOREIGN KEY (`id_auxiliar_entrega`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKfjv94x7fsg2lkvmf244vuvgf7` FOREIGN KEY (`id_auxiliar_recepcion`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prestamos`
--

LOCK TABLES `prestamos` WRITE;
/*!40000 ALTER TABLE `prestamos` DISABLE KEYS */;
INSERT INTO `prestamos` VALUES (1,NULL,NULL,'2026-11-25 13:00:00.000000','2026-11-24 23:53:52.793387',_binary '\0',1,'DEVUELTO_A_TIEMPO','',3,3);
/*!40000 ALTER TABLE `prestamos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registros_auditoria`
--

DROP TABLE IF EXISTS `registros_auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registros_auditoria` (
  `id_registro` int NOT NULL AUTO_INCREMENT,
  `accion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detalle` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `id_usuario` int DEFAULT NULL,
  PRIMARY KEY (`id_registro`),
  KEY `FKr9009uou2e02cmg3xn4huy66m` (`id_usuario`),
  CONSTRAINT `FKr9009uou2e02cmg3xn4huy66m` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registros_auditoria`
--

LOCK TABLES `registros_auditoria` WRITE;
/*!40000 ALTER TABLE `registros_auditoria` DISABLE KEYS */;
INSERT INTO `registros_auditoria` VALUES (1,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 22:38:47.069741',1),(2,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 22:48:34.537917',1),(3,'CREAR_RESERVA','Reserva #1 (ESTANDAR) para unidad POR-N01 en estado CONFIRMADA','2026-11-24 22:51:29.663620',1),(4,'CANCELAR_RESERVA','Reserva #1 cancelada por el titular.','2026-11-24 22:51:40.413914',1),(5,'INICIO_SESION','Acceso exitoso al sistema con rol COORDINADOR','2026-11-24 22:52:56.310185',4),(6,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 22:57:10.676461',1),(7,'CREAR_RESERVA','Reserva #2 (ESPECIALIZADA) para unidad ROB-S01 en estado PENDIENTE','2026-11-24 22:58:43.979498',1),(8,'INICIO_SESION','Acceso exitoso al sistema con rol COORDINADOR','2026-11-24 22:59:06.133116',4),(9,'APROBAR_RESERVA','Reserva #2 aprobada por Coordinador.','2026-11-24 22:59:30.835417',4),(10,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 23:00:09.643496',1),(11,'INICIO_SESION','Acceso exitoso al sistema con rol COORDINADOR','2026-11-24 23:06:14.195682',4),(12,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 23:22:27.092379',1),(13,'CREAR_RESERVA','Reserva #3 (ESTANDAR) para unidad POR-N01 en estado CONFIRMADA','2026-11-24 23:24:13.365301',1),(14,'CANCELAR_RESERVA','Reserva #2 cancelada por el titular.','2026-11-24 23:24:17.552610',1),(15,'INICIO_SESION','Acceso exitoso al sistema con rol COORDINADOR','2026-11-24 23:25:01.310835',4),(16,'INICIO_SESION','Acceso exitoso al sistema con rol AUXILIAR','2026-11-24 23:25:55.021693',3),(17,'INICIO_SESION','Acceso exitoso al sistema con rol MANTENIMIENTO','2026-11-24 23:27:48.789835',5),(18,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 23:46:50.584468',1),(19,'CREAR_RESERVA','Reserva RES-6956 (ESPECIALIZADA) para unidad CAM-N01 en estado PENDIENTE','2026-11-24 23:47:43.256668',1),(20,'INICIO_SESION','Acceso exitoso al sistema con rol COORDINADOR','2026-11-24 23:48:06.303827',4),(21,'APROBAR_RESERVA','Reserva RES-6956 aprobada por Coordinador.','2026-11-24 23:48:13.144583',4),(22,'INICIO_SESION','Acceso exitoso al sistema con rol AUXILIAR','2026-11-24 23:48:36.296784',3),(23,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 23:49:06.292307',1),(24,'INICIO_SESION','Acceso exitoso al sistema con rol AUXILIAR','2026-11-24 23:49:31.913780',3),(25,'INICIO_SESION','Acceso exitoso al sistema con rol SOLICITANTE','2026-11-24 23:53:29.435721',1),(26,'INICIO_SESION','Acceso exitoso al sistema con rol AUXILIAR','2026-11-24 23:53:46.135026',3),(27,'ENTREGA_EQUIPO','Auxiliar entregó unidad CAM-N01 con código RES-6956','2026-11-24 23:53:52.813835',3),(28,'DEVOLUCION_EQUIPO','Devolución de reserva RES-6956 (DEVUELTO_A_TIEMPO) + INCIDENTE REPORTADO','2026-11-24 23:54:16.960470',3),(29,'INICIO_SESION','Acceso exitoso al sistema con rol MANTENIMIENTO','2026-11-24 23:54:45.170996',5),(30,'INICIO_SESION','Acceso exitoso al sistema con rol MANTENIMIENTO','2026-11-25 00:06:04.661074',5),(31,'INICIO_SESION','Acceso exitoso al sistema con rol DIRECCION','2026-11-25 00:06:45.210363',6);
/*!40000 ALTER TABLE `registros_auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservas`
--

DROP TABLE IF EXISTS `reservas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservas` (
  `id_reserva` int NOT NULL AUTO_INCREMENT,
  `estado` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha` date DEFAULT NULL,
  `hora_fin` time(6) DEFAULT NULL,
  `hora_inicio` time(6) DEFAULT NULL,
  `justificacion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_solicitud` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_solicitante` int DEFAULT NULL,
  `id_unidad` int DEFAULT NULL,
  `codigo_retiro` varchar(12) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_reserva`),
  UNIQUE KEY `UKb3mv2tmrmnl7f16aj3tgeouv8` (`codigo_retiro`),
  KEY `FK9528i5xvrin626dn9w1tarivf` (`id_solicitante`),
  KEY `FKn4xrxobyw1v0iuxfcrpdcwoo0` (`id_unidad`),
  CONSTRAINT `FK9528i5xvrin626dn9w1tarivf` FOREIGN KEY (`id_solicitante`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKn4xrxobyw1v0iuxfcrpdcwoo0` FOREIGN KEY (`id_unidad`) REFERENCES `unidades_equipo` (`id_unidad`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservas`
--

LOCK TABLES `reservas` WRITE;
/*!40000 ALTER TABLE `reservas` DISABLE KEYS */;
INSERT INTO `reservas` VALUES (1,'FINALIZADA','2026-11-25','18:00:00.000000','14:00:00.000000','Aprobada por Coordinación.','ESPECIALIZADA',1,5,'RES-6956');
/*!40000 ALTER TABLE `reservas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_equipo`
--

DROP TABLE IF EXISTS `tipos_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_equipo` (
  `id_tipo` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `especializado` bit(1) NOT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_equipo`
--

LOCK TABLES `tipos_equipo` WRITE;
/*!40000 ALTER TABLE `tipos_equipo` DISABLE KEYS */;
INSERT INTO `tipos_equipo` VALUES (1,'Laptop para trabajo académico general',_binary '\0','Portátil Institucional'),(2,'Proyector multimedia con cable HDMI',_binary '\0','Video Beam HD'),(3,'Equipo audiovisual especializado',_binary '','Cámara Profesional 4K'),(4,'Sensores, actuadores y microcontroladores',_binary '','Kit Robótica Avanzada');
/*!40000 ALTER TABLE `tipos_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unidades_equipo`
--

DROP TABLE IF EXISTS `unidades_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidades_equipo` (
  `id_unidad` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` bit(1) NOT NULL,
  `sede` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_tipo` int DEFAULT NULL,
  PRIMARY KEY (`id_unidad`),
  UNIQUE KEY `UKqcju24bivdshynrjk197t9jqs` (`codigo`),
  KEY `FK115y26o93nknvejdqk6t9fa33` (`id_tipo`),
  CONSTRAINT `FK115y26o93nknvejdqk6t9fa33` FOREIGN KEY (`id_tipo`) REFERENCES `tipos_equipo` (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidades_equipo`
--

LOCK TABLES `unidades_equipo` WRITE;
/*!40000 ALTER TABLE `unidades_equipo` DISABLE KEYS */;
INSERT INTO `unidades_equipo` VALUES (1,'POR-N01',_binary '','Sede Norte',1),(2,'POR-N02',_binary '','Sede Norte',1),(3,'POR-S01',_binary '','Sede Sur',1),(4,'PRY-N01',_binary '','Sede Norte',2),(5,'CAM-N01',_binary '\0','Sede Norte',3),(6,'ROB-S01',_binary '','Sede Sur',4);
/*!40000 ALTER TABLE `unidades_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `rol_tipo` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sede_asignada` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bloqueado` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UKcdmw5hxlfj78uf4997i3qyyw5` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES ('SOLICITANTE',1,'solicitante@uni.edu','Juan Estudiante','1234',NULL,_binary '\0'),('SOLICITANTE',2,'bloqueado@uni.edu','Pedro Bloqueado','1234',NULL,_binary ''),('AUXILIAR',3,'auxiliar@uni.edu','Ana Auxiliar Norte','1234','Sede Norte',NULL),('COORDINADOR',4,'coordinador@uni.edu','Carlos Coordinador','1234',NULL,NULL),('MANTENIMIENTO',5,'mantenimiento@uni.edu','Luis Mantenimiento','1234',NULL,NULL),('DIRECCION',6,'direccion@uni.edu','Laura Direccion','1234',NULL,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-25  0:20:44
