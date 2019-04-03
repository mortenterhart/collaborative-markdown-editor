package org.dhbw.mosbach.ai.cmd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Model class for the 'doc' table.
 *
 * @author 3040018
 */
@Entity
@Table(name = "docs")
public class Doc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CTIME")
    private LocalDateTime ctime;

    @Column(name = "UTIME")
    private LocalDateTime utime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSER")
    private User cuser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UUSER")
    private User uuser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FK_REPOS")
    private Repo repo;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    
    public User getCuser() {
		return cuser;
	}

	public void setCuser(User cuser) {
		this.cuser = cuser;
	}

	public User getUuser() {
		return uuser;
	}

	public void setUuser(User uuser) {
		this.uuser = uuser;
	}

	public Repo getRepo() {
        return repo;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("User: \n")
            .append("\tid: " + this.id + "\n")
            .append("\tName: " + this.name + "\n")
            .append("\tCreated: " + this.ctime + "\n")
            .append("\tLast updated: " + this.utime + "\n")
            .append("\tCreated by: " + this.cuser.getName() + "\n")
            .append("\tUpdated by: " + this.uuser.getName() + "\n")
            .append("\tContent: " + this.content + "\n")
            .toString();
    }
}
