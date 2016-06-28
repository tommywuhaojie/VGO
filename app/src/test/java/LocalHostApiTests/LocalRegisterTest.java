package LocalHostApiTests;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import v_go.version10.ApiClasses.LocalHostApi;


public class LocalRegisterTest {


    @Test
    public void Login() throws Exception
    {
        JSONObject result = LocalHostApi.Login("7786889383", "aA1234asd56");
        System.out.println("--> Login Result: " + result.toString());
    }

    @Test
    public void LocalTests() throws  Exception
    {
        JSONObject result1 = LocalHostApi.Register("7786889153", "qaaa@qa.com", "aA123456", "QA", "QA");
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
