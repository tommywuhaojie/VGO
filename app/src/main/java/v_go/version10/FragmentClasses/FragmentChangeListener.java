package v_go.version10.FragmentClasses;

import android.support.v4.app.Fragment;

public interface FragmentChangeListener
{
    void replaceFragment(Fragment fragment);
    void goBackToFragment(Fragment fragment);
}