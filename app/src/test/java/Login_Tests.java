import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import v_go.version10.ApiClasses.UserApi;

/**
 * Created by cindyling on 2016-05-31.
 */
public class Login_Tests {

    @Test
    public void Register_login_logout_logout_tests() throws  Exception
    {
        JSONObject result1 = UserApi.Register("7786889380", "1165637488@qq.com", "aA123456", "Tommy", "Wu");
        System.out.println("-->Register Result: " + result1.toString());
        Assert.assertNotEquals("1", result1.getString("code"));

        JSONObject result2 = UserApi.Login("7786889380", "aA123456");
        System.out.println("-->Login Result: " + result2.toString());
        Assert.assertEquals("1", result2.getString("code"));

        JSONObject result3 = UserApi.Logout();
        System.out.println("-->Logout Result: " + result3.toString());
        Assert.assertEquals("1", result3.getString("code"));

        Assert.assertEquals(result2.getString("session_id"),result3.getString("session_id"));

        JSONObject result4 = UserApi.Logout();
        System.out.println("-->Logout Result: " + result4.toString());
        Assert.assertNotEquals("1", result4.getString("code"));
        Assert.assertNotEquals(result4.getString("session_id"),result3.getString("session_id"));
    }

    @Test
    public void Incorrect_Phone_Number () throws Exception
    {
        JSONObject result = UserApi.Login("0123456789", "aA123456");
        System.out.println("-->Login Result: " + result.toString());
        Assert.assertNotEquals("1", result.getString("code"));
    }

    @Test
    public void Incorrect_Password () throws Exception
    {
        JSONObject result = UserApi.Login("7786889380", "0123456");
        System.out.println("-->Login Result: " + result.toString());
        Assert.assertNotEquals("1", result.getString("code"));
    }

    @Test
    public void Logout_without_Login () throws  Exception
    {
        JSONObject result = UserApi.Logout();
        System.out.println("-->Logout Result: " + result.toString());
        Assert.assertNotEquals("1", result.getString("code"));
    }

}
