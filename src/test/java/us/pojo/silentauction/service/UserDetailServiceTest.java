package us.pojo.silentauction.service;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import us.pojo.silentauction.model.User;
import us.pojo.silentauction.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailServiceTest {
    @Mock
    private UserRepository users;
    
    @Mock
    private PasswordEncoder pwEncoder;
    
    @InjectMocks
    private UserDetailService target;
    
    @Test
    public void shouldFormatPhoneNumber() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "630-453-9090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldFormatPhoneNumberWithSpaces() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "(630)453-9090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldFormatPhoneNumberWhenAlreadyInCorrectFormat() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldCopyValuesIntoNewSavedObject() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, true, true));
        assertThat(result.getName(), is("Ben La Monica"));
        assertThat(result.wantsSms(), is(true));
        assertThat(result.wantsEmail(), is(true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

}
