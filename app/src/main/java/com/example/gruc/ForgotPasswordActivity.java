package com.example.gruc;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etCpf, etNome, etNovaSenha, etConfirmarSenha;
    Button btnReset, btnCancel;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        DB = new DBHelper(this);

        etCpf = findViewById(R.id.etForgotCpf);
        etNome = findViewById(R.id.etForgotName);
        etNovaSenha = findViewById(R.id.etNewPassword);
        etConfirmarSenha = findViewById(R.id.etConfirmNewPassword);

        btnReset = findViewById(R.id.btnResetPassword);
        btnCancel = findViewById(R.id.btnCancelReset);

        // Botão Redefinir
        btnReset.setOnClickListener(view -> {
            String cpf = etCpf.getText().toString().trim();
            String nome = etNome.getText().toString().trim(); // trim() remove espaços extras no início/fim
            String pass = etNovaSenha.getText().toString();
            String repass = etConfirmarSenha.getText().toString();

            // 1. Valida se campos estão preenchidos
            if(cpf.equals("") || nome.equals("") || pass.equals("") || repass.equals("")) {
                Toast.makeText(ForgotPasswordActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Valida se as senhas batem
            if(!pass.equals(repass)){
                Toast.makeText(ForgotPasswordActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Verifica identidade no Banco
            Boolean checkUser = DB.checkUserForRecovery(nome, cpf);

            if(checkUser == true) {
                // 4. Identidade confirmada -> Atualiza a senha
                Boolean update = DB.updatePassword(cpf, pass);
                if(update == true) {
                    Toast.makeText(ForgotPasswordActivity.this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Volta para o Login
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Erro ao atualizar senha.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Falha de segurança
                Toast.makeText(ForgotPasswordActivity.this, "Dados incorretos. Verifique o CPF e o Nome.", Toast.LENGTH_LONG).show();
            }
        });

        // Botão Cancelar
        btnCancel.setOnClickListener(view -> finish());
    }
}