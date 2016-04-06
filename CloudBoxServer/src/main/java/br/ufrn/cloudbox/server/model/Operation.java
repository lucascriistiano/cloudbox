package br.ufrn.cloudbox.server.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.cloudbox.model.User;

@Entity
public class Operation implements Serializable {

	public static final String CREATE = "CREATE";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8891382325049954222L;

	@Id
	@Column(name = "operation_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "relative_file_path", nullable = false)
	private String relativeFilePath;
	
	@Column(name = "datetime", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public Operation() {
	}
	
	public Operation(String relativeFilePath, Date lastModified, String type, User user) {
		this.relativeFilePath = relativeFilePath;
		this.datetime = lastModified;
		this.type = type;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRelativeFilePath() {
		return relativeFilePath;
	}

	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.S");
		return "Operation " + id + " - " + simpleDateFormat.format(datetime) + ": " + type + " " + relativeFilePath;
	}

}
