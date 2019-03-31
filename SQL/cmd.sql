-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema CMD
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `CMD` ;

-- -----------------------------------------------------
-- Schema CMD
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `CMD` DEFAULT CHARACTER SET utf8 ;
USE `CMD` ;

-- -----------------------------------------------------
-- Table `CMD`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CMD`.`users` ;

CREATE TABLE IF NOT EXISTS `CMD`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(45) NOT NULL,
  `PASSWORD` VARCHAR(100) NOT NULL,
  `MAIL` VARCHAR(45) NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CMD`.`repos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CMD`.`repos` ;

CREATE TABLE IF NOT EXISTS `CMD`.`repos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FK_USERS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_REPOS_USERS1_idx` (`FK_USERS` ASC),
  CONSTRAINT `fk_REPOS_USERS1`
    FOREIGN KEY (`FK_USERS`)
    REFERENCES `CMD`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CMD`.`docs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CMD`.`docs` ;

CREATE TABLE IF NOT EXISTS `CMD`.`docs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(45) NULL,
  `CONTENT` TEXT NULL,
  `USERS` VARCHAR(100) NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CUSER` VARCHAR(45) NULL,
  `UUSER` VARCHAR(45) NULL,
  `FK_REPOS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_DOCS_REPOS1_idx` (`FK_REPOS` ASC),
  CONSTRAINT `fk_DOCS_REPOS1`
    FOREIGN KEY (`FK_REPOS`)
    REFERENCES `CMD`.`repos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CMD`.`history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CMD`.`history` ;

CREATE TABLE IF NOT EXISTS `CMD`.`history` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `CONTENT` TEXT NULL,
  `HASH` VARCHAR(100) NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `FK_DOCS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_HISTORY_DOCS_idx` (`FK_DOCS` ASC),
  CONSTRAINT `fk_HISTORY_DOCS`
    FOREIGN KEY (`FK_DOCS`)
    REFERENCES `CMD`.`docs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CMD`.`collaborators`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CMD`.`collaborators` ;

CREATE TABLE IF NOT EXISTS `CMD`.`collaborators` (
  `id` INT NOT NULL,
  `FK_USERS` INT NOT NULL,
  `FK_DOCS` INT NOT NULL,
  `HAS_ACCESS` VARCHAR(1) NOT NULL,
  `CTIME` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_collaborators_users1_idx` (`FK_USERS` ASC),
  INDEX `fk_collaborators_docs1_idx` (`FK_DOCS` ASC),
  CONSTRAINT `fk_collaborators_users1`
    FOREIGN KEY (`FK_USERS`)
    REFERENCES `CMD`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_collaborators_docs1`
    FOREIGN KEY (`FK_DOCS`)
    REFERENCES `CMD`.`docs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `CMD`;

DELIMITER $$

USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`users_BIR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`users_BIR` BEFORE INSERT ON `users` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`users_BUR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`users_BUR` BEFORE UPDATE ON `users` FOR EACH ROW
BEGIN
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`docs_BIR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`docs_BIR` BEFORE INSERT ON `docs` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`docs_BUR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`docs_BUR` BEFORE UPDATE ON `docs` FOR EACH ROW
BEGIN
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`history_BIR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`history_BIR` BEFORE INSERT ON `history` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
END$$


USE `CMD`$$
DROP TRIGGER IF EXISTS `CMD`.`collaborators_BIR` $$
USE `CMD`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`collaborators_BIR` BEFORE INSERT ON `collaborators` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
