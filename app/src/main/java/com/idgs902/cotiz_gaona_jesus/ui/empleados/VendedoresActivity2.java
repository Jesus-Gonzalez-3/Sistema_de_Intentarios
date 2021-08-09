package com.idgs902.cotiz_gaona_jesus.ui.empleados;

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

public class VendedoresActivity2 extends AppCompatActivity {

    private Button btnModificarE, btnEliminarE;
    private TextInputEditText etClaveEM, etNombreEM, txtCalleM, txtTelefonoM, txtComisionesM, etColoniaM, etEmailM;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedores2);

        etClaveEM = this.findViewById(R.id.etClaveEM);
        etNombreEM = this.findViewById(R.id.etNombreEM);
        txtCalleM = this.findViewById(R.id.txtCalleM);
        txtTelefonoM = this.findViewById(R.id.txtTelefonoM);
        txtComisionesM = this.findViewById(R.id.txtComisionesM);
        etColoniaM = this.findViewById(R.id.etColoniaM);
        etEmailM = this.findViewById(R.id.etEmailM);
        btnEliminarE = this.findViewById(R.id.btnEliminarE);
        btnModificarE = this.findViewById(R.id.btnModificarE);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(VendedoresFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM vendedor WHERE Id ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etClaveEM.setText(c.getString(0));
                    etNombreEM.setText(c.getString(1));
                    txtCalleM.setText(c.getString(2));
                    etColoniaM.setText(c.getString(3));
                    txtTelefonoM.setText(c.getString(4));
                    etEmailM.setText(c.getString(5));
                    txtComisionesM.setText(c.getString(6));
                }
            } else {
                showMessage("Info", "No existe un Registro con esa clave");
                finish();
            }

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnModificarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    modificar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnEliminarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    public void modificar() {
        if (etNombreEM.getText().toString().trim().length() != 0
                && txtCalleM.getText().toString().trim().length() != 0
                && txtTelefonoM.getText().toString().trim().length()  != 0
                && etColoniaM.getText().toString().trim().length() != 0
                && etEmailM.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE vendedor SET nombre ='"
                        + etNombreEM.getText() + "', calle='" + txtCalleM.getText()
                        + "', colonia='" + etColoniaM.getText()+ "', telefono='" + txtTelefonoM.getText()
                        + "', email='" + etEmailM.getText()
                        + "' WHERE Id = '" + etClaveEM.getText() + "';");
                showMessage("Exito!", "Vendedor Modificado");
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
            db.execSQL("DELETE FROM vendedor WHERE Id ='" +
                    etClaveEM.getText() + "' AND  nombre ='" + etNombreEM.getText() + "';");
            showMessage("Exito!", "Vendedor Eliminado");
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