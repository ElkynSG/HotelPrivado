package com.esilva.hotelprivado.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.esilva.hotelprivado.R;

import com.esilva.hotelprivado.interfaces.iComunicaFragment;


public class TypeItemsFragment extends Fragment {

    Activity activity;
    iComunicaFragment iComunicaFragment;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button bt_alcohol;
    private Button bt_sin_alcohol;
    private Button bt_snacks;
    private Button bt_souvenirs;

    InterfeceTypeProducto interfeceTypeProducto;


    public TypeItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TypeItemsFragment.
     */

    public static TypeItemsFragment newInstance(String param1, String param2) {
        TypeItemsFragment fragment = new TypeItemsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_type_items, container, false);

        iniEvent(root);

        return root;
    }

    private void iniEvent(View view){

        bt_alcohol = view.findViewById(R.id.bt_alcohol);
        bt_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceTypeProducto.typeProduct(1);
            }
        });

        bt_sin_alcohol = view.findViewById(R.id.bt_sin_alcohol);
        bt_sin_alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceTypeProducto.typeProduct(2);
            }
        });

        bt_snacks = view.findViewById(R.id.bt_snacks);
        bt_snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceTypeProducto.typeProduct(3);
            }
        });

        bt_souvenirs = view.findViewById(R.id.bt_souvenirs);
        bt_souvenirs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceTypeProducto.typeProduct(4);
            }
        });

    }

    public interface InterfeceTypeProducto{
        public void typeProduct(int typo);
    }


    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof InterfeceTypeProducto){
            interfeceTypeProducto = (InterfeceTypeProducto) context;
        }else{
            throw new RuntimeException(context.toString()+"listener fragment");
        }

    }



}