package telran.spring.jpa.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.spring.jpa.dto.IntervalMarksDto;
import telran.spring.jpa.dto.MarkDto;
import telran.spring.jpa.dto.StudentsSubjectMarks;
import telran.spring.jpa.entities.Mark;
import telran.spring.jpa.entities.Student;
import telran.spring.jpa.entities.Subject;
import telran.spring.jpa.repo.MarkRepository;
import telran.spring.jpa.repo.StudentRepository;
import telran.spring.jpa.repo.SubjectRepository;
import telran.spring.jpa.service.interfaces.Marks;

@Service
public class MarksJpaImpl implements Marks {

    @Autowired
    MarkRepository marks;

    @Autowired
    StudentRepository students;

    @Autowired
    SubjectRepository subjects;

    @Override
    public List<Integer> getTopMarksEncountered(int nMarks, String subject) {
	return marks.findTopMarksEncountered(subject, PageRequest.of(0, nMarks));
    }

    @Override
    public List<IntervalMarksDto> getIntervalsMarks(int interval) {
	return marks.findIntervalsMarks(interval);
    }

    @Transactional
    @Override
    public void deleteByNameAndSubject(String name, String subject) {
	marks.deleteByStudentNameAndSubjectSubject(name, subject);
    }

    @Override
    public void addMark(MarkDto markDto) {
	Student student = students.findById(markDto.stid).orElse(null);
	Subject subject = subjects.findById(markDto.suid).orElse(null);
	Mark mark = new Mark(student, subject, markDto.mark);
	marks.save(mark);
    }

    @Override
    public Long getCountMarksByStudentNameAndSubjectName(String name, String subject) {

	return marks.countMarksGroupByStudentNameAndSubjectSubject(name, subject);
    }

    @Override
    public List<StudentsSubjectMarks> getAllCountsMarks() {
	return marks.countAllMarksGroupBySubject();
    }

}
