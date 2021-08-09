package com.idgs902.cotiz_gaona_jesus.ui.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteActivity extends AppCompatActivity {
    private Button btnAgregarC;
    private TextInputEditText etNombreC, etCalleC, etColoniaC, etCiudadC, etRfcC, etTelefonoC, etEmailC;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        etNombreC = this.findViewById(R.id.etNombreC);
        etCalleC = this.findViewById(R.id.etCalleC);
        etColoniaC = this.findViewById(R.id.etColoniaC);
        etCiudadC = this.findViewById(R.id.etCiudadC);
        etRfcC = this.findViewById(R.id.etRfcC);
        etTelefonoC = this.findViewById(R.id.etTelefonoC);
        etEmailC = this.findViewById(R.id.etEmailC);

        btnAgregarC = this.findViewById(R.id.btnGuardarC);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    agregar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void agregar() {
        String saldo = "0";
        if (etNombreC.getText().toString().trim().length() != 0
                && etCalleC.getText().toString().trim().length() != 0
                && etColoniaC.getText().toString().trim().length() != 0
                && etCiudadC.getText().toString().trim().length() != 0
                && etRfcC.getText().toString().trim().length() != 0
                && etTelefonoC.getText().toString().trim().length() != 0
                && etEmailC.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO cliente(nombre,calle,colonia,ciudad, rfc, telefono,email, saldo) VALUES(" +
                        "'" + etNombreC.getText() + "'," +
                        "'" + etCalleC.getText() + "'," +
                        "'"+ etColoniaC.getText() + "'," +
                        "'" + etCiudadC.getText() + "'," +
                        "'" + etRfcC.getText() + "'," +
                        "'" + etTelefonoC.getText() + "'," +
                        "'" + etEmailC.getText() + "'," +
                        "'" + saldo + "');");
                showMessage("Exito!", "Cliente agregado");
                finish();
            }catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        }
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}