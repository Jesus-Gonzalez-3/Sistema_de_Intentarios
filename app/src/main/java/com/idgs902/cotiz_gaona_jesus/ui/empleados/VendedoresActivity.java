package com.idgs902.cotiz_gaona_jesus.ui.empleados;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

public class VendedoresActivity extends AppCompatActivity {
    private Button btnAgregarE;
    private TextInputEditText etClaveE, etNombreE, txtCalle, txtTelefono, txtComisiones, etColonia, etEmail;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedores);

        etClaveE = this.findViewById(R.id.etClaveE);
        etNombreE = this.findViewById(R.id.etNombreE);
        txtCalle = this.findViewById(R.id.txtCalle);
        txtTelefono = this.findViewById(R.id.txtTelefono);
        txtComisiones = this.findViewById(R.id.txtComisiones);
        etColonia = this.findViewById(R.id.etColonia);
        etEmail = this.findViewById(R.id.etEmail);
        btnAgregarE = this.findViewById(R.id.btnGuardarE);


        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    agregar();
                    finish();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void agregar() {
        if (etNombreE.getText().toString().trim().length() != 0
                && txtCalle.getText().toString().trim().length() != 0
                && txtTelefono.getText().toString().trim().length()  != 0
                && etColonia.getText().toString().trim().length() != 0
                && etEmail.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO vendedor(nombre,calle,colonia,telefono,email,comisiones ) VALUES('" + etNombreE.getText() + "','"
                        + txtCalle.getText() + "','"+ etColonia.getText() + "','" + txtTelefono.getText() + "','" + etEmail.getText() + "','" +
                        txtComisiones.getText() + "');");
                showMessage("Exito!", "Vendedor agregado");

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