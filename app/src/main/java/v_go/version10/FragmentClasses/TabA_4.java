package v_go.version10.FragmentClasses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import v_go.version10.ActivityClasses.Main;
import v_go.version10.R;

public class TabA_4 extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_a_4, container, false);

        // change toolbar title
        getActivity().setTitle("Request a trip");
        ((Main)getActivity()).enableBackButton(true);
        setHasOptionsMenu(true);

        TextView addr_a = (TextView)view.findViewById(R.id.addr_a);
        TextView addr_b = (TextView)view.findViewById(R.id.addr_b);
        TextView time = (TextView)view.findViewById(R.id.time);
        TextView a_diff = (TextView)view.findViewById(R.id.a_diff);
        TextView b_diff = (TextView)view.findViewById(R.id.b_diff);
        TextView time_diff = (TextView)view.findViewById(R.id.time_diff);

        addr_a.setText(getArguments().getString("start_location"));
        addr_b.setText(getArguments().getString("end_location"));
        time.setText(getArguments().getString("time"));
        a_diff.setText(getArguments().getString("a_diff"));
        b_diff.setText(getArguments().getString("b_diff"));
        time_diff.setText(getArguments().getString("time_diff"));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home) {
            ((Main)getActivity()).popFragments();
        }
        return super.onOptionsItemSelected(item);
    }
}
