package v_go.version10.HelperClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import v_go.version10.R;

public class MySimpleAdapter extends SimpleAdapter {

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

    // for fun
    int imgIndex[];

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

            imgIndex = new int[95];
            for(int i=0; i<95; i++){
                    imgIndex[i] = Global.getRandomInt(0, 94);
            }

    }

    // this function gets called once each row is revealed
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundColor(Color.parseColor("#6495ED"));
        } else {
            view.setBackgroundColor(Color.parseColor("#4884ea"));
        }

        TextView secondLine = (TextView)view.findViewById(R.id.secondLine);
        if(secondLine != null) {
            secondLine.setSelected(true);
            secondLine.requestFocus();
        }

        // set up user avatar
        int[] img = {R.drawable.fun0,
                R.drawable.fun1,
                R.drawable.fun2,
                R.drawable.fun3,
                R.drawable.fun4,
                R.drawable.fun5,
                R.drawable.fun6,
                R.drawable.fun7,
                R.drawable.fun8,
                R.drawable.fun9,
                R.drawable.fun10,
                R.drawable.fun11,
                R.drawable.fun12,
                R.drawable.fun13,
                R.drawable.fun14,
                R.drawable.fun15,
                R.drawable.fun16,
                R.drawable.fun17,
                R.drawable.fun18,
                R.drawable.fun19,
                R.drawable.fun20,
                R.drawable.fun21,
                R.drawable.fun22,
                R.drawable.fun23,
                R.drawable.fun24,
                R.drawable.fun25,
                R.drawable.fun26,
                R.drawable.fun27,
                R.drawable.fun28,
                R.drawable.fun29,
                R.drawable.fun30,
                R.drawable.fun31,
                R.drawable.fun32,
                R.drawable.fun33,
                R.drawable.fun34,
                R.drawable.fun35,
                R.drawable.fun36,
                R.drawable.fun37,
                R.drawable.fun38,
                R.drawable.fun39,
                R.drawable.fun40,
                R.drawable.fun41,
                R.drawable.fun42,
                R.drawable.fun43,
                R.drawable.fun44,
                R.drawable.fun45,
                R.drawable.fun46,
                R.drawable.fun47,
                R.drawable.fun48,
                R.drawable.fun49,
                R.drawable.fun50,
                R.drawable.fun51,
                R.drawable.fun52,
                R.drawable.fun53,
                R.drawable.fun54,
                R.drawable.fun55,
                R.drawable.fun56,
                R.drawable.fun57,
                R.drawable.fun58,
                R.drawable.fun59,
                R.drawable.fun60,
                R.drawable.fun61,
                R.drawable.fun62,
                R.drawable.fun63,
                R.drawable.fun64,
                R.drawable.fun65,
                R.drawable.fun66,
                R.drawable.fun67,
                R.drawable.fun68,
                R.drawable.fun69,
                R.drawable.fun70,
                R.drawable.fun71,
                R.drawable.fun72,
                R.drawable.fun73,
                R.drawable.fun74,
                R.drawable.fun75,
                R.drawable.fun76,
                R.drawable.fun77,
                R.drawable.fun78,
                R.drawable.fun79,
                R.drawable.fun80,
                R.drawable.fun81,
                R.drawable.fun82,
                R.drawable.fun83,
                R.drawable.fun84,
                R.drawable.fun85,
                R.drawable.fun86,
                R.drawable.fun87,
                R.drawable.fun88,
                R.drawable.fun89,
                R.drawable.fun90,
                R.drawable.fun91,
                R.drawable.fun92,
                R.drawable.fun93,
                R.drawable.fun94
        };

        ImageView imageView = ((ImageView) view.findViewById(R.id.icon));
        if(imageView == null){
            return view;
        }else{
            // nextInt is normally exclusive of the top value,so add 1 to make it inclusive
            imageView.setImageResource(img[imgIndex[position]]);
            //imageView.setImageResource(img[0]);
        }

        return view;
    }

        @Override
        public void notifyDataSetChanged() {
                for(int i=0; i<95; i++){
                        imgIndex[i] = Global.getRandomInt(0, 94);
                }
                super.notifyDataSetChanged();
        }
}
