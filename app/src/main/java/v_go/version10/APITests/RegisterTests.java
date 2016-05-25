package v_go.version10.APITests;
import org.junit.*;

import java.util.Random;
import java.util.UUID;
import v_go.version10.ApiClasses.User;

public class RegisterTests {

    // Cindy:
    // I commented out this test suite since the User class has been changed,
    // you need to recreate you tests in \app\src\test\java folder

    /*
    private User TestUser;
    String correctPassword1 = "aA123456";
    String correctLastName1 = "Ling";
    String correctFirstName1 = "Cindy";

    UUID uuid;
    String firstEmail, PhoneNumber;
    long Number;
    Random rand = new Random();

    @Before
    public void setUp() throws Exception {
        TestUser = new User();
        uuid = UUID.randomUUID();
        firstEmail = uuid.toString()+"@gmail.com";
        Number = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
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
    public void EmptyEmail(){
        String  result = TestUser.Register(" ",correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2", result);
    }

    @Test
    public void RepeatedEmail(){
        uuid = UUID.randomUUID();
        String secondEmail = uuid.toString()+"@gmail.com";
        String firstRegister = TestUser.Register(firstEmail, correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("1", firstRegister);
        String secondRegister = TestUser.Register(secondEmail, correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2",secondRegister);
    }

    @Test
    public void InvalidEmail(){
        String InvalidEmail = firstEmail + "@gmail.com52825";
        String  result = TestUser.Register(InvalidEmail,correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-5", result);
    }

    @Test
    public void RepeatedPhoneNumber (){
        String firstRegister = TestUser.Register(firstEmail, correctPassword1,PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("1", firstRegister);
        long Number2 = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
        String PhoneNumber2 = Number2 + "";
        String secondRegister = TestUser.Register(firstEmail, correctPassword1,PhoneNumber2,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2",secondRegister);
    }

    @Test
    public void PhoneNumberTooLong () {
        long  LargeNumber = Number *100000000 ;
        String LongNumber = LargeNumber +"";
        String  result = TestUser.Register(firstEmail,correctPassword1,LongNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-60", result);
    }

    @Test
    public void PhoneNumberTooShort () {
        long  SmallNumber = Number / 1000 ;
        String ShortNumber = SmallNumber +"";
        String  result = TestUser.Register(firstEmail,correctPassword1,ShortNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-60", result);
    }

    @Test
    public void PhoneNumberWithLetter(){
        String  result = TestUser.Register(firstEmail,correctPassword1,"onlyLetter",correctLastName1,correctFirstName1);
        Assert.assertEquals("-61", result);
    }

    @Test
    public void EmptyPhoneNumber(){
        String  result = TestUser.Register(firstEmail,correctPassword1,"          ",correctLastName1,correctFirstName1);
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyFirstName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber,correctLastName1," ");
        Assert.assertEquals("-2",result);
    }

    @Test
    public void TooLongFirstName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber,correctLastName1,"aaaaaaaaaaaaaaaaaaaaaaaaa");
        Assert.assertEquals("-300",result);
    }

    @Test
    public void EmptyLastName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber," ",correctFirstName1);
        Assert.assertEquals("1",result);
    }

    @Test
    public void TooLongLastName () {
        String  result = TestUser.Register(firstEmail,correctPassword1,PhoneNumber,"bbbbbbbbbbbbbbbbbbbbbb",correctFirstName1);
        Assert.assertEquals("-311",result);
    }

    @Test
    public void EmptyPassword(){
        String  result = TestUser.Register(firstEmail," ",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-2", result);
    }

    @Test
    public void PasswordLessThan8Digits(){
        String  result = TestUser.Register(firstEmail,"aA101",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-40", result);
    }

    @Test
    public void PasswordMoreThan8Digits(){
        String  result = TestUser.Register(firstEmail,"aA101000000000000",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-40", result);
    }

    @Test
    public void PasswordWithoutUpperCase(){
        String  result = TestUser.Register(firstEmail,"a101000000",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-44", result);
    }

    @Test
    public void PasswordWithoutLowerCase(){
        String  result = TestUser.Register(firstEmail,"A1010000000",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-43", result);
    }

    @Test
    public void PasswordWithoutNumber(){
        String  result = TestUser.Register(firstEmail,"LettersOnly",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-42", result);
    }

    @Test
    public void PasswordWithWhiteSpaces(){
        String  result = TestUser.Register(firstEmail," aA123456 789 ",PhoneNumber,correctLastName1,correctFirstName1);
        Assert.assertEquals("-1", result);
    }

*/



}