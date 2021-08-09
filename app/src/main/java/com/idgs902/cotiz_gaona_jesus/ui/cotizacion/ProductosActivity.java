package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;

public class ProductosActivity extends AppCompatActivity {

    private Button btnAgregarP;
    private TextInputEditText etNumeroP, etNombreP, txtLinea, etExistencias, txtCostoP, etCostoProm, txtVentaMenudeo, etCostoMayoreo;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        etNumeroP = this.findViewById(R.id.etNumeroP);
        etNombreP = this.findViewById(R.id.etNombreP);
        txtLinea = this.findViewById(R.id.txtLinea);
        etExistencias = this.findViewById(R.id.etExistencias);
        txtCostoP = this.findViewById(R.id.txtCostoP);
        etCostoProm = this.findViewById(R.id.etCostoProm);
        txtVentaMenudeo = this.findViewById(R.id.txtVentaMenudeo);
        etCostoMayoreo = this.findViewById(R.id.etCostoMayoreo);
        btnAgregarP = this.findViewById(R.id.btnGuardarP);

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        etNumeroP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String texto = "";
                if (etNumeroP.getText().toString().trim().length() != 0) {
                    texto = etNumeroP.getText().toString();
                    String clave = String.valueOf(texto.charAt(0)).toUpperCase();
                    switch (clave) {
                        case "A":
                            txtLinea.setText("Hombre 25 - 30");
                            break;
                        case "B":
                            txtLinea.setText("Joven  22 - 25 ");
                            break;
                        case "C":
                            txtLinea.setText("Niño 18 - 21");
                            break;
                        case "D":
                            txtLinea.setText("Niño 15 - 17");
                            break;
                        case "E":
                            txtLinea.setText("Niño 12 - 14");
                            break;
                        case "R":
                            txtLinea.setText("Dama 22 - 26");
                            break;
                        case "S":
                            txtLinea.setText("Niña 18 - 21");
                            break;
                        case "T":
                            txtLinea.setText("Niña 15 - 17");
                            break;
                        case "U":
                            txtLinea.setText("Niña 12 -14 ");
                            break;
                        case "X":
                            txtLinea.setText("BEBE 10 - 12");
                            break;
                        default:
                            showMessage("Information",
                                    "Las claves deben inciar con una letra \nPosibles Opciones\n['A','B','C','D','E','R','S','T','U','X']");
                            break;
                    }

                }
            }
        });

        btnAgregarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    guardar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });
    }

    private void guardar() {
        if (etNombreP.getText().toString().trim().length() != 0
                && etNumeroP.getText().toString().trim().length() != 0
                && txtLinea.getText().toString().trim().length() != 0) {

            db.execSQL("INSERT INTO producto(clave, nombre, linea,existencia,Pcosto,PCpromedio,PMenudeo,PMayoreo ) VALUES('" + etNumeroP.getText() + "','"
                    + etNombreP.getText() + "','"
                    + txtLinea.getText() + "',0,0,0,0,0);");
            showMessage("Exito!", "Cotización agregada");
            finish();
        } else {
            showMessage("Error", "Debe llenar todos los campos");
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