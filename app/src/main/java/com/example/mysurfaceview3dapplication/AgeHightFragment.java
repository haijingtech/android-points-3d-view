package com.example.mysurfaceview3dapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgeHightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgeHightFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AgeHightFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgeHightFragment newInstance(String param1, String param2) {
        AgeHightFragment fragment = new AgeHightFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    AgeHeightView ageHeightView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sex, container, false);
        ageHeightView=view.findViewById(R.id.ahView);
        int sex=Integer.parseInt(mParam1);
        ageHeightView.setSex(sex);
        ArrayList<People> peoples=new ArrayList<>();
        ArrayList<AgeHeight> ageHeights=new ArrayList<>();
        for(int i=12;i<48;i++){
            AgeHeight ageHeight=new AgeHeight(i,70+i-12,"2022.10.30");
            ageHeights.add(ageHeight);
        }
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),sex==1?R.drawable.touxiang2:R.drawable.touxiang1);
        People people=new People(ageHeights,"2011.11.1",0xff77ab00,bitmap);
        peoples.add(people);

        ArrayList<AgeHeight> ageHeights1=new ArrayList<>();
        for(int i=120;i<168;i++){
            AgeHeight ageHeight=new AgeHeight(i,140+(i-120)*0.3F,"2022.12.30");
            ageHeights1.add(ageHeight);
        }
        Bitmap bitmap1= BitmapFactory.decodeResource(getResources(),sex==1?R.drawable.touxiang3:R.drawable.touxiang);
        People people1=new People(ageHeights1,"2013.11.1",0xff7Ab9fa,bitmap1);
        peoples.add(people1);

        ageHeightView.setPeoplesToView(peoples);

        return view;
    }
}