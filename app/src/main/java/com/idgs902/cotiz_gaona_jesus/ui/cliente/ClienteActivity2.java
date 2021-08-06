package com.idgs902.cotiz_gaona_jesus.ui.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

public class ClienteActivity2 extends AppCompatActivity {

    private Button btnModificar, btnEliminar;
    private TextInputEditText etClaveC, etNombreC, etDomicilio, etFechaRegistro;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente2);

        btnModificar = this.findViewById(R.id.btnModificarC);
        btnEliminar = this.findViewById(R.id.btnEliminarC);
        etClaveC = this.findViewById(R.id.etClaveCM);
        etNombreC = this.findViewById(R.id.etNombreCM);
        etDomicilio = this.findViewById(R.id.etDomicilioM);
        etFechaRegistro = this.findViewById(R.id.etFechaRegistroM);

        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(ClienteFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM cliente WHERE clave ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveC.setText(c.getString(0));
                    etNombreC.setText(c.getString(1));
                    etDomicilio.setText(c.getString(2));
                    etFechaRegistro.setText(c.getString(3));
                    etClaveC.setEnabled(false);
                    etFechaRegistro.setEnabled(false);
                }
            }else{
                showMessage("Info", "No existe un Registro con esa clave");
            }

        }catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    modificar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void modificar() {
        if (etNombreC.getText().toString().trim().length() != 0 && etDomicilio.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE cliente SET nombre ='"
                        + etNombreC.getText() + "', domicilio='" + etDomicilio.getText() + "' WHERE clave = '" + etClaveC.getText() + "';");
                showMessage("Exito!", "Cliente Modificado");
            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }

            finish();
        } else {
            showMessage("Info", "Debe Llenar todos los datos");
        }
    }

    public void eliminar() {
        try {
            db.execSQL("DELETE FROM cliente WHERE clave ='" +
                    etClaveC.getText() + "' AND  nombre ='" + etNombreC.getText() + "';");
            showMessage("Exito!", "Cliente Eliminado");
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        finish();
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}