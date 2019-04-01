package org.dhbw.mosbach.ai.cmd.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Model class for the 'users' table.
 *
 * @author 3040018
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @NaturalId
    @Column(name = "NAME")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "MAIL")
    private String mail;

    @Column(name = "CTIME")
    private LocalDateTime ctime;

    @Column(name = "UTIME")
    private LocalDateTime utime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public LocalDateTime getCtime() {
        return ctime;
    }

    public void setCtime(LocalDateTime ctime) {
        this.ctime = ctime;
    }

    public LocalDateTime getUtime() {
        return utime;
    }

    public void setUtime(LocalDateTime utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("User: \n")
            .append("\tid: " + this.id + "\n")
            .append("\tName: " + this.name + "\n")
            .append("\tHash: " + this.password + "\n")
            .append("\tMAIL: " + this.mail + "\n")
            .append("\tCreated: " + this.ctime + "\n")
            .append("\tLast updated: " + this.utime + "\n")
            .toString();
    }
}
