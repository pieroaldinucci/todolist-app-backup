package it.aldinucci.todoapp.configuration.integration.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import it.aldinucci.todoapp.adapter.out.persistence.entity.UserJPA;
import it.aldinucci.todoapp.adapter.out.persistence.entity.VerificationTokenJPA;
import it.aldinucci.todoapp.adapter.out.persistence.repository.UserJPARepository;
import it.aldinucci.todoapp.adapter.out.persistence.repository.VerificationTokenJPARepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class UserRegistrationIT {

	@Autowired
	private UserJPARepository userRepo;
	
	@Autowired
	private VerificationTokenJPARepository verificationTokenRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@LocalServerPort
	int appPort;
	
	private WebDriver webDriver;
	
	private GreenMail mailServer;
	
	
	@BeforeEach
	void setUp() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		mailServer = new GreenMail(ServerSetupTest.SMTP)
				.withConfiguration(GreenMailConfiguration.aConfig()
						.withUser("todolist@email.it", "mailPassword"));
		mailServer.start();
		webDriver = new HtmlUnitDriver();
	}
	
	@AfterEach
	void tearDown() {
		webDriver.quit();
	}
	
	@Test
	void test_registrationUser() throws IOException, MessagingException {
		
		webDriver.get("http://localhost:"+appPort+"/user/register");
		webDriver.findElement(By.name("email")).sendKeys("test@email.it");
		webDriver.findElement(By.name("username")).sendKeys("Test User");
		webDriver.findElement(By.name("password")).sendKeys("password");
		webDriver.findElement(By.name("confirmedPassword")).sendKeys("password");
		webDriver.findElement(By.name("form-submit")).click();
		
		Optional<UserJPA> optional = userRepo.findByEmail("test@email.it");
		assertThat(optional).isPresent();
		UserJPA userJPA = optional.get();
		assertThat(userJPA.getUsername()).matches("Test User");
		assertThat(encoder.matches("password", userJPA.getPassword())).isTrue();
		assertThat(userJPA.isEnabled()).isFalse();
		
		Optional<VerificationTokenJPA> optionalToken = verificationTokenRepo.findByUser(userJPA);
		assertThat(optionalToken).isPresent();
		
		MimeMessage receivedMessage = mailServer.getReceivedMessages()[0];
		String messageBody = receivedMessage.getContent().toString();
		assertThat(messageBody).contains(optionalToken.get().getToken());
		
		String verificationUrl = "http://"+messageBody.split("http://")[1].trim();
		
		webDriver.get(verificationUrl);
		
		UserJPA updatedUserJpa = userRepo.findByEmail("test@email.it").get();
		assertThat(updatedUserJpa.isEnabled()).isTrue();
	}
}
