package com.idgs902.cotiz_gaona_jesus.ui.vehiculos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.idgs902.cotiz_gaona_jesus.ui.cliente.ClienteFragment;

public class ProveedorActivity2 extends AppCompatActivity {

    private Button btnModificar, btnEliminar;
    private TextInputEditText etNombreP, etCalleP, etColoniaP, etCiudadP, etRfcP, etTelefonoP, etEmailP, etClaveP, etSaldoP;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor2);

        btnModificar = this.findViewById(R.id.btnModificarPR);
        btnEliminar = this.findViewById(R.id.btnEliminarPR);

        etClaveP = this.findViewById(R.id.etClavePRM);
        etNombreP = this.findViewById(R.id.etNombrePRM);
        etCalleP = this.findViewById(R.id.etCallePRM);
        etColoniaP = this.findViewById(R.id.etColoniaPRM);
        etCiudadP = this.findViewById(R.id.etCiudadPRM);
        etRfcP = this.findViewById(R.id.etRfcPRM);
        etTelefonoP = this.findViewById(R.id.etTelefonoPRM);
        etEmailP = this.findViewById(R.id.etEmailPRM);
        etSaldoP = this.findViewById(R.id.etSaldoPRM);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(ProveedorFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM proveedor WHERE id ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveP.setText(c.getString(0));
                    etNombreP.setText(c.getString(1));
                    etCalleP.setText(c.getString(2));
                    etColoniaP.setText(c.getString(3));
                    etCiudadP.setText(c.getString(4));
                    etRfcP.setText(c.getString(5));
                    etTelefonoP.setText(c.getString(6));
                    etEmailP.setText(c.getString(7));
                    etSaldoP.setText(c.getString(8));
                    etClaveP.setEnabled(false);
                    etSaldoP.setEnabled(false);
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
                try {
                    modificar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void modificar() {
        if (  etNombreP.getText().toString().trim().length() != 0
                && etCalleP.getText().toString().trim().length() != 0
                && etColoniaP.getText().toString().trim().length() != 0
                && etCiudadP.getText().toString().trim().length() != 0
                && etRfcP.getText().toString().trim().length() != 0
                && etTelefonoP.getText().toString().trim().length() != 0
                && etEmailP.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE cliente SET " +
                        "nombre ='" + etNombreP.getText() + "', " +
                        "calle ='" + etCalleP.getText() + "', " +
                        "colonia ='" + etColoniaP.getText() + "', " +
                        "ciudad ='" + etCiudadP.getText() + "', " +
                        "rfc ='" + etRfcP.getText() + "', " +
                        "telefono ='" + etTelefonoP.getText() + "', " +
                        "email='" + etEmailP.getText() + "' WHERE id = '" + etClaveP.getText() + "';");
                showMessage("Exito!", "Proveedor Modificado");
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
            db.execSQL("DELETE FROM proveedor WHERE id ='" +
                    etClaveP.getText() + "' AND  nombre ='" + etNombreP.getText() + "';");
            showMessage("Exito!", "Proveedor Eliminado");
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        finish();
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}