package com.example.user.photocollecting.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.photocollecting.R;
import com.example.user.photocollecting.Util.Constants;
import com.example.user.photocollecting.entity.Goods;
import com.example.user.photocollecting.view.AnimationListActivity;
import com.example.user.photocollecting.view.DetailActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2015/11/30.
 */
public class LibraryListViewAdapter extends BaseAdapter{

    private Context mContext;
    private List<Goods> mGoodsList;
    private ListView mListView;

    public LibraryListViewAdapter(Context context, List<Goods> mGoodsList, ListView mListView){
        this.mContext = context;
        this.mGoodsList = mGoodsList;
        this.mListView = mListView;
        init();
    }

    private void init(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mGoodsList.get(position).getImgFile() != null ){
                    Intent intent = new Intent(mContext, AnimationListActivity.class);
                    intent.putExtra("path", mGoodsList.get(position).getImgFile().getAbsolutePath());
                    intent.putExtra("name", mGoodsList.get(position).getName());
                    mContext.startActivity(intent);
                }

            }
        });
    }

    public void setmGoodsList(List<Goods> mGoodsList) {
        this.mGoodsList = mGoodsList;
    }

    @Override
    public int getCount() {
        return mGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_library,null);
            holder = new ViewHolder();
                    /*得到各个控件的对象*/
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.lay_swipe_delete = (RelativeLayout)convertView.findViewById(R.id.lay_swipe_delete);
            holder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
            holder.mProgressBar.setMax(Constants.Num_Frame);
            holder.lay_load = (LinearLayout)convertView.findViewById(R.id.lay_load);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else{
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象                  }
        }

        Bitmap bitmap = mGoodsList.get(position).getBitmap();
        if(bitmap != null){
            holder.img.setImageBitmap(bitmap);
        }else {
            holder.img.setImageResource(R.mipmap.img_default);
        }
        holder.name.setText(mGoodsList.get(position).getName());
        if(mGoodsList.get(position).isProcessing()){
            holder.lay_load.setVisibility(View.VISIBLE);
            holder.mProgressBar.setProgress(mGoodsList.get(position).getProgress());
            holder.name.setVisibility(View.GONE);
        }else {
            holder.lay_load.setVisibility(View.GONE);
            holder.name.setVisibility(View.VISIBLE);
        }

        holder.lay_swipe_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mGoodsList.get(position).isProcessing())
                    deleteFile(mGoodsList.get(position).getName(), position);
            }
        });
        return convertView;
    }

    /**
     * 删除数据
     * @param name
     * @param position
     */
    private void deleteFile(String name, int position){
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordVideo/";// 存放照片的文件夹
        File savedir = new File(savePath+name);
        if(savedir.exists()){
            File[] files = savedir.listFiles();
            if(files.length > 0){
                for (int i=0; i<files.length; i++){
                    files[i].delete();
                }
            }
            savedir.delete();
            //刷新页面
            mGoodsList.remove(position);
            notifyDataSetChanged();
        }
    }

    final class ViewHolder {
        ImageView img;
        TextView name;
        RelativeLayout lay_swipe_delete;
        ProgressBar mProgressBar;
        LinearLayout lay_load;
    }
}
