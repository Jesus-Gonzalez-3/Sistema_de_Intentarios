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
    private TextInputEditText etNombreC, etCalleC, etColoniaC, etCiudadC, etRfcC, etTelefonoC, etEmailC, etClaveC, etSaldoC;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente2);

        btnModificar = this.findViewById(R.id.btnModificarC);
        btnEliminar = this.findViewById(R.id.btnEliminarC);

        etClaveC = this.findViewById(R.id.etClaveCM);
        etNombreC = this.findViewById(R.id.etNombreCM);
        etCalleC = this.findViewById(R.id.etCalleCM);
        etColoniaC = this.findViewById(R.id.etColoniaCM);
        etCiudadC = this.findViewById(R.id.etCiudadCM);
        etRfcC = this.findViewById(R.id.etRfcCM);
        etTelefonoC = this.findViewById(R.id.etTelefonoCM);
        etEmailC = this.findViewById(R.id.etEmailCM);
        etSaldoC = this.findViewById(R.id.etSaldoCM);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(ClienteFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM cliente WHERE id ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveC.setText(c.getString(0));
                    etNombreC.setText(c.getString(1));
                    etCalleC.setText(c.getString(2));
                    etColoniaC.setText(c.getString(3));
                    etCiudadC.setText(c.getString(4));
                    etRfcC.setText(c.getString(5));
                    etTelefonoC.setText(c.getString(6));
                    etEmailC.setText(c.getString(7));
                    etSaldoC.setText(c.getString(8));
                    etClaveC.setEnabled(false);
                    etSaldoC.setEnabled(false);
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
        if (  etNombreC.getText().toString().trim().length() != 0
                && etCalleC.getText().toString().trim().length() != 0
                && etColoniaC.getText().toString().trim().length() != 0
                && etCiudadC.getText().toString().trim().length() != 0
                && etRfcC.getText().toString().trim().length() != 0
                && etTelefonoC.getText().toString().trim().length() != 0
                && etEmailC.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE cliente SET " +
                        "nombre ='" + etNombreC.getText() + "', " +
                        "calle ='" + etCalleC.getText() + "', " +
                        "colonia ='" + etColoniaC.getText() + "', " +
                        "ciudad ='" + etCiudadC.getText() + "', " +
                        "rfc ='" + etRfcC.getText() + "', " +
                        "telefono ='" + etTelefonoC.getText() + "', " +
                        "email='" + etEmailC.getText() + "' WHERE id = '" + etClaveC.getText() + "';");
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
            db.execSQL("DELETE FROM cliente WHERE id ='" +
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