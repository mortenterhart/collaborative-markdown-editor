package org.dhbw.mosbach.ai.cmd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Model class for the 'repos' table.
 *
 * @author 3040018
 */
@Entity
@Table(name = "repos")
public class Repo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FK_USERS")
    private User owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
        Repo other = (Repo) obj;
        if (id != other.id)
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("User: \n")
                .append("\tid: " + this.id + "\n")
                .append("\tOwner: " + this.owner.getName() + "\n")
                .toString();
    }
}
