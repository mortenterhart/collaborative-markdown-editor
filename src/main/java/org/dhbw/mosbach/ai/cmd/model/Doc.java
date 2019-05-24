package org.dhbw.mosbach.ai.cmd.model;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
    @JsonbTransient
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((ctime == null) ? 0 : ctime.hashCode());
		result = prime * result + ((cuser == null) ? 0 : cuser.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((repo == null) ? 0 : repo.hashCode());
		result = prime * result + ((utime == null) ? 0 : utime.hashCode());
		result = prime * result + ((uuser == null) ? 0 : uuser.hashCode());
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
		Doc other = (Doc) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (ctime == null) {
			if (other.ctime != null)
				return false;
		} else if (!ctime.equals(other.ctime))
			return false;
		if (cuser == null) {
			if (other.cuser != null)
				return false;
		} else if (!cuser.equals(other.cuser))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (repo == null) {
			if (other.repo != null)
				return false;
		} else if (!repo.equals(other.repo))
			return false;
		if (utime == null) {
			if (other.utime != null)
				return false;
		} else if (!utime.equals(other.utime))
			return false;
		if (uuser == null) {
			if (other.uuser != null)
				return false;
		} else if (!uuser.equals(other.uuser))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return new StringBuilder()
            .append("Doc: \n")
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