package com.idgs902.cotiz_gaona_jesus;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.idgs902.cotiz_gaona_jesus.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_clientes, R.id.nav_empleado, R.id.nav_vehiculos, R.id.nav_cotizaciones, R.id.nav_compras, R.id.nav_ventas, R.id.nav_reporte_productos)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

                Log.i("Mensaje", "No se tiene permiso para leer.");
                ActivityCompat.requestPermissions(((Activity)getParent()), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
                ActivityCompat.requestPermissions(((Activity)getParent()), new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
            }
        }

        try {
            db = openOrCreateDatabase("SistemaInventariosDB", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS vendedor (" +
                    "id INTEGER PRIMARY KEY, nombre Varchar, calle Varchar, colonia Varchar, telefono Varchar, email Varchar, comisiones Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS producto (" +
                    "id INTEGER PRIMARY KEY, clave Varchar, nombre Varchar, linea Varchar, existencia Varchar, Pcosto Varchar, PCpromedio Varchar, PMenudeo Varchar, PMayoreo Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS cliente (" +
                    "id INTEGER PRIMARY KEY, nombre Varchar, calle Varchar, colonia Varchar, ciudad Varchar, rfc Varchar, telefono Varchar, email Varchar, saldo Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS proveedor (" +
                    "id INTEGER PRIMARY KEY, nombre Varchar, calle Varchar, colonia Varchar, ciudad Varchar, rfc Varchar, telefono Varchar, email Varchar, saldo Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS compras ("+
                    "id INTEGER PRIMARY KEY, clave Varchar, clave_p Varchar, nombre_p Varchar," +
                    "calle_p Varchar,fecha Varchar, total_pares Varchar, subtotal Varchar, iva Varchar, total Varchar, tipo_recibo Varchar, clave_recibo Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS compras_detalle ("+
                    "id INTEGER PRIMARY KEY, id_compra INTEGER, clave_pro Varchar, nombre_pro Varchar," +
                    "unidad Varchar,linea Varchar, cantidad_pro Varchar, costo_pro Varchar, importe Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS ventas ("+
                    "id INTEGER PRIMARY KEY, clave_cliente Varchar, nombre_cliente Varchar, calle_cliente Varchar," +
                    "clave_vendedor Varchar,nombre_vendedor Varchar, fecha Varchar, comision Varchar, tipo_recibo Varchar, clave_recibo Varchar, total_productos Varchar, suma Varchar, iva Varchar, total_venta Varchar, comision_vendedor Varchar);");
            db.execSQL("CREATE TABLE IF NOT EXISTS ventas_detalle ("+
                    "id INTEGER PRIMARY KEY, id_venta INTEGER, clave_pro Varchar, nombre_pro Varchar," +
                    "unidad Varchar,linea Varchar, cantidad_pro Varchar, pre_venta Varchar, importe Varchar);");
        } catch (Exception ex) {
            showMessage("Error", ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}