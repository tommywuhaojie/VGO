package v_go.version10.APITests;
import org.junit.*;

import v_go.version10.ApiClasses.User;

public class LoginTests {

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User();
    }

    @Test
    public void loginWithValidAccount() throws Exception {
        /*
        * return value:
        * -1    : fail to login
        *  1    : successfully login as passenger
        * */
        String result = user.Login("1165637488@qq.com", "123456");
        Assert.assertEquals("1", result);
    }

    @Test
    public void loginWithInvalidAccount(){
        String result = user.Login(" ", " ");
        Assert.assertEquals("-1", result);
    }

    @After
    public void cleanUp(){

    }
}
