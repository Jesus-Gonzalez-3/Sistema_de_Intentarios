package com.idgs902.cotiz_gaona_jesus.ui.vehiculos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

public class ProveedorActivity extends AppCompatActivity {

    private Button btnAgregarP;
    private TextInputEditText etNombreP, etCalleP, etColoniaP, etCiudadP, etRfcP, etTelefonoP, etEmailP;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor);

        etNombreP = this.findViewById(R.id.etNombrePr);
        etCalleP = this.findViewById(R.id.etCallePr);
        etColoniaP = this.findViewById(R.id.etColoniaPr);
        etCiudadP = this.findViewById(R.id.etCiudadPr);
        etRfcP = this.findViewById(R.id.etRfcPr);
        etTelefonoP = this.findViewById(R.id.etTelefonoPr);
        etEmailP = this.findViewById(R.id.etEmailPr);

        btnAgregarP = this.findViewById(R.id.btnGuardarPr);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        btnAgregarP.setOnClickListener(new View.OnClickListener() {
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
        if (etNombreP.getText().toString().trim().length() != 0
                && etCalleP.getText().toString().trim().length() != 0
                && etColoniaP.getText().toString().trim().length() != 0
                && etCiudadP.getText().toString().trim().length() != 0
                && etRfcP.getText().toString().trim().length() != 0
                && etTelefonoP.getText().toString().trim().length() != 0
                && etEmailP.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("INSERT INTO proveedor(nombre,calle,colonia,ciudad, rfc, telefono,email, saldo) VALUES(" +
                        "'" + etNombreP.getText() + "'," +
                        "'" + etCalleP.getText() + "'," +
                        "'"+ etColoniaP.getText() + "'," +
                        "'" + etCiudadP.getText() + "'," +
                        "'" + etRfcP.getText() + "'," +
                        "'" + etTelefonoP.getText() + "'," +
                        "'" + etEmailP.getText() + "'," +
                        "'" + saldo + "');");
                showMessage("Exito!", "Proveedor agregado");
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