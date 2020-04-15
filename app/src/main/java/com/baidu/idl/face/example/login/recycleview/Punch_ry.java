package com.baidu.idl.face.example.login.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baidu.idl.face.example.login.tools.Punch_tools;
import com.example.rnsb_start.R;

import java.util.ArrayList;
import java.util.List;

public class Punch_ry extends RecyclerView.Adapter {
    private List<Punch_tools> list=new ArrayList<>();
    private Context context;
    public Punch_ry(List<Punch_tools> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        return new ViewHolder2(LayoutInflater.from(context).inflate(R.layout.punch_ry,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder2 v2= (ViewHolder2) viewHolder;
        v2.tv1.setText(list.get(i).getName());
        if(list.get(i).getRecoding().equals("0")){
            v2.tv2.setTextColor(Color.parseColor("#FF0000"));
            v2.tv2.setText("未完成");
        }else {
            v2.tv2.setText("完成");
            v2.tv2.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class  ViewHolder2 extends RecyclerView.ViewHolder{
        private TextView tv1,tv2;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            tv1=itemView.findViewById(R.id.tv1);
            tv2=itemView.findViewById(R.id.tv2);
        }
    }
}
