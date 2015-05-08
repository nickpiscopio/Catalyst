package com.catalyst.catalyst;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nickpiscopio on 5/8/15.
 */
public class DemoSliderFragment extends Fragment
{
    public static String DEMO_PAGE = "demo_page";

    private static final int DESCRIPTION_PAGE = 0;
    private static final int INSPIRATION_PAGE = 1;
    private static final int SHARE_PAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int page = getArguments().getInt(DEMO_PAGE);
        int pageId;
        switch (page)
        {
            case INSPIRATION_PAGE:
                pageId = R.layout.fragment_demo_2;
                break;

            case SHARE_PAGE:
                pageId = R.layout.fragment_demo_3;
                break;

            case DESCRIPTION_PAGE:
            default:
                pageId = R.layout.fragment_demo_1;
                break;
        }

        return inflater.inflate(pageId, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, R.menu.menu_main, 0,
                 getResources().getString(R.string.app_name))
            .setIcon(R.drawable.abc_ab_share_pack_mtrl_alpha)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
