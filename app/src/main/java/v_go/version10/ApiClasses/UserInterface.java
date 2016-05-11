package v_go.version10.ApiClasses;

/**
 * Created by michael on 16-05-09.
 */
public interface UserInterface {
    /*
        * v-go url addrress
        * */
    String Rootpath();

    /*
        input:
        first name  :(string)
        last name   :(string)
        email       :(string)
        password    :(string)
        number      :(int)
        * return value:
        * -1    : fail to register;
        * -2    :invalid inputs/ did not fill in all fields;
        * -300  :first name too long max 15
        * -301  :only letter and aspace allowed in first name
        * -311  :last name too long max 15
        * -40   :password length is not 8
        * -41   :symbol is not allowed in password
        * -42   :password must include one number
        * -43   :password must include one lower case letter
        * -44   :password must include one upper case letter
        * -5    : wrong email format
        * -60   :phone number leng is not 10
        * -61   :phone number not only contain number
        * 0     :failed(no specify reason)
        * 1     :successfully register;
        * */
    String Register(String email, String password, String phone, String lastname, String firstname);

    /*
        * return value:
        * -1    : fail to login
        *1      : sucessfully login as passanger
        * */
    String Login(String email, String password);
}
