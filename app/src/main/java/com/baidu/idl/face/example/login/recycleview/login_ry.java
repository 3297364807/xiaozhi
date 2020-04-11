package com.baidu.idl.face.example.login.recycleview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.idl.face.example.login.tools.login_tools;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rnsb_start.R;

import java.util.ArrayList;
import java.util.List;

public class login_ry  extends RecyclerView.Adapter {
    private List<login_tools> list=new ArrayList<>();
    private Context context;
    private String Class,name;
    public login_ry(List<login_tools> list, String Class, String name) {
        this.list=list;
        this.Class=Class;
        this.name=name;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
     ViewHolder2 viewHolder2=new ViewHolder2(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.login_ry,viewGroup,false));
        return viewHolder2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int i) {
        ViewHolder2 viewHolder2= (ViewHolder2) Holder;
        viewHolder2.login_ry_tv1.setText(list.get(i).getName());
        viewHolder2.login_ry_tv2.setText(list.get(i).getRecoding());
        viewHolder2.login_ry_tv1.setTextColor(Color.parseColor("#FF0000"));
        viewHolder2.login_ry_tv2.setTextColor(Color.parseColor("#FF0000"));
        if(list.get(i).getRecoding().equals("完成")){
            viewHolder2.login_ry_tv1.setTextColor(Color.parseColor("#000000"));
            viewHolder2.login_ry_tv2.setTextColor(Color.parseColor("#000000"));
        }
        obtain(viewHolder2.login_ry_iv1,list.get(i).getName(),name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    static class  ViewHolder2 extends RecyclerView.ViewHolder{
        private TextView login_ry_tv1,login_ry_tv2;
        private PhotoView login_ry_iv1;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            login_ry_tv1=itemView.findViewById(R.id.login_ry_tv1);
            login_ry_tv2=itemView.findViewById(R.id.login_ry_tv2);
            login_ry_iv1=itemView.findViewById(R.id.login_ry_iv1);
        }

    }
    //获取头像图片
    private void obtain(PhotoView login_ry_iv1, String image, String name) {
        String path = "http://39.106.133.87/Class/" +Class+"/"+ image + ".jpg";
        if(image.equals(name)){
            Glide.with(context).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(login_ry_iv1);
        }else {
            Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.NONE).into(login_ry_iv1);
        }
    }
}
