package securecoding.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
public class Challenge {

	private @Id String url;
	private String title;
	private String description;

	private int points;
	private String difficulty;
	private String category;

	private Date createdOn;
	private Date modifiedOn;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "assignments", joinColumns = { @JoinColumn(name = "challenge") }, inverseJoinColumns = {
			@JoinColumn(name = "batch") })
	@JsonManagedReference
	private Set<Batch> batches;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "questions", joinColumns = { @JoinColumn(name = "challenge") }, inverseJoinColumns = {
			@JoinColumn(name = "quiz") })
	@JsonManagedReference
	private Set<Quiz> quizzes;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
	@JsonManagedReference
	private Set<Attempt> attempts;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
	@JsonManagedReference
	private Set<Hint> hints;


	public Challenge(String url, String title, String description, int points, String difficulty, String category) {
		this.url = url;
		this.title = title;
		this.description = description;
		this.points = points;
		this.difficulty = difficulty;
		this.category = category;

		this.createdOn = new Date();

		this.batches = new HashSet<>();
		this.quizzes = new HashSet<>();
		this.attempts = new HashSet<>();
		this.hints = new HashSet<>();
	}

}
