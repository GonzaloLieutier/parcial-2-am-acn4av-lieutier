package com.example.imc_parcial1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Map;

public class HistorialActivity extends AppCompatActivity {

    private LinearLayout containerHistorial;
    private TextView tvHistorialEmpty;
    private Button btnLimpiarHistorial, btnVolver;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        containerHistorial = findViewById(R.id.containerHistorial);
        tvHistorialEmpty = findViewById(R.id.tvHistorialEmpty);
        btnLimpiarHistorial = findViewById(R.id.btnLimpiarHistorial);
        btnVolver = findViewById(R.id.btnVolver);

        sharedPreferences = getSharedPreferences("HistorialIMC", MODE_PRIVATE);

        cargarHistorial();

        btnLimpiarHistorial.setOnClickListener(v -> limpiarHistorial());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarHistorial() {
        Map<String, ?> todosLosCalculos = sharedPreferences.getAll();

        if (todosLosCalculos.isEmpty()) {
            tvHistorialEmpty.setVisibility(View.VISIBLE);
        } else {
            tvHistorialEmpty.setVisibility(View.GONE);
            containerHistorial.removeAllViews();

            for (Map.Entry<String, ?> entry : todosLosCalculos.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();

                String[] partes = value.split("\\|");
                if (partes.length == 3) {
                    String imc = partes[0];
                    String categoria = partes[1];
                    String fecha = partes[2];

                    agregarItemHistorial(imc, categoria, fecha);
                }
            }
        }
    }

    private void agregarItemHistorial(String imc, String categoria, String fecha) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackgroundResource(R.drawable.bg_historial_item);
        itemLayout.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(12));
        itemLayout.setLayoutParams(params);

        TextView tvImc = new TextView(this);
        tvImc.setText("IMC: " + imc);
        tvImc.setTextSize(20f);
        tvImc.setTextColor(ContextCompat.getColor(this, getCategoriaColor(categoria)));
        tvImc.setGravity(Gravity.START);

        TextView tvCategoria = new TextView(this);
        tvCategoria.setText(categoria);
        tvCategoria.setTextSize(16f);
        tvCategoria.setTextColor(ContextCompat.getColor(this, R.color.imc_neutral));
        tvCategoria.setGravity(Gravity.START);

        TextView tvFecha = new TextView(this);
        tvFecha.setText(fecha);
        tvFecha.setTextSize(14f);
        tvFecha.setTextColor(ContextCompat.getColor(this, R.color.imc_neutral));
        tvFecha.setGravity(Gravity.START);
        tvFecha.setPadding(0, dp(8), 0, 0);

        itemLayout.addView(tvImc);
        itemLayout.addView(tvCategoria);
        itemLayout.addView(tvFecha);

        containerHistorial.addView(itemLayout);
    }

    private int getCategoriaColor(String categoria) {
        if (categoria.equals("Bajo peso")) {
            return R.color.imc_underweight;
        } else if (categoria.equals("Peso normal")) {
            return R.color.imc_good;
        } else if (categoria.equals("Sobrepeso")) {
            return R.color.imc_overweight;
        } else if (categoria.equals("Obesidad")) {
            return R.color.imc_bad;
        }
        return R.color.imc_neutral;
    }

    private void limpiarHistorial() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        containerHistorial.removeAllViews();
        tvHistorialEmpty.setVisibility(View.VISIBLE);

        Toast.makeText(this, R.string.historial_limpiado, Toast.LENGTH_SHORT).show();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
