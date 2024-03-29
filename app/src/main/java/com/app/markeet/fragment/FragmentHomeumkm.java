package com.app.markeet.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.markeet.ActivityMain;
import com.app.markeet.LoginActivity;
import com.app.markeet.R;
import com.app.markeet.connection.API;
import com.app.markeet.connection.RestAdapter;
import com.app.markeet.connection.callbacks.CallbackSaldo;
import com.app.markeet.data.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHomeumkm extends Fragment {

    private SharedPref sharedPref;
    private Button btnlogout;
    private TextView Saldo;

    public FragmentHomeumkm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_homeumkm, null);
        sharedPref = new SharedPref(this.getActivity());
        btnlogout = (Button) fragmentView.findViewById(R.id.btnLogout);
        Saldo = (TextView) fragmentView.findViewById(R.id.saldoku);
        getSaldo();
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmRegist();
            }
        });
        return  fragmentView;
    }
    public void dialogConfirmRegist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getString(R.string.logout_info));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogoutProcess();
            }
        });
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }
    private void getSaldo(){
        String iduser = sharedPref.getSPIdUser().toString();
        API api = RestAdapter.createAPI();
        api.checkSaldo(iduser).enqueue(new Callback<CallbackSaldo>() {
            @Override
            public void onResponse(Call<CallbackSaldo> call, Response<CallbackSaldo> response) {
                CallbackSaldo responseBody = response.body();
                if(responseBody!=null){
                    String akun = responseBody.getName().toString();
                    if(akun.equals("UnauthorizedError")){
                        Toast.makeText(getActivity(), "Account will relogin", Toast.LENGTH_SHORT).show();
                        sharedPref.saveBoolean(sharedPref.SP_SUDAH_LOGIN, false);
                        startActivity(new Intent(getActivity(), LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                    }else{
                        String jml_uang = responseBody.getJumlah_uang().toString();
                        Toast.makeText(getActivity(), "Ada saldo"+jml_uang, Toast.LENGTH_SHORT).show();
                        Saldo.setText("IDR : "+jml_uang);
                        sharedPref.saveString(sharedPref.BALANCE_USER,jml_uang);
                    }
                }else{
                    //Snackbar.make(parent_view, "EzPay Balance Not Loaded", Snackbar.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CallbackSaldo> call, Throwable t) {
                //Snackbar.make(parent_view, "EzPay Balance Not Loaded", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    private void LogoutProcess(){
        final String idu =sharedPref.getSPIdUser().toString();
        API api = RestAdapter.createAPI();
        api.logoutRequest(idu).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if(responseBody!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        String status = jsonObject.getString("success");
                        if(status.equals("Berhasil Log Out")){
                            sharedPref.saveBoolean(sharedPref.SP_SUDAH_LOGIN, false);
                            startActivity(new Intent(getActivity(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to Logout", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
