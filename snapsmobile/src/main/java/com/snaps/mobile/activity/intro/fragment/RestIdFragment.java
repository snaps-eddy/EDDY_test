package com.snaps.mobile.activity.intro.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;

public class RestIdFragment extends Fragment {

    ISnapsHamburgerMenuListener menuClickListenter = null;
    TextView btn_confirm;

    public static RestIdFragment newInstance(ISnapsHamburgerMenuListener listenter) {
        RestIdFragment fragment = new RestIdFragment();
        fragment.menuClickListenter = listenter;
        return fragment;
    }

    public RestIdFragment() {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loginp_rest_id, container, false);
        btn_confirm = (TextView)v.findViewById(R.id.rest_id_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuClickListenter != null) {
                    menuClickListenter.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_COMPLATE_REST_ID);
                }
            }
        });
        return v;
    }

}
