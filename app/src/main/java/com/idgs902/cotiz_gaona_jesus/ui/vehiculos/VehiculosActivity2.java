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

public class VehiculosActivity2 extends AppCompatActivity {

    private Button btnModificarV, btnEliminarV;
    private TextInputEditText etClaveVM, etNombreVM, etMarcaM, etModeloM, etCostoM;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos2);

        etClaveVM = this.findViewById(R.id.etClaveVM);
        etNombreVM = this.findViewById(R.id.etNombreVM);
        etMarcaM = this.findViewById(R.id.etMarcaM);
        etModeloM = this.findViewById(R.id.etModeloM);
        etCostoM = this.findViewById(R.id.etCostoM);

        btnModificarV = this.findViewById(R.id.btnModificarV);
        btnEliminarV = this.findViewById(R.id.btnEliminarV);
        try {
            db = openOrCreateDatabase("CotizacionesDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(VehiculoFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM vehiculo WHERE clave ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveVM.setText(c.getString(0));
                    etNombreVM.setText(c.getString(1));
                    etMarcaM.setText(c.getString(2));
                    etModeloM.setText(c.getString(3));
                    etCostoM.setText(c.getString(4));
                    etClaveVM.setEnabled(false);
                }
            } else {
                showMessage("Info", "No existe un Registro con esa clave");
            }

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnEliminarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
        btnModificarV.setOnClickListener(new View.OnClickListener() {
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
        if (etNombreVM.getText().toString().trim().length() != 0
                && etMarcaM.getText().toString().trim().length() != 0
                && etModeloM.getText().toString().trim().length() != 0
                && etCostoM.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE vehiculo SET nombre ='"
                        + etNombreVM.getText() + "', marca='" + etMarcaM.getText() +
                        "modelo='" + etModeloM.getText() + "', costo='" + etCostoM.getText() +
                        "' WHERE clave = '" + etClaveVM.getText() + "';");
                showMessage("Exito!", "Vehiculo Modificado");
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
            db.execSQL("DELETE FROM vehiculo WHERE clave ='" +
                    etClaveVM.getText() + "' AND  nombre ='" + etNombreVM.getText() + "';");
            showMessage("Exito!", "Vehiculo Eliminado");
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