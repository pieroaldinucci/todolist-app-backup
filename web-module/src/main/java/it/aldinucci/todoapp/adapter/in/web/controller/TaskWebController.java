package it.aldinucci.todoapp.adapter.in.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.aldinucci.todoapp.adapter.in.web.dto.UserWebDto;
import it.aldinucci.todoapp.application.port.in.LoadProjectsByUserUsePort;
import it.aldinucci.todoapp.application.port.in.LoadTasksByProjectUsePort;
import it.aldinucci.todoapp.application.port.in.LoadUserByProjectIdUsePort;
import it.aldinucci.todoapp.application.port.in.dto.ProjectIdDTO;
import it.aldinucci.todoapp.application.port.in.dto.UserIdDTO;
import it.aldinucci.todoapp.domain.User;
import it.aldinucci.todoapp.mapper.AppGenericMapper;
import it.aldinucci.todoapp.webcommons.security.authorization.InputModelAuthorization;

@Controller
@RequestMapping("/project/{projectId}")
public class TaskWebController {
	
	private LoadTasksByProjectUsePort loadTasks;
	private LoadUserByProjectIdUsePort loadUser;
	private LoadProjectsByUserUsePort loadProjects;
	private AppGenericMapper<User, UserWebDto> userMapper;
	private InputModelAuthorization<User> authorize;


	@Autowired
	public TaskWebController(LoadTasksByProjectUsePort loadTasks, LoadUserByProjectIdUsePort loadUser,
			LoadProjectsByUserUsePort loadProjects, AppGenericMapper<User, UserWebDto> userMapper,
			InputModelAuthorization<User> authorize) {
		super();
		this.loadTasks = loadTasks;
		this.loadUser = loadUser;
		this.loadProjects = loadProjects;
		this.userMapper = userMapper;
		this.authorize = authorize;
	}

	@GetMapping("/tasks")
	public String getTasks(Authentication authentication, Model model, @Valid ProjectIdDTO projectId) {
		User user = loadUser.load(projectId);
		
		authorize.check(authentication.getName(), user);
		model.addAttribute("user", userMapper.map(user));		
		model.addAttribute("projects", loadProjects.load(new UserIdDTO(user.getEmail())));
		model.addAttribute("tasks",loadTasks.load(projectId));
		
		return "index";
	}
	

}
