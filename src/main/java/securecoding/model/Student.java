package securecoding.model;

import java.time.LocalDateTime;
// import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
    private LocalDateTime datetime_joined;

	@ManyToOne
	@JoinColumn(name = "quiz")
	@JsonBackReference
	private Quiz quiz;
	@ManyToOne
	@JoinColumn(name = "user")
	@JsonBackReference
	private User user;

	public Student(Quiz quiz, User user){
		this.quiz = quiz;
		this.user = user;

		this.datetime_joined = LocalDateTime.now();
	}
    
}
