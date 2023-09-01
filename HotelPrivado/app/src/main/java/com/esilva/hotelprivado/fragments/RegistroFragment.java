package com.esilva.hotelprivado.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esilva.hotelprivado.LoginActivity;
import com.esilva.hotelprivado.R;
import com.esilva.hotelprivado.db.AdminBaseDatos;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText ed_usua;
    private EditText ed_co1;
    private EditText ed_co2;
    private Button Registro;

    private String ed1;
    private String ed2;
    private String ed3;

    InterfeceRegistro interfeceRegistro;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistroFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistroFragment newInstance(String param1, String param2) {
        RegistroFragment fragment = new RegistroFragment();
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
        View root = inflater.inflate(R.layout.fragment_registro, container, false);

        ed_usua = (EditText) root.findViewById(R.id.edUsuarioRe);
        ed_co1 = (EditText) root.findViewById(R.id.edContrase1);
        ed_co2 = (EditText) root.findViewById(R.id.edContrase2);

        Registro = (Button) root.findViewById(R.id.btGuardarUsuario);
        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interfeceRegistro.resRegistro(guardaUsuario());
            }
        });


        return root;
    }

    private int guardaUsuario(){
        int ret = validaData();
        if(ret != 0)
            return ret;

        AdminBaseDatos adminBaseDatos = new AdminBaseDatos(getContext());
        adminBaseDatos.insertUsuario(ed1,ed2);
        return 0;
    }

    private int validaData(){
        ed1 = ed_usua.getText().toString().trim();
        ed2 = ed_co1.getText().toString().trim();
        ed3 = ed_co2.getText().toString().trim();
        if(ed1.isEmpty() || ed2.isEmpty() || ed3.isEmpty()){
            return -1;
        }
        if(!ed2.equals(ed3)){
            return -2;
        }
        return 0;
    }

    public interface InterfeceRegistro{
        public void resRegistro(int result);
    }

    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof InterfeceRegistro){
            interfeceRegistro = (InterfeceRegistro) context;
        }else{
            throw new RuntimeException(context.toString()+"listener fragment");
        }

    }
}