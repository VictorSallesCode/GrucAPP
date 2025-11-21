package com.example.gruc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    // Declaração das variáveis
    EditText etNome, etCpf, etEmail, etPhone, etPass, etAvsec, etCnv, etCred;
    Button btnRegister, btnLogin, btnPhoto;
    CheckBox cbLeader;
    LinearLayout layoutCertificates;
    ImageView imgProfile;
    DBHelper DB;

    // Variável para saber se o usuário escolheu uma foto
    private static final int PICK_IMAGE_REQUEST = 1;
    boolean photoSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar o Banco de Dados
        DB = new DBHelper(this);

        // Ligar as variáveis aos IDs do XML
        etNome = findViewById(R.id.etNome);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPass = findViewById(R.id.etPassword);

        // Campos de datas
        etAvsec = findViewById(R.id.etDateAvsec);
        etCnv = findViewById(R.id.etDateCnv);
        etCred = findViewById(R.id.etDateCred);

        // Controles
        cbLeader = findViewById(R.id.cbIsLeader);
        layoutCertificates = findViewById(R.id.layoutCertificates);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnBackToLogin);
        btnPhoto = findViewById(R.id.btnSelectPhoto);
        imgProfile = findViewById(R.id.imgProfile);

        // --- LÓGICA 1: Ocultar campos se for Lider ---
        cbLeader.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Se for lider, esconde (GONE) as datas e o botão de foto
                layoutCertificates.setVisibility(View.GONE);
                btnPhoto.setVisibility(View.GONE);
                imgProfile.setVisibility(View.GONE);
            } else {
                // Se for funcionário, mostra tudo
                layoutCertificates.setVisibility(View.VISIBLE);
                btnPhoto.setVisibility(View.VISIBLE);
                imgProfile.setVisibility(View.VISIBLE);
            }
        });

        // --- LÓGICA 2: Selecionar Foto ---
        btnPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Abre a galeria
            startActivityForResult(Intent.createChooser(intent, "Selecione a Foto"), PICK_IMAGE_REQUEST);
        });

        // --- LÓGICA 3: Botão Cadastrar ---
        btnRegister.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String pass = etPass.getText().toString();

            boolean isLeader = cbLeader.isChecked();
            int ehLiderInt = isLeader ? 1 : 0; // Banco salva 1 para sim, 0 para não

            // Validação básica
            if (nome.equals("") || cpf.equals("") || pass.equals("")) {
                Toast.makeText(RegisterActivity.this, "Por favor, preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Se for funcionário, precisa das datas e foto
            String dAvsec = "", dCnv = "", dCred = "";
            byte[] fotoBytes = null;

            if (!isLeader) {
                dAvsec = etAvsec.getText().toString();
                dCnv = etCnv.getText().toString();
                dCred = etCred.getText().toString();

                if (dAvsec.equals("") || dCnv.equals("") || dCred.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Funcionários precisam informar as datas dos certificados.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Converter a imagem do ImageView para Bytes
                if (imgProfile.getDrawable() != null && photoSelected) {
                    Bitmap bitmap = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
                    fotoBytes = DBHelper.getBytesFromBitmap(bitmap);
                } else {
                    // Opcional: Bloquear se não tiver foto
                    // Toast.makeText(RegisterActivity.this, "Selecione uma foto.", Toast.LENGTH_SHORT).show();
                    // return;
                }
            }

            // Verifica se CPF já existe
            Boolean checkCpf = DB.checkCpf(cpf);
            if (!checkCpf) {
                // Tenta inserir
                Boolean insert = DB.insertData(cpf, nome, email, phone, pass, ehLiderInt, fotoBytes, dAvsec, dCnv, dCred);
                if (insert) {
                    Toast.makeText(RegisterActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    // Aqui redirecionaremos para o Login (quando criarmos)
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Erro ao cadastrar.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Usuário com este CPF já existe.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão voltar para Login
        btnLogin.setOnClickListener(v -> {
            // finish fecha esta tela e volta para a anterior (que será o login)
            finish();
        });
    }

    // Método para receber o resultado da Galeria de Fotos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProfile.setImageBitmap(bitmap);
                photoSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}