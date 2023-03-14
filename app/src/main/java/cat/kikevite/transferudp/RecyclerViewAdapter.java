package cat.kikevite.transferudp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<String> conversa;
    private List<Boolean> enviat;

    RecyclerViewAdapter(List<String> conversa, List<Boolean> enviat) {
        this.conversa = conversa;
        this.enviat = enviat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (enviat.get(position)) {
            // Missatge enviat
            holder.missatgeEnviat.setText(conversa.get(position));
            holder.missatgeEnviat.setVisibility(View.VISIBLE);
            holder.missatgeRebut.setVisibility(View.INVISIBLE);
            holder.globoEnviat.setVisibility(View.VISIBLE);
            holder.globoRebut.setVisibility(View.INVISIBLE);
        } else {
            // Missatge rebut
            holder.missatgeRebut.setText(conversa.get(position));
            holder.missatgeEnviat.setVisibility(View.INVISIBLE);
            holder.missatgeRebut.setVisibility(View.VISIBLE);
            holder.globoEnviat.setVisibility(View.INVISIBLE);
            holder.globoRebut.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return conversa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView missatgeRebut;
        private final TextView missatgeEnviat;
        private final View globoRebut;
        private final View globoEnviat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.missatgeRebut = itemView.findViewById(R.id.missatgeRebut);
            this.globoRebut = itemView.findViewById(R.id.globoRebut);
            this.missatgeEnviat = itemView.findViewById(R.id.missatgeEnviat);
            this.globoEnviat = itemView.findViewById(R.id.globoEnviat);
        }
    }
}
