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
        String result = user.Login("invalid@qq.com", "000000");
        Assert.assertEquals("0", result);
    }


    @Test
    public void loginWithAccountWithSpace(){
        String result = user.Login(" 1165637488@qq.com", "123456");
        Assert.assertEquals("0",result);
    }
    /*@Test
    public void register(){
        String res = user.Register("testerjava@qq.com", "aA123456", "7789451236", "superman", "tester");
        Assert.assertEquals("1", res);
    }
    */

    @After
    public void cleanUp(){

    }
}