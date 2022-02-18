package it.aldinucci.todoapp.application.service;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.aldinucci.todoapp.application.port.in.CreateProjectUsePort;
import it.aldinucci.todoapp.application.port.in.dto.NewProjectDTOIn;
import it.aldinucci.todoapp.application.port.out.CreateProjectDriverPort;
import it.aldinucci.todoapp.application.port.out.dto.NewProjectDTOOut;
import it.aldinucci.todoapp.domain.Project;

@Service
@Transactional
class CreateNewProjectService implements CreateProjectUsePort {

	private final CreateProjectDriverPort newProjectDriverPort;

	private ModelMapper mapper;

	@Autowired
	public CreateNewProjectService(CreateProjectDriverPort newProjectDriverPort, ModelMapper mapper) {
		this.newProjectDriverPort = newProjectDriverPort;
		this.mapper = mapper;
	}

	@Override
	public Project create(NewProjectDTOIn projectDto) {

		return newProjectDriverPort.create(mapper.map(projectDto, NewProjectDTOOut.class));
	}

}
