package LocalHostApiTests;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import v_go.version10.ApiClasses.LocalHostApi;


public class LocalRegisterTest {

    @Test
    public void LocalTests() throws  Exception
    {
        JSONObject result1 = LocalHostApi.Register("7686881234", "116567444@qq.com", "aA123456", "Tommy", "Wu");
        System.out.println("-->Register Result: " + result1.toString());
        Assert.assertNotEquals("1", result1.getString("code"));

    }

    @Test
    public void Logout() throws Exception
    {
        JSONObject result1 = LocalHostApi.Logout();
        System.out.println("--> Logout Result: " + result1.toString());
    }
}
