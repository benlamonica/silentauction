package us.pojo.silentauction.model;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void shouldReturnShortName() {
        assertThat(new User(1, "", "Ben La Monica", "", true, true, true).getShortName(), is("Ben L"));
    }
    
    @Test
    public void shouldReturnShortNameEvenWhenTheNameIsAlreadyShort() {
        assertThat("did not shorten name correctly!", new User(1, "", "Brad", "", true, true, true).getShortName(), is("Brad"));
    }
}
