package it.aldinucci.todoapp.adapter.out.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.aldinucci.todoapp.adapter.out.persistence.entity.ProjectJPA;
import it.aldinucci.todoapp.adapter.out.persistence.entity.UserJPA;
import it.aldinucci.todoapp.adapter.out.persistence.repository.ProjectJPARepository;
import it.aldinucci.todoapp.adapter.out.persistence.repository.UserJPARepository;
import it.aldinucci.todoapp.application.port.out.CreateProjectDriverPort;
import it.aldinucci.todoapp.application.port.out.dto.NewProjectData;
import it.aldinucci.todoapp.domain.Project;
import it.aldinucci.todoapp.exception.AppUserNotFoundException;
import it.aldinucci.todoapp.mapper.AppGenericMapper;

@Component
public class CreateProjectJPA implements CreateProjectDriverPort{
	
	private UserJPARepository userRepository;
	
	private ProjectJPARepository projectRepository;
	
	private AppGenericMapper<ProjectJPA, Project> mapper;

	@Autowired
	public CreateProjectJPA(UserJPARepository userRepository, ProjectJPARepository projectRepository,
			AppGenericMapper<ProjectJPA, Project> mapper) {
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
		this.mapper = mapper;
	}

	@Override
	public Project create(NewProjectData project) throws AppUserNotFoundException{
		UserJPA user = userRepository.findByEmail(project.getUserEmail()).orElseThrow(() 
				->new AppUserNotFoundException("User not found with email: "+project.getUserEmail()));
		ProjectJPA projectJPA = new ProjectJPA(project.getName(), user);
		user.getProjects().add(projectJPA);
		projectJPA = projectRepository.save(projectJPA);
		userRepository.saveAndFlush(user);
		return mapper.map(projectJPA);
	}


}
