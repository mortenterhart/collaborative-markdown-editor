package org.dhbw.mosbach.ai.cmd.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeDeserializer;
import org.dhbw.mosbach.ai.cmd.services.serialize.LocalDateTimeSerializer;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
    @JsonIgnore
    private String password;

    @Column(name = "MAIL")
    private String mail;

    @Column(name = "CTIME")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime ctime;

    @Column(name = "UTIME")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime utime;

    @PrePersist
    private void onInsert() {
        this.ctime = LocalDateTime.now();
        this.utime = this.ctime;
    }

    @PreUpdate
    private void onUpdate() {
        this.utime = LocalDateTime.now();
    }

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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ctime == null) ? 0 : ctime.hashCode());
        result = prime * result + id;
        result = prime * result + ((mail == null) ? 0 : mail.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((utime == null) ? 0 : utime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (ctime == null) {
            if (other.ctime != null)
                return false;
        } else if (!ctime.equals(other.ctime))
            return false;
        if (id != other.id)
            return false;
        if (mail == null) {
            if (other.mail != null)
                return false;
        } else if (!mail.equals(other.mail))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (utime == null) {
            if (other.utime != null)
                return false;
        } else if (!utime.equals(other.utime))
            return false;
        return true;
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
