package v_go.version10.APITests;
import org.junit.*;

import java.util.Random;
import java.util.UUID;
import v_go.version10.ApiClasses.User;

public class RegisterTests {

    private User TestUser;
    String correctPassword1 = "aA123456";
    String correctLastName1 = "Ling1";
    String correctFirstName1 = "Cindy1";

    String correctPassword2 = "bB123456";
    String correctLastName2 = "Ling2";
    String correctFirstName2 = "Cindy2";

    UUID uuid;
    String firstEmail, PhoneNumber;
    Random rand = new Random();

    @Before
    public void setUp() throws Exception {
        TestUser = new User();
        uuid = UUID.randomUUID();
        firstEmail = uuid.toString()+"@gmail.com";
        long Number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        PhoneNumber = Number + "";
    }

    @Test
    public void correctRegister(){
        String  RegisterResult = TestUser.Register(firstEmail,correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("1",RegisterResult);
        String LoginResult = TestUser.Login(firstEmail,correctPassword1);
        Assert.assertEquals("1",LoginResult);
    }

    @Test
    public void RepeatedPhoneNumber (){
        String firstRegister = TestUser.Register(firstEmail, correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("1", firstRegister);
        long Number2 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        String PhoneNumber2 = Number2 + "";
        String secondRegister = TestUser.Register(firstEmail, correctPassword2,PhoneNumber2,correctLastName2,correctFirstName2);
        Assert.assertEquals("-3",secondRegister);
    }

    @Test
    public void RepeatedEmail(){
        uuid = UUID.randomUUID();
        String secondEmail = uuid.toString()+"@gmail.com";
        String firstRegister = TestUser.Register(firstEmail, correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("1", firstRegister);
        String secondRegister = TestUser.Register(secondEmail, correctPassword2,PhoneNumber,correctLastName2,correctFirstName2);
        Assert.assertEquals("-3",secondRegister);
    }

    @Test
    public void PhoneNumberTooLong () {
        String  result = TestUser.Register(firstEmail,correctPassword1,"7781234567000000000000000",correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void PhoneNumberWithLetter(){
        uuid = UUID.randomUUID();
        String phone = uuid.toString();
        String  result = TestUser.Register(firstEmail,correctPassword1,"lit",correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void EmptyPhoneNumber(){
        String  result = TestUser.Register(firstEmail,correctPassword1," ",correctLastName1,correctFirstName1);
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyFirstName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber,correctLastName1," ");
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyLastName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber," ",correctFirstName1);
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyEmail(){
        String  result = TestUser.Register(" ",correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2", result);
    }

    @Test
    public void EmptyPassword(){
        String  result = TestUser.Register(firstEmail," ",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2", result);
    }

    @Test
    public void PasswordLessThan8Digits(){
        String  result = TestUser.Register(firstEmail,"aA101",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void PasswordWithoutUpperCase(){
        String  result = TestUser.Register(firstEmail,"a101000000",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void PasswordWithoutLowerCase(){
        String  result = TestUser.Register(firstEmail,"A1010000000",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void PasswordWithWhiteSpaces(){
        String  result = TestUser.Register(firstEmail," aA123456 7 ",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

    @Test
    public void InvalidEmail(){
        String InvalidEmail = firstEmail + "@gmail.com52825";
        String  result = TestUser.Register(InvalidEmail,correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2", result);
    }


}