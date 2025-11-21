package com.example.gruc;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmployeeHomeActivity extends AppCompatActivity {

    TextView tvName, tvCpf;
    TextView tvDateAvsec, tvDateCnv, tvDateCred;
    ImageView imgProfile;
    View statusAvsec, statusCnv, statusCred;
    Button btnLogout, btnEdit;
    DBHelper DB;
    String userCpf;

    // Formato da data no Brasil
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

        // 1. Pegar o CPF que veio do Login
        userCpf = getIntent().getStringExtra("USER_CPF");

        DB = new DBHelper(this);

        // Vincular layout
        tvName = findViewById(R.id.tvHomeName);
        tvCpf = findViewById(R.id.tvHomeCpf);
        imgProfile = findViewById(R.id.imgHomeProfile);

        tvDateAvsec = findViewById(R.id.tvDateAvsec);
        tvDateCnv = findViewById(R.id.tvDateCnv);
        tvDateCred = findViewById(R.id.tvDateCred);

        statusAvsec = findViewById(R.id.statusAvsec);
        statusCnv = findViewById(R.id.statusCnv);
        statusCred = findViewById(R.id.statusCred);

        btnLogout = findViewById(R.id.btnLogout);
        btnEdit = findViewById(R.id.btnEditProfile);

        loadUserData();

        // Botão Sair
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeHomeActivity.this, LoginActivity.class);
            // Limpa a pilha de telas para o usuário não poder voltar com botão "back"
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Botão Editar (Vamos criar essa tela depois)
        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade Editar será a próxima!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EmployeeHomeActivity.this, EditProfileActivity.class);
            intent.putExtra("USER_CPF", userCpf);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega os dados se voltar da tela de edição
        loadUserData();
    }

    private void loadUserData() {
        Cursor cursor = DB.getUserData(userCpf);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.moveToFirst()) {
            // Pegando índices das colunas (Boa prática)
            int idxNome = cursor.getColumnIndex("nome");
            int idxFoto = cursor.getColumnIndex("foto");
            int idxAvsec = cursor.getColumnIndex("data_avsec");
            int idxCnv = cursor.getColumnIndex("data_cnv");
            int idxCred = cursor.getColumnIndex("data_cred");

            // 1. Preencher Cabeçalho
            tvName.setText(cursor.getString(idxNome));
            tvCpf.setText("CPF: " + userCpf);

            byte[] fotoBytes = cursor.getBlob(idxFoto);
            if (fotoBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imgProfile.setImageBitmap(bitmap);
            }

            // 2. Calcular e Mostrar Status dos Certificados
            // AVSEC (2 Anos)
            String dAvsec = cursor.getString(idxAvsec);
            processCertificateLogic(dAvsec, 2, "AVSEC", statusAvsec, tvDateAvsec);

            // CNV (2 Anos)
            String dCnv = cursor.getString(idxCnv);
            processCertificateLogic(dCnv, 2, "CNV", statusCnv, tvDateCnv);

            // Credencial (1 Ano)
            String dCred = cursor.getString(idxCred);
            processCertificateLogic(dCred, 1, "Credencial", statusCred, tvDateCred);
        }
        cursor.close();
    }

    // Lógica central de vencimento e cores
    private void processCertificateLogic(String dataEmissaoStr, int validadeAnos, String nomeCert, View bolinhaView, TextView tvDisplay) {
        try {
            Date dataEmissao = sdf.parse(dataEmissaoStr);

            // Calcular Vencimento
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataEmissao);
            cal.add(Calendar.YEAR, validadeAnos);
            Date dataVencimento = cal.getTime();

            tvDisplay.setText("Vence em: " + sdf.format(dataVencimento));

            // Calcular Diferença em Dias
            Date hoje = new Date();
            long diffMillies = dataVencimento.getTime() - hoje.getTime();
            long diffDias = TimeUnit.DAYS.convert(diffMillies, TimeUnit.MILLISECONDS);

            // Aplicar Cores e ALERTAS
            if (diffDias < 0) {
                // --- VERMELHO (Vencido) ---
                bolinhaView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                mostrarAlerta("VENCIDO!", nomeCert, "Este certificado venceu há " + Math.abs(diffDias) + " dias. Regularize imediatamente!");

            } else if (diffDias <= 14) {
                // --- LARANJA (Crítico - 2 semanas) ---
                bolinhaView.getBackground().setColorFilter(Color.parseColor("#FF9800"), PorterDuff.Mode.SRC_IN);
                mostrarAlerta("URGENTE", nomeCert, "Atenção! Faltam apenas " + diffDias + " dias para vencer.");

            } else if (diffDias <= 90) {
                // --- AMARELO (Atenção - 3 meses) ---
                bolinhaView.getBackground().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                mostrarAlerta("Atenção", nomeCert, "O certificado está a menos de 3 meses do vencimento (" + diffDias + " dias).");

            } else {
                // --- VERDE (OK) ---
                bolinhaView.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                // Sem alerta para verde
            }

        } catch (Exception e) {
            tvDisplay.setText("Data Inválida");
            bolinhaView.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    // Metodo de Alerta mais dinâmico
    private void mostrarAlerta(String titulo, String certificado, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo + ": " + certificado)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }

}