package it.aldinucci.todoapp.application.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aldinucci.todoapp.application.port.in.LoadProjectsByUserUsePort;
import it.aldinucci.todoapp.application.port.in.dto.UserIdDTO;
import it.aldinucci.todoapp.application.port.out.LoadProjectsByUserDriverPort;
import it.aldinucci.todoapp.domain.Project;

@Service
@Transactional
public class LoadUserProjectsService implements LoadProjectsByUserUsePort{

	private final LoadProjectsByUserDriverPort loadProjectsPort;
	
	@Autowired
	public LoadUserProjectsService(LoadProjectsByUserDriverPort port) {
		this.loadProjectsPort = port;
	}
	
	@Override
	public List<Project> load(UserIdDTO userId) {
		return loadProjectsPort.load(userId.getEmail());
	}

}