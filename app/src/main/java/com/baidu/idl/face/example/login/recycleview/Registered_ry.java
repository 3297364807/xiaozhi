package com.baidu.idl.face.example.login.recycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.idl.face.example.login.tools.Registered_tools;
import com.example.rnsb_start.R;

import java.util.ArrayList;
import java.util.List;

public class Registered_ry extends RecyclerView.Adapter {
    private List<Registered_tools> list=new ArrayList<>();
    private Context context;
    private EditText edit;
    public Registered_ry(List<Registered_tools> list, EditText edit) {
        this.list=list;
        this.edit=edit;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        return new ViewHolder2(LayoutInflater.from(context).inflate(R.layout.registered_ry,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder2 v2= (ViewHolder2) viewHolder;
        v2.tv.setText(list.get(i).getName());
        v2.tv.setOnClickListener(v -> {
            edit.setText(list.get(i).getName());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class  ViewHolder2 extends RecyclerView.ViewHolder{
        private TextView tv;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.registered_id);
        }
    }
}
