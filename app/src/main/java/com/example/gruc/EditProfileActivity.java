package com.example.gruc;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    EditText etNome, etCpf, etEmail, etPhone, etAvsec, etCnv, etCred;
    Button btnSave, btnCancel;
    DBHelper DB;
    String userCpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Pega o CPF enviado pela Home
        userCpf = getIntent().getStringExtra("USER_CPF");
        DB = new DBHelper(this);

        // Vincular IDs
        etNome = findViewById(R.id.etEditNome);
        etCpf = findViewById(R.id.etEditCpf);
        etEmail = findViewById(R.id.etEditEmail);
        etPhone = findViewById(R.id.etEditPhone);
        etAvsec = findViewById(R.id.etEditAvsec);
        etCnv = findViewById(R.id.etEditCnv);
        etCred = findViewById(R.id.etEditCred);

        btnSave = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancelEdit);

        // Carregar dados atuais
        loadCurrentData();

        // Ação Salvar
        btnSave.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String dAvsec = etAvsec.getText().toString();
            String dCnv = etCnv.getText().toString();
            String dCred = etCred.getText().toString();

            if(email.isEmpty() || phone.isEmpty() || dAvsec.isEmpty() || dCnv.isEmpty() || dCred.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Preencha todos os campos editáveis", Toast.LENGTH_SHORT).show();
                return;
            }

            // Atualiza no Banco
            // O método updateProfile no DBHelper recebe: cpf, telefone, email, avsec, cnv, cred
            Boolean checkUpdate = DB.updateProfile(userCpf, phone, email, dAvsec, dCnv, dCred);

            if(checkUpdate) {
                Toast.makeText(EditProfileActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela e volta para a Home (que vai recarregar os dados novos)
            } else {
                Toast.makeText(EditProfileActivity.this, "Erro ao atualizar dados.", Toast.LENGTH_SHORT).show();
            }
        });

        // Ação Cancelar
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadCurrentData() {
        Cursor cursor = DB.getUserData(userCpf);
        if(cursor.moveToFirst()) {
            // Preencher campos (Nome e CPF são readonly no XML, mas preenchemos visualmente)
            etNome.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
            etCpf.setText(cursor.getString(cursor.getColumnIndexOrThrow("cpf")));

            // Preencher campos editáveis com o que já existe
            etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("telefone")));
            etAvsec.setText(cursor.getString(cursor.getColumnIndexOrThrow("data_avsec")));
            etCnv.setText(cursor.getString(cursor.getColumnIndexOrThrow("data_cnv")));
            etCred.setText(cursor.getString(cursor.getColumnIndexOrThrow("data_cred")));
        }
        cursor.close();
    }
}