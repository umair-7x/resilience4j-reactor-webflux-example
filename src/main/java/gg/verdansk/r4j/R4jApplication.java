package gg.verdansk.r4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(value = {"gg.verdansk.*"})
@EnableWebMvc
@EnableSwagger2
public class R4jApplication {

	public static void main(String[] args) {
		SpringApplication.run(R4jApplication.class, args);
	}

}
