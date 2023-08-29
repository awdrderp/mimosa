package securecoding.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import securecoding.model.Batch;
import securecoding.model.User;
import securecoding.model.property.Roles;

@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, String> {

	public User findByUsername(String username);
	public User findByAdminNo(String adminNo);
	public User findByEmail(String email);
	public User findByUuid(String uuid);
	public List<User> findByBatch(Batch batch);
	public List<User> findByRole(String role);
	public List<User> findByEnabledTrue();
	
	public default List<User> findStudentsByOrderByPointsDesc() {
		List<User> users = findByRole(Roles.STUDENT);
		return users.stream().sorted((a, b) -> b.getPoints() - a.getPoints()).collect(Collectors.toList());
	}
	
	public default List<User> findStudentsByBatchOrderByPointsDesc(Batch batch) {
		List<User> users = findByBatch(batch);
		return users.stream().sorted((a, b) -> b.getPoints() - a.getPoints()).collect(Collectors.toList());
	}
	
	public default List<User> findByOrderByPointsDesc() {
		return findAll().stream().sorted((a, b) -> b.getPoints() - a.getPoints()).collect(Collectors.toList());
	}

	public default List<User> findByBatchAndPointsLessThan(Batch batch, int points) {
		List<User> users = findByBatch(batch);
		return users.stream().filter(u -> u.getPoints() < points).collect(Collectors.toList());
	}

}
