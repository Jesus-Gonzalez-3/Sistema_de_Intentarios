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
    private Button btnAgregar;
    private TextInputEditText etClaveC, etNombreC, etDomicilio, etFechaRegistro;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        etClaveC = this.findViewById(R.id.etClaveC);
        etNombreC = this.findViewById(R.id.etNombreC);
        etDomicilio = this.findViewById(R.id.etDomicilio);
        etFechaRegistro = this.findViewById(R.id.etFechaRegistro);

        btnAgregar = this.findViewById(R.id.btnGuardarC);
        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        etFechaRegistro.setText(fecha.format(date).toString());
        etFechaRegistro.setEnabled(false);

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregar.setOnClickListener(new View.OnClickListener() {
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
        if (etClaveC.getText().toString().trim().length() != 0
                && etNombreC.getText().toString().trim().length() != 0
                && etDomicilio.getText().toString().trim().length() != 0
                && etFechaRegistro.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO cliente VALUES('" + etClaveC.getText() + "','"
                        + etNombreC.getText() + "','" + etDomicilio.getText() + "','" +
                        etFechaRegistro.getText() + "');");
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