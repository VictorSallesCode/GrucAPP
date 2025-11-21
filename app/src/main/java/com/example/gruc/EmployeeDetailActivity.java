package com.example.gruc;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmployeeDetailActivity extends AppCompatActivity {

    TextView tvName, tvCpf;
    TextView tvDateAvsec, tvDateCnv, tvDateCred;
    ImageView imgProfile;
    View statusAvsec, statusCnv, statusCred;
    Button btnBack;
    DBHelper DB;
    String targetCpf;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        // Recebe o CPF do funcionário clicado na lista
        targetCpf = getIntent().getStringExtra("USER_CPF");

        DB = new DBHelper(this);

        // Ligações com o XML
        tvName = findViewById(R.id.tvDetailName);
        tvCpf = findViewById(R.id.tvDetailCpf);
        imgProfile = findViewById(R.id.imgDetailProfile);

        tvDateAvsec = findViewById(R.id.tvDetailDateAvsec);
        tvDateCnv = findViewById(R.id.tvDetailDateCnv);
        tvDateCred = findViewById(R.id.tvDetailDateCred);

        statusAvsec = findViewById(R.id.statusDetailAvsec);
        statusCnv = findViewById(R.id.statusDetailCnv);
        statusCred = findViewById(R.id.statusDetailCred);

        btnBack = findViewById(R.id.btnDetailBack);

        loadEmployeeData();

        // Botão Voltar apenas fecha a tela atual
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadEmployeeData() {
        Cursor cursor = DB.getUserData(targetCpf);

        if (cursor.moveToFirst()) {
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            byte[] fotoBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("foto"));
            String dAvsec = cursor.getString(cursor.getColumnIndexOrThrow("data_avsec"));
            String dCnv = cursor.getString(cursor.getColumnIndexOrThrow("data_cnv"));
            String dCred = cursor.getString(cursor.getColumnIndexOrThrow("data_cred"));

            tvName.setText(nome);
            tvCpf.setText("CPF: " + targetCpf);

            if (fotoBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imgProfile.setImageBitmap(bitmap);
            }

            // Calcular cores (sem alertas pop-up)
            calculateColor(dAvsec, 2, statusAvsec, tvDateAvsec);
            calculateColor(dCnv, 2, statusCnv, tvDateCnv);
            calculateColor(dCred, 1, statusCred, tvDateCred);
        } else {
            Toast.makeText(this, "Erro ao carregar funcionário", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    private void calculateColor(String dataEmissaoStr, int validadeAnos, View bolinhaView, TextView tvDisplay) {
        try {
            Date dataEmissao = sdf.parse(dataEmissaoStr);

            Calendar cal = Calendar.getInstance();
            cal.setTime(dataEmissao);
            cal.add(Calendar.YEAR, validadeAnos);
            Date dataVencimento = cal.getTime();

            tvDisplay.setText("Vence em: " + sdf.format(dataVencimento));

            Date hoje = new Date();
            long diffMillies = dataVencimento.getTime() - hoje.getTime();
            long diffDias = TimeUnit.DAYS.convert(diffMillies, TimeUnit.MILLISECONDS);

            if (diffDias < 0) {
                bolinhaView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else if (diffDias <= 14) {
                bolinhaView.getBackground().setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_IN);
            } else if (diffDias <= 90) {
                bolinhaView.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            } else {
                bolinhaView.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        } catch (Exception e) {
            tvDisplay.setText("Data N/D");
            bolinhaView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
    }
}