package it.aldinucci.todoapp.adapter.in.web.controller.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

import it.aldinucci.todoapp.adapter.in.web.controller.CreateTaskWebController;
import it.aldinucci.todoapp.adapter.in.web.controller.ProjectWebController;
import it.aldinucci.todoapp.adapter.in.web.dto.NewTaskWebDto;
import it.aldinucci.todoapp.adapter.in.web.dto.UserWebDto;
import it.aldinucci.todoapp.application.port.in.LoadProjectsByUserUsePort;
import it.aldinucci.todoapp.application.port.in.LoadTasksByProjectUsePort;
import it.aldinucci.todoapp.application.port.in.LoadUserByProjectIdUsePort;
import it.aldinucci.todoapp.application.port.in.dto.ProjectIdDTO;
import it.aldinucci.todoapp.application.port.in.dto.UserIdDTO;
import it.aldinucci.todoapp.domain.Project;
import it.aldinucci.todoapp.domain.Task;
import it.aldinucci.todoapp.domain.User;
import it.aldinucci.todoapp.mapper.AppGenericMapper;
import it.aldinucci.todoapp.webcommons.security.authorization.InputModelAuthorization;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {ProjectWebController.class})
@PropertySource("classpath:messages.properties")
class ProjectWebViewTest {

	private static final String FIXTURE_EMAIL = "test@email.it";
	
	@Autowired
	private WebClient webClient;
	
	@MockBean
	private LoadProjectsByUserUsePort loadProjects;
	
	@MockBean
	private LoadTasksByProjectUsePort loadTasks;
	
	@MockBean
	private LoadUserByProjectIdUsePort loadUser;
	
	@MockBean
	private AppGenericMapper<User, UserWebDto> mapper;
	
	@MockBean
	private InputModelAuthorization<User> authorizeUser;
	
	@MockBean
	CreateTaskWebController createTaskController;
	
	@Autowired
	private Environment env;
	
	private List<Project> projects;
	
	private User user;
	
	@BeforeEach
	void setUp() {
		user = new User(FIXTURE_EMAIL, "username", "password");
		projects = Arrays.asList(
				new Project(2L, "test project"),
				new Project(5L, "second project"),
				new Project(7L, "project test"));
		when(loadUser.load(isA(ProjectIdDTO.class))).thenReturn(user);
		when(mapper.map(user)).thenReturn(new UserWebDto("username", FIXTURE_EMAIL));
		when(loadProjects.load(isA(UserIdDTO.class))).thenReturn(projects);
	}
	
	@Test
	@WithMockUser(FIXTURE_EMAIL)
	void test_projectView_shouldContainsAllTask() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		List<Task> tasks = Arrays.asList(
				new Task(11L, "first task", "desc 1"),
				new Task(13L, "second task", "desc 2"));
		
		when(loadTasks.load(isA(ProjectIdDTO.class))).thenReturn(tasks);
		
		HtmlPage page = webClient.getPage("/project/5/tasks");
		
		assertThat(page.getHtmlElementById("active-project-section").getTextContent())
			.contains("second project");
		
		HtmlUnorderedList taskList = page.getHtmlElementById("tasks-list");
		assertThat(taskList.getTextContent())
			.contains("first task")
			.contains("desc 1")
			.contains("second task")
			.contains("desc 2");

		
	}
	
	@Test
	@WithMockUser(FIXTURE_EMAIL)
	void test_projectView_shouldContrainCreateTaskForm() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		when(loadTasks.load(isA(ProjectIdDTO.class))).thenReturn(Collections.emptyList());
		when(createTaskController.createNewTask(any(),any(), any(), any())).thenReturn("test-view-page");
		
		HtmlPage page = webClient.getPage("/project/5/tasks");
		page.getElementById("add-task-link").click();
		HtmlForm form = page.getFormByName("new-task-form");
		form.getInputByName("name").setValueAttribute("test task");
		form.getTextAreaByName("description").setText("Test description");
		HtmlButton formButton = form.getButtonByName("submit-button");
		
		assertThat(formButton.getTextContent()).matches(env.getProperty("add"));
		formButton.click();
	
		verify(createTaskController).createNewTask(
				isA(Authentication.class),
				eq(new ProjectIdDTO(5)),
				eq(new NewTaskWebDto("test task", "Test description")),
				isA(BindingResult.class));
		
	}
}