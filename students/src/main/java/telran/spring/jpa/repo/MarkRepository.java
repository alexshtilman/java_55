package telran.spring.jpa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.spring.jpa.dto.IntervalMarksDto;
import telran.spring.jpa.dto.StudentsSubjectMarks;
import telran.spring.jpa.entities.Mark;

public interface MarkRepository extends JpaRepository<Mark, Integer> {

    @Query(value = "select m.mark from Mark m where m.subject.subject=:subject group by m.mark "
	    + "order by count(*) desc,m.mark desc")
    List<Integer> findTopMarksEncountered(@Param("subject") String subject, Pageable pageable);

    @Query(value = "select m.mark/:interval * :interval as minVal, :interval * (m.mark/:interval + 1) - 1 as maxVal, "
	    + "count(*) as occurrences " + "from  Mark m group by minVal, maxVal order by minVal")
    List<IntervalMarksDto> findIntervalsMarks(@Param("interval") int interval);

    @Query(value = "SELECT  m.student.stid AS stid , m.subject.suid AS suid, COUNT(*) AS count,"
	    + "ROUND(AVG(m.mark)) AS avg  FROM Mark AS m  GROUP BY m.student.stid,m.subject.suid "
	    + "HAVING COUNT(*) > 1")
    List<StudentsSubjectMarks> countAllMarksGroupBySubject();

    @Modifying
    void deleteByStudentStidAndSubjectSuid(@Param("stid") int stid, @Param("suid") int suid);

    @Modifying
    void deleteByStudentNameAndSubjectSubject(@Param("name") String name, @Param("subject") String subject);

    Long countMarksGroupByStudentNameAndSubjectSubject(@Param("student_name") String name,
	    @Param("subject_name") String subject);
}
