import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import v_go.version10.ApiClasses.LocalHostApi;

public class LocalHostApiTests {

    private LocalHostApi localServer;

    @Before
    public void setUp() throws Exception {
        localServer = new LocalHostApi();
    }

    @Test
    public void Register() throws Exception
    {
        JSONObject result = localServer.Register("7781111111", "1165637488@qq.com", "aA123456", "Tommy", "Wu");
        System.out.println("-->Sign up Result: " + result.getString("code") + " " + result.getString("msg"));
    }
    @Test
    public void login() throws  Exception
    {
        /*
        * -1    : fail to login
        *  1    : successfully login
        * */
        JSONObject result2 = localServer.Login("7786889383", "aA123456");
        System.out.println("-->Login Result: " + result2.getString("code") + " " + result2.getString("msg"));
    }
}
