package it.aldinucci.todoapp.adapter.in.rest.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.aldinucci.todoapp.application.port.in.LoadUnfinishedTasksByProjectIdUsePort;
import it.aldinucci.todoapp.application.port.in.dto.ProjectIdDTO;
import it.aldinucci.todoapp.domain.Task;
import it.aldinucci.todoapp.exception.AppProjectNotFoundException;
import it.aldinucci.todoapp.webcommons.security.authorization.InputModelAuthorization;

@RestController
@RequestMapping("/api")
public class LoadUnfinishedTaskByProjectIdRestController {

	private LoadUnfinishedTasksByProjectIdUsePort loadProjectTasks;
	private InputModelAuthorization<ProjectIdDTO> authorize;
	
	@Autowired
	public LoadUnfinishedTaskByProjectIdRestController(LoadUnfinishedTasksByProjectIdUsePort loadProjects,
			InputModelAuthorization<ProjectIdDTO> authorize) {
		this.loadProjectTasks = loadProjects;
		this.authorize = authorize;
	}

	@GetMapping("/project/{projectId}/tasks/unfinished")
	public List<Task> getTasksByProjectEndPoint(Authentication authentication, @Valid ProjectIdDTO projectId)
				throws AppProjectNotFoundException{
		authorize.check(authentication.getName(), projectId);
		return loadProjectTasks.load(projectId);
	}
}
