package com.example.gruc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etCpf, etPassword;
    Button btnLogin;
    TextView tvRegister, tvForgot;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCpf = findViewById(R.id.etLoginCpf);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvGoToRegister);
        tvForgot = findViewById(R.id.tvForgotPassword);

        DB = new DBHelper(this);

        // Ação do botão Entrar
        btnLogin.setOnClickListener(view -> {
            String cpf = etCpf.getText().toString();
            String pass = etPassword.getText().toString();

            if(cpf.equals("") || pass.equals("")) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                // 1. Verifica se a senha bate com o CPF
                Boolean checkUserPass = DB.checkCpfPassword(cpf, pass);

                if(checkUserPass) {
                    Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    // 2. Agora precisamos saber QUEM é este usuário (Líder ou Funcionário?)
                    Cursor cursor = DB.getUserData(cpf);
                    if (cursor.moveToFirst()) {
                        // A coluna 'eh_lider' é a 5ª coluna (indice 5) baseada na criação no DBHelper
                        // Mas é mais seguro buscar pelo nome da coluna
                        int ehLiderIndex = cursor.getColumnIndex("eh_lider");
                        int ehLider = cursor.getInt(ehLiderIndex);

                        Intent intent;
                        if (ehLider == 1) {
                            // É Líder -> Vai para a Home do Líder
                            intent = new Intent(getApplicationContext(), LeaderHomeActivity.class);
                        } else {
                            // É Funcionário -> Vai para a Home do Funcionário
                            intent = new Intent(getApplicationContext(), EmployeeHomeActivity.class);
                        }

                        // Passamos o CPF para a próxima tela, para saber quem logou
                        intent.putExtra("USER_CPF", cpf);
                        startActivity(intent);
                        finish(); // Fecha o login para não voltar com o botão "voltar"
                    }
                    cursor.close();

                } else {
                    Toast.makeText(LoginActivity.this, "CPF ou Senha incorretos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ação do link "Não possui cadastro"
        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        // Ação do link "Esqueceu a senha"
        tvForgot.setOnClickListener(view -> {
            // Vamos criar esta tela depois, por enquanto exibe aviso
            Toast.makeText(LoginActivity.this, "Funcionalidade em construção", Toast.LENGTH_SHORT).show();
            // Quando criarmos a ForgotPasswordActivity, descomente a linha abaixo:
            // startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        });
    }
}