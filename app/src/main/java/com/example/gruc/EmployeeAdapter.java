package com.example.gruc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    Context context;
    List<User> userList;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public EmployeeAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    // Método para atualizar a lista quando pesquisamos
    public void setFilteredList(List<User> filteredList){
        this.userList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getNome());
        holder.tvCpf.setText("CPF: " + user.getCpf());

        if (user.getFoto() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getFoto(), 0, user.getFoto().length);
            holder.imgProfile.setImageBitmap(bitmap);
        }

        // --- LÓGICA DA BORDA COLORIDA ---
        // Verifica os 3 certificados e pega a pior situação
        int piorSituacao = 4; // 4=Verde, 3=Amarelo, 2=Laranja, 1=Vermelho

        piorSituacao = Math.min(piorSituacao, checkSituation(user.getDataAvsec(), 2));
        piorSituacao = Math.min(piorSituacao, checkSituation(user.getDataCnv(), 2));
        piorSituacao = Math.min(piorSituacao, checkSituation(user.getDataCred(), 1));

        // Aplica a cor baseada no pior cenário
        int corBorda = Color.GREEN; // Padrão
        String textoStatus = "Tudo OK";

        if (piorSituacao == 1) {
            corBorda = Color.RED;
            textoStatus = "Contém itens VENCIDOS";
        } else if (piorSituacao == 2) {
            corBorda = Color.parseColor("#FF9800"); // Laranja
            textoStatus = "Urgente (< 2 semanas)";
        } else if (piorSituacao == 3) {
            corBorda = Color.YELLOW;
            textoStatus = "Atenção Próxima";
        }

        holder.borderFrame.setBackgroundColor(corBorda);
        holder.tvStatus.setText(textoStatus);
        holder.tvStatus.setTextColor(corBorda);

        // --- CLIQUE NO ITEM ---
        holder.itemView.setOnClickListener(v -> {
            // Vai para a tela de Detalhes (Passo seguinte)
            Intent intent = new Intent(context, EmployeeDetailActivity.class);
            intent.putExtra("USER_CPF", user.getCpf());
            context.startActivity(intent);
        });
    }

    // Método auxiliar que retorna um número de gravidade
    private int checkSituation(String dataStr, int validadeAnos) {
        try {
            Date dataEmissao = sdf.parse(dataStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataEmissao);
            cal.add(Calendar.YEAR, validadeAnos);
            Date dataVencimento = cal.getTime();

            Date hoje = new Date();
            long diffMillies = dataVencimento.getTime() - hoje.getTime();
            long diffDias = TimeUnit.DAYS.convert(diffMillies, TimeUnit.MILLISECONDS);

            if (diffDias < 0) return 1; // Vermelho
            if (diffDias <= 14) return 2; // Laranja
            if (diffDias <= 90) return 3; // Amarelo
            return 4; // Verde
        } catch (Exception e) {
            return 4; // Se der erro, assume verde para não travar
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCpf, tvStatus;
        ImageView imgProfile;
        FrameLayout borderFrame;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvCpf = itemView.findViewById(R.id.tvItemCpf);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
            imgProfile = itemView.findViewById(R.id.imgItemProfile);
            borderFrame = itemView.findViewById(R.id.borderFrame);
        }
    }
}