import org.json.JSONObject;
import org.junit.Assert;
import org.junit.*;
import v_go.version10.ApiClasses.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Register_Tests {

    String Password = "aA123456";
    String LastName = "Ling";
    String FirstName = "Cindy";

    UUID uuid;
    String Email, PhoneNumber;
    long Number;

    @Before
    public void setUp() throws Exception
    {
        uuid = UUID.randomUUID();
        Email = uuid.toString()+"@gmail.com";
        Number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        PhoneNumber = Number + "";
    }

    @Test
    public void Repeated_Phone_Number() throws Exception
    {
        JSONObject result1 = User.Register(PhoneNumber,Email,Password,FirstName,LastName);
        System.out.println("-->Register Result: " + result1.toString());
        Assert.assertEquals("1" , result1.getString("code"));
        uuid = UUID.randomUUID();
        String Email2 = uuid.toString()+"@gmail.com";
        JSONObject result2 = User.Register(PhoneNumber,Email2,Password,FirstName,LastName);
        System.out.println("-->Register Result: " + result2.toString());
        Assert.assertNotEquals("1" , result2.getString("code"));
    }

    @Test
    public void Repeated_Email() throws Exception
    {
        long Number2 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        String PhoneNumber2 = Number2 + "";
        JSONObject result1 = User.Register(PhoneNumber,Email,Password,FirstName,LastName);
        System.out.println("-->Register Result: " + result1.toString());
        Assert.assertEquals("1" , result1.getString("code"));
        JSONObject result2 = User.Register(PhoneNumber2,Email,Password,FirstName,LastName);
        System.out.println("-->Register Result: " + result2.toString());
        Assert.assertNotEquals("1" , result2.getString("code"));
    }

    @Test
    public void Phone_Number_Error () throws Exception
    {
        ArrayList<String> Number_list = new ArrayList<String>();
        Number_list.add(PhoneNumber+"00000000"); //over length
        Number_list.add(PhoneNumber.substring(5));//under length
        Number_list.add("abcdefghij"); //letters only
        Number_list.add(PhoneNumber +"  "); //space at the end
        Number_list.add("  " + PhoneNumber ); // space at the beginning
        Number_list.add(PhoneNumber.substring(5)+"abcdf"); // half numbers, half letters
        Number_list.add(PhoneNumber.substring(0,4)+" "+PhoneNumber.substring(6,9));  // space in middle
        Number_list.add("%^$#@&*_+!"); //characters only
        for (int index = 0; index < Number_list.size(); index ++)
        {
            uuid = UUID.randomUUID();
            String email = uuid.toString()+"@hotmail.com";
            JSONObject result = User.Register(Number_list.get(index),email,Password, FirstName,LastName);
            System.out.println("-->Register Result: " + result.toString());
            Assert.assertNotEquals("1" , result.getString("code"));
        }
    }

    @Test
    public void Email_Error() throws Exception
    {
        ArrayList<String> Email_list = new ArrayList<String>();
        Email_list.add(Email+"@hotmail.com"); //wrong format: sth@sth.com@hotmail.com
        Email_list.add(uuid.toString()); // wrong format: without @sth.com
        Email_list.add("123456789");
        Email_list.add(uuid.toString()+".com");
        for(int index = 0; index < Email_list.size(); index ++)
        {
            long number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
            String phone_number = number + "";
            JSONObject result = User.Register(phone_number,Email_list.get(index), Password, FirstName,LastName);
            System.out.println("-->Register Result: " + result.toString());
            Assert.assertNotEquals("1" , result.getString("code"));
        }
    }


}
