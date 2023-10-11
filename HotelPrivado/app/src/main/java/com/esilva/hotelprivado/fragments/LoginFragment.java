package com.esilva.hotelprivado.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.db.AdminBaseDatos;
import com.esilva.hotelprivado.db.DataUsuario;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText ed_usua;
    private EditText ed_co1;
    private Button Ingreso;

    private String ed1;
    private String ed2;

    InterfeceLogin interfeceLogin;


    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        ed_usua = (EditText) root.findViewById(R.id.edUsuario);
        ed_co1 = (EditText) root.findViewById(R.id.edContrase);


        Ingreso = (Button) root.findViewById(R.id.btIngresoUsuario);
        Ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceLogin.resultLogin(validaUsuario());
            }
        });

        return root;
    }

    private int validaUsuario(){
        int ret = validaData();
        if(ret != 0)
            return ret;

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(getContext());
        DataUsuario dataUsuario = adminBaseDatos.getUsurio();
        if(dataUsuario == null)
            return -2;
        if(!dataUsuario.getUsUsuario().equals(ed1) || !dataUsuario.getUsContrasena().equals(ed2))
            return -3;

        return 0;
    }

    private int validaData(){
        ed1 = ed_usua.getText().toString().trim();
        ed2 = ed_co1.getText().toString().trim();
        if(ed1.isEmpty() || ed2.isEmpty()){
            return -1;
        }
        return 0;
    }

    public interface InterfeceLogin{
        public void resultLogin(int result);
    }

    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof InterfeceLogin){
            interfeceLogin = (InterfeceLogin) context;
        }else{
            throw new RuntimeException(context.toString()+"listener fragment");
        }

    }
}