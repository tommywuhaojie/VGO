package v_go.version10.APITests;
import org.junit.*;

import v_go.version10.ApiClasses.LocalHostApi;
import v_go.version10.ApiClasses.User;

public class LoginTests {

    private User user;
    private LocalHostApi localServer;

    @Before
    public void setUp() throws Exception {
        user = new User();
        localServer = new LocalHostApi();
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

    @Test
    public void localApiRegister(){
        String res = localServer.Register("123456@qq.com", "aA123456");
        System.out.println("Server: " + res);

    }

    @After
    public void cleanUp(){

    }
}
