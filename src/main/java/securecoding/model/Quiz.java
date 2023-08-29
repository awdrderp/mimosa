package securecoding.model;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
// import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
public class Quiz {

	// private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
	
	@Id
	@Column(name="code")
	private String code;
	private String year;
	private boolean enabled;
	private LocalDateTime expire;
	private Long duration;

	@OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
	@JsonManagedReference
	private Set<User> users;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecturer")
	@JsonBackReference
	private User lecturer;
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "quizzes")
	@JsonBackReference
	private Set<Challenge> questions;
	@OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Student> students;

	public Quiz(String code, String year, User lecturer, Long duration, LocalDateTime expire) {
		this.code = code;
		this.year = year;
		
		this.enabled = true;
        this.duration = duration;
        this.expire = expire;
		this.lecturer = lecturer;
		
		this.users = new HashSet<>();
		this.questions = new HashSet<>();
		this.students = new HashSet<>();
	}
	
	public Quiz(String code, String year, Long duration, LocalDateTime expire) {
		this(code, year, null, duration, expire);
	}

}
