package com.baidu.idl.face.example.login.recycleview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.idl.face.example.login.tools.Registered_tools;
import com.example.rnsb_start.R;

import java.util.ArrayList;
import java.util.List;

public class Copy_ry extends RecyclerView.Adapter {
    private List<Registered_tools> list=new ArrayList<>();
    private Context context;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        return new login_ry.ViewHolder2(LayoutInflater.from(context).inflate(R.layout.registered_ry,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder2 v2= (ViewHolder2) viewHolder;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class  ViewHolder2 extends RecyclerView.ViewHolder{
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
        }
    }
}
