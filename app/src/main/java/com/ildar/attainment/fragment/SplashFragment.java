package com.ildar.attainment.fragment;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ildar.attainment.R;

import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {


    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SplashTask splashTask = new SplashTask();
        splashTask.execute();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    class SplashTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TimeUnit.SECONDS.sleep(2);  //усыпляем наш поток на 2 секунды
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (getActivity()!=null)
                getActivity().getFragmentManager().popBackStack();
            return null;
        }
    }
}
