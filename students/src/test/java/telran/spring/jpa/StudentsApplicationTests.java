package telran.spring.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.spring.jpa.dto.IntervalMarksDto;
import telran.spring.jpa.dto.StudentRaw;
import telran.spring.jpa.dto.StudentsMarksCount;
import telran.spring.jpa.service.interfaces.Marks;
import telran.spring.jpa.service.interfaces.Students;
import telran.spring.jpa.service.interfaces.Subjects;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class StudentsApplicationTests {

    private static final String FILL_TABELS_SQL = "fillTabels.sql";
    private static final String CONTROLLERS = "controllers";
    private static final String SERVISES = "servises";
    private static final String GET = "GET ";
    private static final String POST = "POST ";
    private static final String PUT = "PUT ";
    private static final String DELETE = "DELETE ";

    private static final String MARKS = "/marks";
    private static final String COUNT = "/count";
    private static final String ALL_GT_ONE = "/all_gt_one";
    private static final String STUDENTS = "/students";
    private static final String BEST = "/best";
    private static final String DISTRIBUTION = "/distribution";
    private static final String WIDESPREAD = "/widespread";
    private static final String SUBJECTS = "/subjects";

//	name	subject	mark
//	--------------------
//	Izhak	CSS		100
//	Izhak	React	100
//	Izhak	Java	99
//	Izhak	Java	95
//	Izhak	Java	88
//	Sara	Java	85
//	Moshe	Java	80
//	Sara	CSS		80
//	Moshe	Java Technologies	80
//	Moshe	CSS		75
//	Sara	React	75
//	Moshe	Java	70
//	Moshe	Java	70
//	Sara	Java	63
//	Moshe	React	60
//	Sara	Java	48
//	Moshe	Java	45
//	Moshe	Java	45
//	Izhak	Java	39
//	Sara	Java	32

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Students students;

    @Autowired
    Marks marks;

    @Autowired
    Subjects subjects;

    @DisplayName("All beans successfully loaded")
    @Test
    void contextLoads() {
	assertNotNull(students);
	assertNotNull(marks);
	assertNotNull(subjects);
    }

    ObjectMapper mapper = new ObjectMapper();

    public String assertHttpStatus(String method, String url) throws Exception, UnsupportedEncodingException {

	Map<String, RequestBuilder> methods = new HashMap<String, RequestBuilder>();
	methods.put(GET, MockMvcRequestBuilders.get(url));
	methods.put(POST, MockMvcRequestBuilders.post(url));
	methods.put(PUT, MockMvcRequestBuilders.put(url));
	methods.put(DELETE, MockMvcRequestBuilders.delete(url));

	MockHttpServletResponse responce = mockMvc.perform(methods.get(method)).andReturn().getResponse();

	assertEquals(HttpStatus.OK, HttpStatus.valueOf(responce.getStatus()));
	return responce.getContentAsString();
    }

    @DisplayName(CONTROLLERS)
    @Nested
    class testsControllers {
	@Nested
	class studentsController {
	    @DisplayName(GET)
	    @Sql(FILL_TABELS_SQL)
	    @Nested
	    class testsGet {
		@DisplayName(STUDENTS + "?name=Moshe")
		@Test
		void testFindStudentByName() throws Exception {

		    String json = assertHttpStatus(GET, STUDENTS + "?name=Moshe");
		    StudentRaw studentByName = students.findByName("Moshe");

		    assertEquals(1, studentByName.getStid());
		    assertEquals("Moshe", studentByName.getName());

		    assertEquals(mapper.writeValueAsString(studentByName), json);
		}

		@DisplayName(STUDENTS + "/1")
		@Test
		void testFindStudentByStid() throws Exception {
		    String json = assertHttpStatus(GET, STUDENTS + "/1");
		    StudentRaw studentByStid = students.findByStid(1);

		    assertEquals(1, studentByStid.getStid());
		    assertEquals("Moshe", studentByStid.getName());

		    assertEquals(mapper.writeValueAsString(studentByStid), json);
		}

		@DisplayName(STUDENTS + BEST)
		@Test
		void testBestStudents() throws Exception {
		    String json = assertHttpStatus(GET, STUDENTS + BEST);
		    List<String> bestStudents = students.bestStudents();

		    assertEquals(1, bestStudents.size());
		    assertEquals("Izhak", bestStudents.get(0));

		    assertEquals(mapper.writeValueAsString(bestStudents), json);
		}

		@DisplayName(STUDENTS + BEST + "?limit=2")
		@Test
		void testBestStudentsWithLimit() throws Exception {
		    String json = assertHttpStatus(GET, STUDENTS + BEST + "?n_students=2");
		    List<String> bestStudents = students.bestStudents(2);

		    assertEquals(2, bestStudents.size());
		    assertEquals("Izhak", bestStudents.get(0));
		    assertEquals("Moshe", bestStudents.get(1));

		    assertEquals(mapper.writeValueAsString(bestStudents), json);
		}

		@DisplayName(STUDENTS + BEST + "?subject=Java")
		@Test
		void testStudentsSubject() throws Exception {
		    String json = assertHttpStatus(GET, STUDENTS + BEST + "?subject=Java");
		    List<String> bestStudents = students.studentsSubject("Java");
		    assertEquals(1, bestStudents.size());
		    assertEquals("Izhak", bestStudents.get(0));

		    assertEquals(mapper.writeValueAsString(bestStudents), json);
		}

		@DisplayName(STUDENTS + BEST + "?subject=Java&n_students=5")
		@Test
		void testBestStudentsSubect() throws Exception {
		    String json = assertHttpStatus(GET, STUDENTS + BEST + "?subject=Java&n_students=5");
		    List<String> bestStudents = students.bestStudentsSubect(5, "Java");

		    assertEquals(3, bestStudents.size());
		    assertEquals("Izhak", bestStudents.get(0));
		    assertEquals("Moshe", bestStudents.get(1));
		    assertEquals("Sara", bestStudents.get(2));

		    assertEquals(mapper.writeValueAsString(bestStudents), json);
		}

		@DisplayName(STUDENTS + MARKS + COUNT)
		@Test
		void testStudentsMarksCount() throws Exception {

		    String json = assertHttpStatus(GET, STUDENTS + MARKS + COUNT);
		    List<StudentsMarksCount> bestStudents = students.getStudentsMarksCount();

		    assertEquals(4, bestStudents.size());
		    assertEquals("Moshe", bestStudents.get(0).getName());
		    assertEquals(8L, bestStudents.get(0).getMarksCount());

		    assertEquals("Izhak", bestStudents.get(1).getName());
		    assertEquals(6L, bestStudents.get(1).getMarksCount());

		    assertEquals("Sara", bestStudents.get(2).getName());
		    assertEquals(6L, bestStudents.get(2).getMarksCount());

		    assertEquals("Lilit", bestStudents.get(3).getName());
		    assertEquals(2L, bestStudents.get(3).getMarksCount());

		    assertEquals(mapper.writeValueAsString(bestStudents), json);

		}
	    }

	    @DisplayName(DELETE)
	    @Nested
	    class testsDelete {
		@DisplayName(STUDENTS + "/1")
		@Test
		@Sql(FILL_TABELS_SQL)
		void testDeleteByStid() throws Exception {

		    assertHttpStatus(DELETE, STUDENTS + "/1");
		    String json = assertHttpStatus(GET, STUDENTS + "/1");
		    assertEquals("", json);

		}

		@DisplayName(STUDENTS + "?name=Moshe")
		@Test
		@Sql(FILL_TABELS_SQL)
		void testDeleteByName() throws Exception {
		    assertHttpStatus(DELETE, STUDENTS + "?name=Moshe");
		    String json = assertHttpStatus(GET, STUDENTS + "?name=Moshe");
		    assertEquals("", json);
		}
	    }
	}

	@Nested
	class subjectsController {
	    @DisplayName(GET)
	    @Sql(FILL_TABELS_SQL)
	    @Nested
	    class testsGet {
		@DisplayName(SUBJECTS + MARKS + COUNT)
		@Test

		void testGetSubjectsHighestMarks() throws Exception {

		    String json = assertHttpStatus(GET, SUBJECTS + MARKS + COUNT);
		    List<String> topSubject = subjects.getSubjectsHighestMarks();

		    assertEquals(1, topSubject.size());
		    assertEquals("Java", topSubject.get(0));

		    assertEquals(mapper.writeValueAsString(topSubject), json);
		}

		@DisplayName(SUBJECTS + MARKS + COUNT + "?n_subjects=2")
		@Test

		void testGetSubjectsHighestMarksLimit() throws Exception {

		    String json = assertHttpStatus(GET, SUBJECTS + MARKS + COUNT + "?n_subjects=2");
		    List<String> topSubject = subjects.getTopSubjectsHighestMarks(2);

		    assertEquals(2, topSubject.size());
		    assertEquals("Java", topSubject.get(0));
		    assertEquals("CSS", topSubject.get(1));

		    assertEquals(mapper.writeValueAsString(topSubject), json);
		}
	    }

	    @DisplayName(PUT)
	    @Nested
	    class testsPut {
		@DisplayName(SUBJECTS)
		@Test
		@Sql(FILL_TABELS_SQL)
		void testPutSubjects() throws Exception {
		    assertHttpStatus(PUT, SUBJECTS);
		    String[] names = { "Moshe", "Sara", "Izhak" };
		    for (String name : names) {
			String json = assertHttpStatus(GET, MARKS + COUNT + "?subject_name=Java&student_name=" + name);
			assertEquals("1", json);
		    }

		}
	    }
	}

	@Nested
	class marksController {
	    @DisplayName(GET)
	    @Sql(FILL_TABELS_SQL)
	    @Nested
	    class testsGet {
		@DisplayName(MARKS + COUNT)
		@Test

		void testGetMarksCount() throws Exception {
		    String json = assertHttpStatus(GET, MARKS + COUNT + "?subject_name=Java&student_name=Moshe");
		    assertEquals("5", json);
		}

		@DisplayName(MARKS + COUNT + ALL_GT_ONE)
		@Test
		void testGetMarksAllCountGtOneBySubject() throws Exception {
		    String json = assertHttpStatus(GET, MARKS + COUNT + ALL_GT_ONE);
		    assertEquals("[{\"count\":5,\"stid\":1,\"suid\":1,\"avg\":62},"
			    + "{\"count\":4,\"stid\":2,\"suid\":1,\"avg\":57},"
			    + "{\"count\":4,\"stid\":3,\"suid\":1,\"avg\":80}]", json);
		}

		@DisplayName(MARKS + WIDESPREAD + "/Java?n_marks=3")
		@Test
		void testGetMarksWideSpred() throws Exception {

		    String json = assertHttpStatus(GET, MARKS + WIDESPREAD + "/Java?n_marks=3");
		    List<Integer> topMarks = marks.getTopMarksEncountered(3, "Java");

		    assertEquals(3, topMarks.size());
		    assertEquals(70, topMarks.get(0));
		    assertEquals(45, topMarks.get(1));
		    assertEquals(99, topMarks.get(2));

		    assertEquals(mapper.writeValueAsString(topMarks), json);
		}

		@DisplayName(MARKS + DISTRIBUTION + "/?interval=10")
		@Test
		void testGetDistribution() throws Exception {

		    String json = assertHttpStatus(GET, MARKS + DISTRIBUTION + "/?interval=10");

		    List<IntervalMarksDto> distribution = marks.getIntervalsMarks(10);
		    assertEquals(7, distribution.size());

		    int[][] expected = { { 2, 30, 39 }, { 5, 40, 49 }, { 2, 60, 69 }, { 4, 70, 79 }, { 5, 80, 89 },
			    { 2, 90, 99 }, { 2, 100, 109 } };
		    for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i][0], distribution.get(i).getOccurrences());
			assertEquals(expected[i][1], distribution.get(i).getMinVal());
			assertEquals(expected[i][2], distribution.get(i).getMaxVal());
		    }

		    assertEquals(mapper.writeValueAsString(distribution), json);
		}
	    }

	    @DisplayName(DELETE)
	    @Nested
	    class testsDelete {
		@DisplayName(MARKS + "/Java?name=Moshe")
		@Test
		@Sql(FILL_TABELS_SQL)
		void testDeleteByName() throws Exception {

		    assertHttpStatus(DELETE, MARKS + "/Java?name=Moshe");
		    String json = assertHttpStatus(GET, MARKS + COUNT + "?subject_name=Java&student_name=Moshe");
		    assertEquals("0", json);
		}
	    }
	}
    }

    @DisplayName(SERVISES)
    @Nested
    class testsServices {
	@Nested
	class studentsSerivce {
	    @Test
	    @Sql(FILL_TABELS_SQL)
	    void testDeleteByStid() throws Exception {

		students.deleteByStid(1);
		StudentRaw studentByStid = students.findByStid(1);
		assertEquals(null, studentByStid);

	    }

	    @Test
	    @Sql(FILL_TABELS_SQL)
	    void testDeleteByName() throws Exception {

		students.deleteByName("Moshe");
		StudentRaw studentByStid = students.findByName("Moshe");
		assertEquals(null, studentByStid);
	    }
	}

	@Nested
	class marksSerivce {
	    @Test
	    @Sql(FILL_TABELS_SQL)
	    void testDeleteByNameAndSubject() throws Exception {
		marks.deleteByNameAndSubject("Moshe", "Java");
		Long count = marks.getCountMarksByStudentNameAndSubjectName("Moshe", "Java");
		assertEquals(0, count);
	    }
	}

	@Nested
	class subjectsService {
	    @Test
	    @Sql(FILL_TABELS_SQL)
	    void testSetAveragingSubjectMarks() throws Exception {
		subjects.setAveragingSubjectMarks();

		List<StudentsMarksCount> bestStudents = students.getStudentsMarksCount();

		assertEquals(4, bestStudents.size());
		assertEquals("Moshe", bestStudents.get(0).getName());
		assertEquals(4, bestStudents.get(0).getMarksCount());

		assertEquals("Izhak", bestStudents.get(1).getName());
		assertEquals(3, bestStudents.get(1).getMarksCount());

		assertEquals("Sara", bestStudents.get(2).getName());
		assertEquals(3, bestStudents.get(2).getMarksCount());

		assertEquals("Lilit", bestStudents.get(3).getName());
		assertEquals(2, bestStudents.get(3).getMarksCount());
	    }
	}
    }

}
