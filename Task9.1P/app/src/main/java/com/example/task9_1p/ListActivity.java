package com.example.task9_1p;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListActivity extends Activity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv)
    RecyclerView lv;
    private MyAdapter myAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        tvTitle.setText("LOST OR FOUND ITEMS");

        List<Bean> list_all = MySqliteOpenHelper.query(ListActivity.this);
        //init listview

        @SuppressLint("WrongConstant")
        LinearLayoutManager manager = new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
        if (null == manager)
            return;
        lv.setLayoutManager(manager);
        myAdapter = new MyAdapter(ListActivity.this, list_all, R.layout.item_meal);
        myAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra("bean", list_all.get(position));
                startActivity(intent);
                onBackPressed();
            }
        });

        lv.setAdapter(myAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    class MyAdapter extends BaseRecyclerAdapter<Bean> {
        private TextView tv_name, tv_content, tv_content4;

        public MyAdapter(Context context, List<Bean> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void setView(MyRVViewHolder holder, final Bean bean, int position) {
            if (null == holder || null == bean)
                return;
            //init view

            tv_name = holder.getView(R.id.tv_name);
            tv_content = holder.getView(R.id.tv_content);
            TextView tv_content2 = holder.getView(R.id.tv_content2);
            TextView tv_content3 = holder.getView(R.id.tv_content3);
            tv_content4 = holder.getView(R.id.tv_content4);
            //set view
            tv_name.setText("Type:"+bean.type);

            tv_content.setText(
                    "Name:" + bean.value0
            );

            tv_content2.setText(
                    "Phone:" + bean.value1
            );
            tv_content3.setText(
                    "Description:" + bean.value2
            );

            tv_content4.setText("Location:"+bean.value4 + "-lat:" + bean.latitude + "-lon:" + bean.longitude);
        }
    }


    @OnClick({R.id.imgv_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgv_return:
                onBackPressed();
                break;
        }
    }

    static class MyRVViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;

        public MyRVViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
        }

        //通过viewId获取控件
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        /**
         * set TextView value
         */
        public MyRVViewHolder setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text);
            return this;
        }

        public MyRVViewHolder setImageResource(int viewId, int resId) {
            ImageView view = getView(viewId);
            view.setImageResource(resId);
            return this;
        }
    }

    abstract static class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<MyRVViewHolder> {
        public List<T> mDatas;
        private LayoutInflater mInflater;
        private int mlayoutId;
        public Context context;
        private BaseRecyclerAdapter.OnItemClickListener onItemClickListener;
        private BaseRecyclerAdapter.OnItemLongClickListener onItemLongClickListener;

        public BaseRecyclerAdapter(Context context, List<T> datas, int layoutId) {
            this.context = context;
            this.mDatas = datas;
            this.mlayoutId = layoutId;
            mInflater = LayoutInflater.from(context);
        }

        public Context getAdapterContext() {
            return context;
        }


        public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener listener) {
            this.onItemClickListener = listener;
        }


        public void setOnItemLongClickListener(BaseRecyclerAdapter.OnItemLongClickListener listener) {
            this.onItemLongClickListener = listener;
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        @Override
        public MyRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyRVViewHolder viewHolder = new MyRVViewHolder(mInflater.inflate(mlayoutId, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyRVViewHolder holder, int position) {
            setView(holder, mDatas.get(position), position);
            setUpItemEvent(holder);
        }

        public abstract void setView(MyRVViewHolder holder, T datadto, int position);


        public void setUpItemEvent(final MyRVViewHolder holder) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int layoutPosition = holder.getAdapterPosition();
                        onItemClickListener.onItemClick(holder.itemView, layoutPosition);
                    }
                });
            }
            if (null != onItemLongClickListener) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int layoutPosition = holder.getAdapterPosition();
                        onItemLongClickListener.onItemLongClick(holder.itemView, layoutPosition);
                        return true;
                    }
                });
            }
        }

        public void addData(int pos, T datas) {
            mDatas.add(pos, datas);
            notifyItemInserted(pos);
        }

        public void deleteData(int pos) {
            mDatas.remove(pos);
            notifyItemRemoved(pos);
        }


        interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        interface OnItemLongClickListener {
            void onItemLongClick(View view, int position);
        }
    }
}