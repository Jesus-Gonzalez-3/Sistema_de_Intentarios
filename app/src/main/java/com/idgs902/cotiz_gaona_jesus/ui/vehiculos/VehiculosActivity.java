package com.idgs902.cotiz_gaona_jesus.ui.vehiculos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

public class VehiculosActivity extends AppCompatActivity {

    private Button btnAgregarV;
    private TextInputEditText etClaveV, etNombreV, etMarca, etModelo, etCosto;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);
        etClaveV = this.findViewById(R.id.etClaveV);
        etNombreV = this.findViewById(R.id.etNombreV);
        etMarca = this.findViewById(R.id.etMarca);
        etModelo = this.findViewById(R.id.etModelo);
        etCosto = this.findViewById(R.id.etCosto);
        btnAgregarV = this.findViewById(R.id.btnGuardarV);

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarV.setOnClickListener(new View.OnClickListener() {
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
        if (etClaveV.getText().toString().trim().length() != 0
                && etNombreV.getText().toString().trim().length() != 0
                && etMarca.getText().toString().trim().length() != 0
                && etModelo.getText().toString().trim().length() != 0
                && etCosto.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO vehiculo VALUES('" + etClaveV.getText() + "','"
                        + etNombreV.getText() + "','" + etMarca.getText() +
                        "','" + etModelo.getText() + "','" +
                        etCosto.getText() + "');");
                showMessage("Exito!", "Vehiculo agregado");
                finish();
            } catch (Exception ex) {
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