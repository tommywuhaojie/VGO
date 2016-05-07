package v_go.version10.APITests;
import org.junit.*;
import java.util.UUID;
import v_go.version10.ApiClasses.User;

public class RegisterTests {

    private User TestUser;
    UUID uuid = UUID.randomUUID();
    String randomUUIDString = uuid.toString();

    @Before
    public void setUp() throws Exception {
        TestUser = new User();
    }


    @Test
    public void RepeatedPhoneNumber (){
        String firstRegister = TestUser.Register(uuid.toString(), "aA123456","7788921222","LING","CINDY");
        String secondRegister = TestUser.Register("cindyling@gmail.com", "Bb123456","7788921222","LING","CINDY");
        Assert.assertEquals("-1",secondRegister);
    }

    @Test
    public void PhoneNumberTooLong () {
        String result = TestUser.Register("cindyling000@gmail.com", "aA123456","778892122222222222222222222222222222","LING","CINDY");
        Assert.assertEquals("-1", result);
    }

    @Test
    public void PhoneNumberWithLetter(){
        String result = TestUser.Register("cindyling000@gmail.com", "aA123456","letter55","LING","CINDY");
        Assert.assertEquals("-1", result);
    }

    @Test
    public void EmptyFirstName () {
        String result = TestUser.Register("cindyling000@gmail.com", "aA123456","7788921222","Ling"," ");
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyLastName () {
        String result = TestUser.Register("cindyling000@gmail.com", "aA123456","7788921222"," ","CINDY");
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyNames () {
        String result = TestUser.Register("cindyling000@gmail.com", "aA123456","7788921222"," "," ");
        Assert.assertEquals("-2",result);
    }

    @Test
    public void EmptyEmail(){
        String result = TestUser.Register(" ", "aA123456","7788921222","LING","CINDY");
        Assert.assertEquals("-2", result);
    }

    @Test
    public void EmptyPassword(){
        String result = TestUser.Register("cindyling000@gmail.com", " ","7788921222","LING","CINDY");
        Assert.assertEquals("-2", result);
    }

    @Test
    public void InvalidPassword(){
        String result = TestUser.Register("cindyling000@gmail.com", "--  123456","7788921222","LING","CINDY");
        Assert.assertEquals("-1", result);
    }

    @Test
    public void RegisterWithInvalidEmail(){
        String result = TestUser.Register("123456", "aA123456","7788921222","LING","CINDY");
        Assert.assertEquals("-2", result);
    }

    @Test
    public void RepeatedEmail(){
        String firstRegister = TestUser.Register("cindyling000@gmail.com", "aA123456","7788921222","LING","CINDY");
        String secondRegister = TestUser.Register("cindyling000@gmail.com", "Bb123456","7788921222","LING","CINDY");
        Assert.assertEquals("-3",secondRegister);
    }

}