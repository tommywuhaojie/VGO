package v_go.version10.HelperClasses;

import android.graphics.Bitmap;

public class UserCache {

    private String first_name;
    private String last_name;
    private Bitmap avatar;

    public UserCache(){

    }

    public void setAvatar(Bitmap bitmap){
        avatar = bitmap;
    }

    public Bitmap getAvatar(){
        return avatar;
    }
}
