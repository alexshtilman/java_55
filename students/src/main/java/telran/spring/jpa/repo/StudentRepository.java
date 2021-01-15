package telran.spring.jpa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.spring.jpa.dto.StudentRaw;
import telran.spring.jpa.dto.StudentsMarksCount;
import telran.spring.jpa.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query(value = "SELECT s.name FROM students s join marks m on s.stid=m.student_stid"
	    + " GROUP by s.name having avg(mark)> (SELECT avg(mark)"
	    + " from marks) order by round(avg(mark)) desc", nativeQuery = true)
    List<String> findBestStudents();

    @Query(value = "SELECT name FROM students_marks_subjects where subject=:subject "
	    + "group by name having avg(mark) >= (SELECT avg(mark) FROM students_marks_subjects "
	    + "where subject=:subject) order by avg(mark)", nativeQuery = true)
    List<String> findBestStudentsSubject(@Param("subject") String subject);

    @Query(value = "SELECT s.name as name FROM Student s join Mark m on s.stid=m.student.stid "
	    + "group by s.name order by round(avg(m.mark)) desc")
    List<String> findTopBestStudents(Pageable pageable);

    @Query(value = "SELECT s.name as name, count(m.mark) as marksCount FROM Student s left join Mark m on "
	    + "s.stid=m.student.stid GROUP by s.name order by marksCount desc")
    List<StudentsMarksCount> findStudentsMarksCount();

    @Query(value = "SELECT m.student.name FROM Mark m  where m.subject.subject = :subject "
	    + "GROUP by m.student.name order by round(avg(m.mark)) desc")
    List<String> findTopBestStudentsSubect(String subject, Pageable pageable);

    StudentRaw findByName(String name);

    StudentRaw findByStid(int stid);

    @Modifying
    void deleteByName(String name);

    @Modifying
    void deleteByStid(int stid);
}
