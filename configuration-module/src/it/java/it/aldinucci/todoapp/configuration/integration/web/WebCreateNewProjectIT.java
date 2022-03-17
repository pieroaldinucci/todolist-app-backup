package it.aldinucci.todoapp.configuration.integration.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import it.aldinucci.todoapp.adapter.out.persistence.entity.ProjectJPA;
import it.aldinucci.todoapp.adapter.out.persistence.entity.UserJPA;
import it.aldinucci.todoapp.adapter.out.persistence.repository.ProjectJPARepository;
import it.aldinucci.todoapp.adapter.out.persistence.repository.UserJPARepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class WebCreateNewProjectIT {

	@Autowired
	private UserJPARepository userRepo;
	
	@Autowired
	private ProjectJPARepository projectRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@LocalServerPort
	int appPort;
	
	private String baseUrl;
	
	private WebDriver webDriver;
	
	@BeforeEach
	void setUp() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		databaseSetup();
		webDriver = new HtmlUnitDriver();
		baseUrl = "http://localhost:"+appPort;
		doLogin();
	}

	@Test
	void test_createNewProjects() {
		webDriver.get(baseUrl+"/web");
		webDriver.findElement(ById.id("open-new-project-card")).click();
		webDriver.findElement(ByName.name("name")).sendKeys("New Test Project");
		webDriver.findElement(By.name("submit-button")).click();
		
		assertThat(webDriver.findElement(By.cssSelector("h1")).getText())
			.contains("New Test Project");		
		
		webDriver.findElement(ById.id("open-new-project-card")).click();
		webDriver.findElement(ByName.name("name")).sendKeys("Second Test Project");
		webDriver.findElement(By.name("submit-button")).click();
		
		assertThat(webDriver.findElement(By.cssSelector("h1")).getText())
			.contains("Second Test Project");
		
		List<ProjectJPA> projects = projectRepo.findAll();
		assertThat(projects).hasSize(2);
		ProjectJPA project1 = projects.get(0);
		ProjectJPA project2 = projects.get(1);
		assertThat(project1.getName()).matches("New Test Project");
		assertThat(project2.getName()).matches("Second Test Project");
		
		WebElement project1Link = webDriver.findElement(By.linkText("New Test Project"));
		assertThat(project1Link.getAttribute("href"))
			.matches(baseUrl+"/web/project/"+project1.getId()+"/tasks");
		assertThat(webDriver.findElement(By.linkText("Second Test Project")).getAttribute("href"))
			.matches(baseUrl+"/web/project/"+project2.getId()+"/tasks");
		
		assertThat(webDriver.getCurrentUrl()).matches(baseUrl+"/web/project/"+project2.getId()+"/tasks");
		
		project1Link.click();
		assertThat(webDriver.getCurrentUrl()).matches(baseUrl+"/web/project/"+project1.getId()+"/tasks");
	}
	
	
	private void databaseSetup() {
		userRepo.deleteAll();
		userRepo.saveAndFlush(new UserJPA(null, "user@email.it", "User test", encoder.encode("test2Pass"), true,
				Collections.emptyList()));
	}
	
	private void doLogin() {
		webDriver.get(baseUrl);
		
		webDriver.findElement(By.name("username")).sendKeys("user@email.it");
		webDriver.findElement(By.name("password")).sendKeys("test2Pass");
		webDriver.findElement(By.name("log-in")).click();
	}
}
