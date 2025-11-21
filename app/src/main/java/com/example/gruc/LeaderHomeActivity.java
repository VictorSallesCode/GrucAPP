package com.example.gruc;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LeaderHomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText etSearch;
    Button btnLogout;
    DBHelper DB;
    EmployeeAdapter adapter;
    List<User> fullList; // Lista original completa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_home);

        DB = new DBHelper(this);

        recyclerView = findViewById(R.id.recyclerViewEmployees);
        etSearch = findViewById(R.id.etSearch);
        btnLogout = findViewById(R.id.btnLeaderLogout);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Carregar dados
        fullList = DB.getAllEmployees();
        adapter = new EmployeeAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        // Lógica de Pesquisa
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Lógica de Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(LeaderHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza a lista se voltar da tela de detalhes
        fullList = DB.getAllEmployees();
        adapter = new EmployeeAdapter(this, fullList);
        recyclerView.setAdapter(adapter);
    }

    private void filter(String text) {
        List<User> filteredList = new ArrayList<>();
        for (User item : fullList) {
            // Filtra por Nome OU CPF
            if (item.getNome().toLowerCase().contains(text.toLowerCase()) ||
                    item.getCpf().contains(text)) {
                filteredList.add(item);
            }
        }
        adapter.setFilteredList(filteredList);
    }
}