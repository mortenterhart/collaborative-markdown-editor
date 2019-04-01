package org.dhbw.mosbach.ai.cmd.model;

import org.dhbw.mosbach.ai.cmd.util.HasAccess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "collaborators")
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USERS")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_DOCS")
    private Doc doc;

    @Enumerated(EnumType.STRING)
    @Column(name = "HAS_ACCESS")
    private HasAccess hasAccess;

    @Column(name = "CTIME")
    private LocalDateTime ctime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public HasAccess getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(HasAccess hasAccess) {
        this.hasAccess = hasAccess;
    }

    public LocalDateTime getCtime() {
        return ctime;
    }

    public void setCtime(LocalDateTime ctime) {
        this.ctime = ctime;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("User: \n")
            .append("\tid: " + this.id + "\n")
            .append("\tUser: " + this.user.getName() + "\n")
            .append("\tDocument: " + this.doc.getId() + "\n")
            .append("\tHas access: " + this.hasAccess.getHasAccess() + "\n")
            .append("\tCreated: " + this.ctime + "\n")
            .toString();
    }
}
