package com.idgs902.cotiz_gaona_jesus.ui.cotizacion;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.idgs902.cotiz_gaona_jesus.R;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductosActivity2 extends AppCompatActivity {

    private Button btnModificarP, btnEliminarP;
    private TextInputEditText etId, etNumeroPM, etNombrePM, txtLineaM, etExistenciasM, txtCostoPM, etCostoPromM, txtVentaMenudeoM, etCostoMayoreoM;
    private SQLiteDatabase db;

    private static boolean creado = false;

    DecimalFormat format = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos2);

        btnModificarP = this.findViewById(R.id.btnModificarP);
        btnEliminarP = this.findViewById(R.id.btnEliminarP);
        etId = this.findViewById(R.id.etIdP);
        etNumeroPM = this.findViewById(R.id.etNumeroPM);
        etNombrePM = this.findViewById(R.id.etNombrePM);
        txtLineaM = this.findViewById(R.id.txtLineaM);
        etExistenciasM = this.findViewById(R.id.etExistenciasM);
        txtCostoPM = this.findViewById(R.id.txtCostoPM);
        etCostoPromM = this.findViewById(R.id.etCostoPromM);
        txtVentaMenudeoM = this.findViewById(R.id.txtVentaMenudeoM);
        etCostoMayoreoM = this.findViewById(R.id.etCostoMayoreoM);

        etExistenciasM.setEnabled(false);
        txtCostoPM.setEnabled(false);
        txtVentaMenudeoM.setEnabled(false);
        etCostoPromM.setEnabled(false);
        etCostoMayoreoM.setEnabled(false);


        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
        try {
            Intent i = getIntent();
            String codigo = i.getStringExtra(ProductosFragment.DATOS);
            Cursor c = db.rawQuery("SELECT * FROM producto WHERE id ='" + codigo + "';", null);
            if (c.getCount() != 0) {
                if (c.moveToFirst()) {
                    etId.setText(c.getString(0));
                    etNumeroPM.setText(c.getString(1));
                    etNombrePM.setText(c.getString(2));
                    txtLineaM.setText(c.getString(3));
                    etExistenciasM.setText(c.getString(4));
                    txtCostoPM.setText(c.getString(5));
                    etCostoPromM.setText(format.format(Double.parseDouble(c.getString(6))));
                    txtVentaMenudeoM.setText(format.format(Double.parseDouble(c.getString(7))));
                    etCostoMayoreoM.setText(format.format(Double.parseDouble(c.getString(8))));
                }
            } else {
                showMessage("Info", "No existe un Registro con esa clave");
                finish();
            }

        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }

        etNumeroPM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String texto = "";
                if (etNumeroPM.getText().toString().trim().length() != 0) {
                    texto = etNumeroPM.getText().toString();
                    String clave = String.valueOf(texto.charAt(0)).toUpperCase();
                    switch (clave) {
                        case "A":
                            txtLineaM.setText("Hombre 25 - 30");
                            break;
                        case "B":
                            txtLineaM.setText("Joven  22 - 25 ");
                            break;
                        case "C":
                            txtLineaM.setText("Niño 18 - 21");
                            break;
                        case "D":
                            txtLineaM.setText("Niño 15 - 17");
                            break;
                        case "E":
                            txtLineaM.setText("Niño 12 - 14");
                            break;
                        case "R":
                            txtLineaM.setText("Dama 22 - 26");
                            break;
                        case "S":
                            txtLineaM.setText("Niña 18 - 21");
                            break;
                        case "T":
                            txtLineaM.setText("Niña 15 - 17");
                            break;
                        case "U":
                            txtLineaM.setText("Niña 12 -14 ");
                            break;
                        case "X":
                            txtLineaM.setText("BEBE 10 - 12");
                            break;
                        default:
                            showMessage("Information",
                                    "Las claves deben inciar con una letra \nPosibles Opciones\n['A','B','C','D','E','R','S','T','U','X']");
                            break;
                    }

                }
            }
        });

        btnEliminarP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    eliminar();
                } catch (Exception ex) {
                    showMessage("Error", ex.getMessage());
                }
            }
        });

        btnModificarP.setOnClickListener(new View.OnClickListener() {
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
        if (etNombrePM.getText().toString().trim().length() != 0
                && etNumeroPM.getText().toString().trim().length() != 0
                && txtLineaM.getText().toString().trim().length() != 0) {
            try {
                db.execSQL("UPDATE producto SET clave ='" + etNumeroPM.getText() +
                        "', nombre ='" + etNombrePM.getText() +
                        "', linea ='" + txtLineaM.getText().toString() +
                        "' WHERE id = '" + etId.getText() + "';");
                showMessage("Exito!", "Cotización Modificada");
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
            db.execSQL("DELETE FROM producto WHERE id ='" +
                    etId.getText() + "' AND  clave ='" + etNumeroPM.getText() + "';");
            showMessage("Exito!", "Producto Eliminado");
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