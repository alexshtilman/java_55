package telran.spring.jpa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.spring.jpa.entities.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    @Query(value = "SELECT subject FROM marks_subjects GROUP BY subject HAVING sum(mark) >" + "(SELECT AVG(total) FROM "
	    + "(SELECT SUM(mark) as total from marks_subjects group by subject) as totals) "
	    + "order by sum(mark) desc", nativeQuery = true)
    List<String> findSubjectsHighestMarks();

    @Query(value = "SELECT m.subject.subject FROM Mark m  "
	    + "group by m.subject.subject order by SUM(m.mark) desc, m.subject.subject desc")
    List<String> findTopSubjectsHighestMarks(Pageable pageable);
}
