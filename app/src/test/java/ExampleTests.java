import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import v_go.version10.ApiClasses.User;

public class ExampleTests {

    @Test
    public void Register_login_logout_example_tests() throws  Exception
    {
        JSONObject result1 = User.Register("7786889380", "1165637488@qq.com", "aA123456", "Tommy", "Wu");
        System.out.println("-->Register Result: " + result1.toString());
        Assert.assertNotEquals("1", result1.getString("code"));

        JSONObject result2 = User.Login("7786889380", "aA123456");
        System.out.println("-->Login Result: " + result2.toString());
        Assert.assertEquals("1", result2.getString("code"));

        JSONObject result3 = User.Logout();
        System.out.println("-->Logout Result: " + result3.toString());
        Assert.assertEquals("1", result3.getString("code"));
    }

}
