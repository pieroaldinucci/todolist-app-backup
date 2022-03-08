package it.aldinucci.todoapp.adapter.in.web.controller.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.VoidAnswer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import it.aldinucci.todoapp.adapter.in.web.controller.RegisterUserWebController;
import it.aldinucci.todoapp.adapter.in.web.controller.ResendVerificationTokenController;
import it.aldinucci.todoapp.adapter.in.web.dto.RegisterUserDto;
import it.aldinucci.todoapp.adapter.in.web.validator.RegisterUserValidator;
import it.aldinucci.todoapp.application.port.in.CreateUserUsePort;
import it.aldinucci.todoapp.application.port.in.RetrieveVerificationTokenUsePort;
import it.aldinucci.todoapp.application.port.in.SendVerificationEmailUsePort;
import it.aldinucci.todoapp.application.port.in.dto.NewUserDTOIn;
import it.aldinucci.todoapp.application.port.in.dto.NewUserDtoOut;
import it.aldinucci.todoapp.application.port.in.dto.VerificationLinkDTO;
import it.aldinucci.todoapp.domain.User;
import it.aldinucci.todoapp.domain.VerificationToken;
import it.aldinucci.todoapp.exceptions.AppEmailAlreadyRegisteredException;
import it.aldinucci.todoapp.mapper.AppGenericMapper;

/**
 * These test class should only check if the view will interact correctly with the
 * controller, is not his responsibility to test the controller correct behavior.
 *  
 * @author piero
 *
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {RegisterUserWebController.class, ResendVerificationTokenController.class})
class RegisterUserWebViewTest {
	
	@Autowired
	private WebClient webClient;
	
	@MockBean
	private CreateUserUsePort createUser;
	
	@MockBean
	private AppGenericMapper<RegisterUserDto, NewUserDTOIn> mapper;
	
	@MockBean
	private RegisterUserValidator userValidator;
	
	@MockBean
	private SendVerificationEmailUsePort sendMail;
	
	@MockBean
	private RetrieveVerificationTokenUsePort retrieveToken;
	
	@SpyBean
	private RegisterUserWebController controller;
	
	@Autowired
	private Validator validator;
	
	private HtmlPage page;
	
	@BeforeEach
	void setUp() throws FailingHttpStatusCodeException, MalformedURLException, IOException, AppEmailAlreadyRegisteredException {
		when(userValidator.supports(RegisterUserDto.class)).thenReturn(true);
		when(mapper.map(isA(RegisterUserDto.class))).thenReturn(new NewUserDTOIn("name", "user@email.it", "test"));
		when(createUser.create(isA(NewUserDTOIn.class))).thenReturn(
				new NewUserDtoOut(
						new User("user@email.it",null,null),
						new VerificationToken()));
		doNothing().when(sendMail).send(isA(VerificationLinkDTO.class));
		
		page = webClient.getPage("/user/register");
	}
	
	
	@Test
	void test_registrationForm_filledSuccess() throws ElementNotFoundException, IOException{
		doNothing().when(userValidator).validate(any(), any());
		
		HtmlForm form = page.getFormByName("user-register");
		form.getInputByName("email").setValueAttribute("user@email.it");
		form.getInputByName("username").setValueAttribute("name");
		form.getInputByName("password").setValueAttribute("test");
		form.getInputByName("confirmedPassword").setValueAttribute("test");
		form.getButtonByName("form-submit").click();
		
		RegisterUserDto userDto = new RegisterUserDto("user@email.it", "name", "test", "test");
		ArgumentCaptor<BindingResult> bindingResult = ArgumentCaptor.forClass(BindingResult.class);
		verify(controller).postRegistrationPage(eq(userDto), bindingResult.capture());
		assertThat(bindingResult.getValue().getAllErrors()).isEmpty();
	}
	
	@Test
	void test_registrationForm_ValidationErrors() throws ElementNotFoundException, IOException{
		doAnswer(answerVoid(new VoidAnswer2<Object, Errors>() {

			@Override
			public void answer(Object argument0, Errors argument1) throws Throwable {
				validator.validate(argument0, argument1);
			}

		})).when(userValidator).validate(any(), any());
		
		HtmlForm form = page.getFormByName("user-register");
		HtmlInput emailInput = form.getInputByName("email");
		emailInput.setValueAttribute("useremail.it");
		form.getInputByName("username").setValueAttribute("");
		form.getInputByName("password").setValueAttribute("");
		form.getInputByName("confirmedPassword").setValueAttribute("");
		form.getButtonByName("form-submit").click();
		
		RegisterUserDto userDto = new RegisterUserDto("useremail.it", "", "", "");
		ArgumentCaptor<BindingResult> bindingResult = ArgumentCaptor.forClass(BindingResult.class);
		verify(controller).postRegistrationPage(eq(userDto), bindingResult.capture());
		assertThat(bindingResult.getValue().getAllErrors()).hasSize(4);
	}
	
	@Test
	void test_registrationForm_linkToResendToken(){
		assertThatCode(() -> page.getAnchorByHref("/user/register/resend/verification").click())
			.doesNotThrowAnyException();
	}

}