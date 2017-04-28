package us.pojo.silentauction.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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
    
    @Mock
    private NotificationService notifications;
    
    @InjectMocks
    private UserDetailService target;
    
    private User currentUser = new User(5, "ben.lamonica@gmail.com", "Ben La Monica", null, true, true, true);
    
    @Test
    public void shouldFormatPhoneNumber() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(currentUser, new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "630-453-9090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldFormatPhoneNumberWithSpaces() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(currentUser, new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "(630)453-9090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldFormatPhoneNumberWhenAlreadyInCorrectFormat() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(currentUser, new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, true, true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

    @Test
    public void shouldCopyValuesIntoNewSavedObject() {
        when(users.findUserByEmail("ben.lamonica@gmail.com")).thenReturn(new User());
        User result = target.modifyUser(currentUser, new User(5, "ben.lamonica@gmail.com", "Ben La Monica", "+16304539090", true, true, true));
        assertThat(result.getName(), is("Ben La Monica"));
        assertThat(result.wantsSms(), is(true));
        assertThat(result.wantsEmail(), is(true));
        assertThat(result.getPhone(), is("+16304539090"));
    }

}
