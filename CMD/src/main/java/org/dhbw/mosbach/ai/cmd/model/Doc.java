package org.dhbw.mosbach.ai.cmd.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Model class for the 'doc' table.
 * @author 3040018
 */
@Entity
@Table(name="docs")
public class Doc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="CONTENT")
	private String content;
	
	@Column(name="USERS")
	private String users;
	
	@Column(name="CTIME")
	private LocalDateTime ctime;
	
	@Column(name="UTIME")
	private LocalDateTime utime;
	
	@Column(name="CUSER")
	private String cuser;
	
	@Column(name="UUSER")
	private String uuser;
	
	@ManyToOne(fetch = FetchType.LAZY)
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

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
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

	public String getCuser() {
		return cuser;
	}

	public void setCuser(String cuser) {
		this.cuser = cuser;
	}

	public String getUuser() {
		return uuser;
	}

	public void setUuser(String uuser) {
		this.uuser = uuser;
	}

	public Repo getRepo() {
		return repo;
	}

	public void setRepo(Repo repo) {
		this.repo = repo;
	}
}