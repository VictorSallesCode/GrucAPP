package com.example.gruc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {

    // Nome do banco de dados e versão
    public static final String DBNAME = "GRUC_DB.db";
    private static final int DB_VERSION = 1;

    // Construtor
    public DBHelper(Context context) {
        super(context, DBNAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        // Criação da tabela de usuários.
        // Armazenamos as datas como TEXTO (formato YYYY-MM-DD para facilitar calculos depois)
        // FOTO é armazenada como BLOB (Binário)
        MyDB.execSQL("create Table users(" +
                "cpf TEXT primary key, " +
                "nome TEXT, " +
                "email TEXT, " +
                "telefone TEXT, " +
                "senha TEXT, " +
                "eh_lider INTEGER, " + // 0 = Não, 1 = Sim
                "foto BLOB, " +
                "data_avsec TEXT, " +
                "data_cnv TEXT, " +
                "data_cred TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        // Se atualizarmos o app e mudarmos o banco, isso reseta a tabela
        MyDB.execSQL("drop Table if exists users");
        onCreate(MyDB);
    }

    // --- MÉTODOS CRUD ---

    // 1. Inserir Usuário (Cadastro)
    public Boolean insertData(String cpf, String nome, String email, String telefone, String senha, int ehLider, byte[] foto, String dAvsec, String dCnv, String dCred) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("cpf", cpf);
        contentValues.put("nome", nome);
        contentValues.put("email", email);
        contentValues.put("telefone", telefone);
        contentValues.put("senha", senha);
        contentValues.put("eh_lider", ehLider);
        contentValues.put("foto", foto); // Pode ser null se for lider

        // Se for lider, estas datas virão como null ou vazias, trataremos na lógica da tela
        contentValues.put("data_avsec", dAvsec);
        contentValues.put("data_cnv", dCnv);
        contentValues.put("data_cred", dCred);

        long result = MyDB.insert("users", null, contentValues);
        return result != -1; // Retorna verdadeiro se inseriu com sucesso
    }

    // 2. Verificar Login (CPF e Senha)
    public Boolean checkCpfPassword(String cpf, String senha) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        // Busca onde CPF e SENHA coincidem
        Cursor cursor = MyDB.rawQuery("Select * from users where cpf = ? and senha = ?", new String[]{cpf, senha});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 3. Verificar se CPF já existe (para não duplicar no cadastro)
    public Boolean checkCpf(String cpf) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where cpf = ?", new String[]{cpf});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 4. Buscar dados de um usuário específico (para preencher a Home)
    public Cursor getUserData(String cpf) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("Select * from users where cpf = ?", new String[]{cpf});
    }

    // 5. Atualizar Senha (Recuperação)
    public Boolean updatePassword(String cpf, String novaSenha) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("senha", novaSenha);
        Cursor cursor = MyDB.rawQuery("Select * from users where cpf = ?", new String[]{cpf});
        if (cursor.getCount() > 0) {
            long result = MyDB.update("users", contentValues, "cpf=?", new String[]{cpf});
            cursor.close();
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    // 6. Validar usuário para recuperação (Nome e CPF)
    public Boolean checkUserForRecovery(String nome, String cpf) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where cpf = ? and nome = ?", new String[]{cpf, nome});
        boolean match = cursor.getCount() > 0;
        cursor.close();
        return match;
    }

    // 7. Atualizar Dados do Perfil (Update)
    public Boolean updateProfile(String cpf, String telefone, String email, String dAvsec, String dCnv, String dCred) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("telefone", telefone);
        contentValues.put("email", email);
        contentValues.put("data_avsec", dAvsec);
        contentValues.put("data_cnv", dCnv);
        contentValues.put("data_cred", dCred);

        long result = MyDB.update("users", contentValues, "cpf=?", new String[]{cpf});
        return result != -1;
    }

    // Metodo auxiliar: Converter Bitmap (imagem) para Byte Array (para salvar no banco)
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    // 8. Buscar TODOS os funcionários (para a lista do Líder)
    // Importante: importar java.util.ArrayList e java.util.List;
    public java.util.List<User> getAllEmployees() {
        java.util.List<User> lista = new java.util.ArrayList<>();
        SQLiteDatabase MyDB = this.getWritableDatabase();

        // Pega todos que NÃO são líderes (eh_lider = 0)
        Cursor cursor = MyDB.rawQuery("Select * from users where eh_lider = 0", null);

        if (cursor.moveToFirst()) {
            do {
                // Pegar dados
                String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
                byte[] foto = cursor.getBlob(cursor.getColumnIndexOrThrow("foto"));
                String dAvsec = cursor.getString(cursor.getColumnIndexOrThrow("data_avsec"));
                String dCnv = cursor.getString(cursor.getColumnIndexOrThrow("data_cnv"));
                String dCred = cursor.getString(cursor.getColumnIndexOrThrow("data_cred"));

                // Criar objeto e adicionar na lista
                lista.add(new User(nome, cpf, foto, dAvsec, dCnv, dCred));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}