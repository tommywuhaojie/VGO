package v_go.version10.HelperClasses;

/*IGNORE THIS CLASS*/
/*IGNORE THIS CLASS*/
/*IGNORE THIS CLASS*/
/*IGNORE THIS CLASS*/
/*IGNORE THIS CLASS*/
/*IGNORE THIS CLASS*/

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import v_go.version10.R;


class ViewHolder{
    EditText info;
}
public class RegisterInfoAdapter extends ArrayAdapter<String> {

    final String[] infoAry;
    final int[] min_max;
    final int MIN = 0;
    final int MAX = 8;
    final int NUM_OF_INFO = 100;
    final int PASSWORD_POS = 1;
    final int PASSWORD_CONFIRM_POS = 2;


    public RegisterInfoAdapter(Context context, String[] infoDescription, ListView listView) {
        super(context, R.layout.register_info_row, infoDescription);
        infoAry = new String[NUM_OF_INFO];
        min_max = new int[2];
        min_max[0] = MIN;
        min_max[1] = MAX;

        for (int i = 0; i < NUM_OF_INFO; i++) {
            infoAry[i] = "";
        }

        listView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                EditText editText = (EditText) view.findViewById(R.id.info);
                Log.d("DEBUG", "VIEW MOVE TO SCRAP INFO: " + editText.getText());
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (position < min_max[0]) { //if position < min, range decrement by 1
            min_max[0]--;
            min_max[1]--;
        } else if (position > min_max[1]) { //if position > max, range increments by 1
            min_max[0]++;
            min_max[1]++;
        }

        holder = new ViewHolder();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.register_info_row, parent, false);

        String description = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.infoDescription);
        textView.setText(description);

        holder.info = (EditText) convertView.findViewById(R.id.info);
        convertView.setTag(holder);

        //if is password field set password type
        if (position == PASSWORD_POS || position == PASSWORD_CONFIRM_POS) {
            holder.info.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        holder.info.setText(infoAry[position]); //set old text back to field
        holder.info.addTextChangedListener(new TextWatcher() { //when text that is visible got changed

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //setting data to array, when changed
                if (position >= min_max[0] && position <= min_max[1]) {
                    infoAry[position] = s.toString();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return convertView;
    }
}