package securecoding.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

import securecoding.model.property.Roles;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

	private @Id String username;
	private @JsonIgnore String password;

	private String firstName;
	private String lastName;

	private boolean enabled;
	private String role;

	private @Email String email;
	private String adminNo;

	private Date createdOn;
	private Date lastLogin;

	private boolean verified;
	private String uuid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "batch")
	@JsonBackReference
	private Batch batch;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quiz")
	@JsonBackReference
	private Quiz quiz;
	@OneToMany(mappedBy = "lecturer", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Batch> teachingGroups;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@JsonManagedReference
	private Set<Notice> notices;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@JsonManagedReference
	private Set<Attempt> attempts;
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
	@JsonBackReference
	private Set<Hint> hints;
	
	public User() {
		this.createdOn = new Date();
		
		this.teachingGroups = new HashSet<>();
		this.notices = new HashSet<>();
		this.attempts = new HashSet<>();
		this.hints = new HashSet<>();
	}

	public User(String username, String password, String firstName, String lastName, boolean enabled, String role,
			String email, String adminNo, Batch batch, Quiz quiz) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.role = role;
		this.email = email;
		this.adminNo = adminNo;

		this.createdOn = new Date();
		this.batch = batch;
		this.quiz = quiz;

		this.teachingGroups = new HashSet<>();
		this.notices = new HashSet<>();
		this.attempts = new HashSet<>();
		this.hints = new HashSet<>();
	}

	public User(String username, String password, String firstName, String lastName, String email, String adminNo,
			Batch batch) {
		this(username, password, firstName, lastName, true, Roles.STUDENT, email, adminNo, batch, null);
	}

	public User(String username, String password, String firstName, String lastName, String role, String email) {
		this(username, password, firstName, lastName, true, role, email, null, null, null);
	}

	public int getPoints() {
		return attempts.stream().mapToInt(attempt -> attempt.getPoints()).sum()
				- hints.stream().mapToInt(hint -> hint.getCost()).sum();
	}

}
