package it.aldinucci.todoapp.application.port.in;

import it.aldinucci.todoapp.application.port.in.dto.TaskIdDTO;
import it.aldinucci.todoapp.exception.AppTaskNotFoundException;

public interface DeleteTaskByIdUsePort {

	public void delete(TaskIdDTO task) throws AppTaskNotFoundException;
}
