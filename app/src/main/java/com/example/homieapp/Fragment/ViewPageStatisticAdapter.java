package com.example.homieapp.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageStatisticAdapter extends FragmentStateAdapter {

    public ViewPageStatisticAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){

            case 1:
                return new BillFragment();

            case 2:
                return new HotProductFragment();
            case 0:

            default:
                return new SalesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
