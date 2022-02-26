package it.aldinucci.todoapp.webcommons.security.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.aldinucci.todoapp.application.port.in.LoadUserByTaskIdUsePort;
import it.aldinucci.todoapp.application.port.in.dto.TaskIdDTO;
import it.aldinucci.todoapp.domain.User;
import it.aldinucci.todoapp.webcommons.exception.UnauthorizaedWebAccessException;

@Component
public class TaskIdWebAuthorization implements InputModelAuthorization<TaskIdDTO> {

	private LoadUserByTaskIdUsePort loadUser;
	
	@Autowired
	public TaskIdWebAuthorization(LoadUserByTaskIdUsePort loadUser) {
		this.loadUser = loadUser;
	}

	@Override
	public void check(String authenticatedEmail, TaskIdDTO model) throws UnauthorizaedWebAccessException {
		User user = loadUser.load(model);
		if(!authenticatedEmail.equals(user.getEmail()))
				throw new UnauthorizaedWebAccessException("This operation is not permitted for the authenticated user");
	}

}