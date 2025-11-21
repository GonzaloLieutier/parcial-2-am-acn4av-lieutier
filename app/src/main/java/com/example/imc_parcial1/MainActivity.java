package com.example.imc_parcial1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etPeso, etAltura;
    private TextView tvResultado, tvImcGigante;
    private LinearLayout resultContainer, dynamicContainer;
    private Button btnCalcular, btnVerHistorial;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        tvResultado = findViewById(R.id.tvResultado);
        tvImcGigante = findViewById(R.id.tvImcGigante);
        resultContainer = findViewById(R.id.resultContainer);
        dynamicContainer = findViewById(R.id.dynamicContainer);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);

        sharedPreferences = getSharedPreferences("HistorialIMC", MODE_PRIVATE);

        btnCalcular.setOnClickListener(v -> calcularIMC());
        btnVerHistorial.setOnClickListener(v -> abrirHistorial());
    }

    private void calcularIMC() {
        String sPeso = etPeso.getText().toString().trim();
        String sAltura = etAltura.getText().toString().trim();

        if (sPeso.isEmpty() || sAltura.isEmpty()) {
            Toast.makeText(this, "Ingresá peso y altura", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float peso = Float.parseFloat(sPeso.replace(",", "."));
            float alturaCm = Float.parseFloat(sAltura.replace(",", "."));
            if (peso <= 0 || alturaCm <= 0) {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
                return;
            }
            float alturaM = alturaCm / 100f;
            float imc = peso / (alturaM * alturaM);


            String categoria;
            int colorResId;
            if (imc < 18.5f) {
                categoria = "Bajo peso";
                colorResId = R.color.imc_underweight;
            } else if (imc < 25f) {
                categoria = "Peso normal";
                colorResId = R.color.imc_good;
            } else if (imc < 30f) {
                categoria = "Sobrepeso";
                colorResId = R.color.imc_overweight;
            } else {
                categoria = "Obesidad";
                colorResId = R.color.imc_bad;
            }


            tvImcGigante.setText(String.format(Locale.getDefault(), "%.1f", imc));
            tvImcGigante.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);


            tvResultado.setText(categoria);


            @ColorInt int color = ContextCompat.getColor(this, colorResId);
            Drawable bg = resultContainer.getBackground();
            if (bg instanceof GradientDrawable) {
                ((GradientDrawable) bg.mutate()).setColor(color);
            } else {
                resultContainer.setBackgroundColor(color);
            }

            TextView tipView;
            if (dynamicContainer.getChildCount() == 0 || !(dynamicContainer.getChildAt(0) instanceof TextView)) {
                tipView = new TextView(this);
                tipView.setTextSize(16f);
                tipView.setPadding(0, dp(8), 0, 0);
                dynamicContainer.removeAllViews();
                dynamicContainer.addView(tipView);
            } else {
                tipView = (TextView) dynamicContainer.getChildAt(0);
            }
            tipView.setText(getTip(categoria));

            guardarEnHistorial(imc, categoria);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato numérico inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarEnHistorial(float imc, String categoria) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String key = "calculo_" + System.currentTimeMillis();
        String value = String.format(Locale.getDefault(), "%.1f", imc) + "|" + categoria + "|" + fecha;
        editor.putString(key, value);
        editor.apply();
    }

    private void abrirHistorial() {
        Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
        startActivity(intent);
    }

    private String getTip(String categoria) {
        if (categoria == null) return "";
        if (categoria.equals(getString(R.string.cat_bajo_peso)))  return getString(R.string.tip_bajo_peso);
        if (categoria.equals(getString(R.string.cat_normal))) return getString(R.string.tip_normal);
        if (categoria.equals(getString(R.string.cat_sobrepeso)))   return getString(R.string.tip_sobrepeso);
        if (categoria.equals(getString(R.string.cat_obesidad)))    return getString(R.string.tip_obesidad);
        return "";
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}

