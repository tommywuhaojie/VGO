package v_go.version10.HelperClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import v_go.version10.ApiClasses.UserApi;
import v_go.version10.R;

public class ContactListAdapter extends SimpleAdapter {

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */

    private List<Integer> badgeList;
    private List<String> userIdList;
    private Context context;
    private Context appContext;
    private Bitmap avatarBitmap;

    public ContactListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, List<String> userIdList, List<Integer> badgeList, Context appContext) {
        super(context, data, resource, from, to);
        this.context = context;
        this.appContext = appContext;
        this.userIdList = userIdList;
        this.badgeList = badgeList;
    }

    // this function gets called once each row is revealed
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        avatarBitmap = UserApi.loadAvatarFromStorage(userIdList.get(position), appContext);
        if(avatarBitmap != null){
            ImageView imageView = ((ImageView) view.findViewById(R.id.icon));
            if(imageView != null){
                imageView.setImageBitmap(avatarBitmap);
                // update badge
                if(badgeList.get(position) != 0) {
                    BadgeView badge = new BadgeView(context, imageView);
                    badge.setText(badgeList.get(position).toString());
                    badge.show();
                }
            }
        }

        return view;
    }

    public void recycleBitmaps(){
        if(avatarBitmap != null){
           if(!avatarBitmap.isRecycled()){
               avatarBitmap.recycle();
           }
        }
    }
}