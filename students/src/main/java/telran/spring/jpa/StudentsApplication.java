package telran.spring.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StudentsApplication {

    public static void main(String[] args) {

	ConfigurableApplicationContext ctx = SpringApplication.run(StudentsApplication.class, args);
	JPQLQueryConsole console = ctx.getBean(JPQLQueryConsole.class);
	console.run();
    }

}
