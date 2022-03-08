package it.aldinucci.todoapp.application.port.out;

import it.aldinucci.todoapp.application.port.out.dto.UserData;
import it.aldinucci.todoapp.domain.User;

public interface UpdateUserDriverPort {

	public User update(UserData user);
}
