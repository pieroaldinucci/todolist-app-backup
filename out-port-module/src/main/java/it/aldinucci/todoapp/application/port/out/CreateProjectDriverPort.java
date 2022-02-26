package it.aldinucci.todoapp.application.port.out;

import it.aldinucci.todoapp.application.port.out.dto.NewProjectDTOOut;
import it.aldinucci.todoapp.domain.Project;

public interface CreateProjectDriverPort {
	
	public Project create(NewProjectDTOOut project);
}