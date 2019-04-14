-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cmd
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `cmd` ;

-- -----------------------------------------------------
-- Schema cmd
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cmd` DEFAULT CHARACTER SET utf8 ;
USE `cmd` ;

-- -----------------------------------------------------
-- Table `cmd`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmd`.`users` ;

CREATE TABLE IF NOT EXISTS `cmd`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(45) NOT NULL,
  `PASSWORD` VARCHAR(100) NOT NULL,
  `MAIL` VARCHAR(45) NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmd`.`repos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmd`.`repos` ;

CREATE TABLE IF NOT EXISTS `cmd`.`repos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FK_USERS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_REPOS_USERS1_idx` (`FK_USERS` ASC),
  CONSTRAINT `fk_REPOS_USERS1`
    FOREIGN KEY (`FK_USERS`)
    REFERENCES `cmd`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmd`.`docs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmd`.`docs` ;

CREATE TABLE IF NOT EXISTS `cmd`.`docs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(45) NULL,
  `CONTENT` TEXT NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CUSER` INT NOT NULL,
  `UUSER` INT NOT NULL,
  `FK_REPOS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_DOCS_REPOS1_idx` (`FK_REPOS` ASC),
  INDEX `fk_docs_users1_idx` (`CUSER` ASC),
  INDEX `fk_docs_users2_idx` (`UUSER` ASC),
  CONSTRAINT `fk_DOCS_REPOS1`
    FOREIGN KEY (`FK_REPOS`)
    REFERENCES `cmd`.`repos` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_docs_users1`
    FOREIGN KEY (`CUSER`)
    REFERENCES `cmd`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_docs_users2`
    FOREIGN KEY (`UUSER`)
    REFERENCES `cmd`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmd`.`history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmd`.`history` ;

CREATE TABLE IF NOT EXISTS `cmd`.`history` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `CONTENT` TEXT NULL,
  `HASH` VARCHAR(100) NULL,
  `CTIME` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `FK_DOCS` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_HISTORY_DOCS_idx` (`FK_DOCS` ASC),
  CONSTRAINT `fk_HISTORY_DOCS`
    FOREIGN KEY (`FK_DOCS`)
    REFERENCES `cmd`.`docs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmd`.`collaborators`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmd`.`collaborators` ;

CREATE TABLE IF NOT EXISTS `cmd`.`collaborators` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FK_USERS` INT NOT NULL,
  `FK_DOCS` INT NOT NULL,
  `HAS_ACCESS` VARCHAR(1) NOT NULL,
  `CTIME` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_collaborators_users1_idx` (`FK_USERS` ASC),
  INDEX `fk_collaborators_docs1_idx` (`FK_DOCS` ASC),
  CONSTRAINT `fk_collaborators_users1`
    FOREIGN KEY (`FK_USERS`)
    REFERENCES `cmd`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_collaborators_docs1`
    FOREIGN KEY (`FK_DOCS`)
    REFERENCES `cmd`.`docs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `cmd`;

DELIMITER $$

USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`users_BIR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`users_BIR` BEFORE INSERT ON `users` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`users_BUR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`users_BUR` BEFORE UPDATE ON `users` FOR EACH ROW
BEGIN
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`docs_BIR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`docs_BIR` BEFORE INSERT ON `docs` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`docs_BUR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`docs_BUR` BEFORE UPDATE ON `docs` FOR EACH ROW
BEGIN
 SET NEW.UTIME = CURRENT_TIMESTAMP;
END$$


USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`history_BIR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`history_BIR` BEFORE INSERT ON `history` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
END$$


USE `cmd`$$
DROP TRIGGER IF EXISTS `cmd`.`collaborators_BIR` $$
USE `cmd`$$
CREATE DEFINER = CURRENT_USER TRIGGER `cmd`.`collaborators_BIR` BEFORE INSERT ON `collaborators` FOR EACH ROW
BEGIN
 SET NEW.CTIME = CURRENT_TIMESTAMP;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
