package com.teamnoname.streetartzone.StreetGroup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.Data.UserBookMarkGroup;
import com.teamnoname.streetartzone.R;
import com.teamnoname.streetartzone.Util.GlideApp;

import org.w3c.dom.Text;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class UserBookmarkGroupsActivity extends AppCompatActivity implements UserBookMarkGroupListAdapter.OnClickListener {

    private ImageView img_backBtn;
    private RecyclerView recycler_bookmarkGroup;
    private UserBookMarkGroupListAdapter adapter_bookmarkGroup;
    private RealmResults<UserBookMarkGroup> result_bookmarkGroup;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        realm = Realm.getDefaultInstance();
        getBookmarkGroupData();
        initView();
    }

    private void initView(){
        recycler_bookmarkGroup = (RecyclerView)findViewById(R.id.bookmark_recycler_group);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_bookmarkGroup.setLayoutManager(layoutManager);

        adapter_bookmarkGroup = new UserBookMarkGroupListAdapter(this,realm,this,result_bookmarkGroup,true);
        recycler_bookmarkGroup.setAdapter(adapter_bookmarkGroup);

    }

    private void getBookmarkGroupData(){
        result_bookmarkGroup = realm.where(UserBookMarkGroup.class).findAll()
                .sort("addDate", Sort.DESCENDING);
    }

    @Override
    public void onClick(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result_bookmarkGroup.get(position).deleteFromRealm();
                Log.e("itemDelete","delete!!");
            }
        });
    }
}

class UserBookMarkGroupListAdapter extends RealmRecyclerViewAdapter<UserBookMarkGroup,UserBookMarkGroupListAdapter.ItemViewHolder>{


    private Realm realm;
    private Context context;
    private OnClickListener clickListener;

    public UserBookMarkGroupListAdapter(Context context,Realm realm,OnClickListener clickListener, @Nullable OrderedRealmCollection<UserBookMarkGroup> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.realm = realm;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.bookmark_item,parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder,int position) {
        Log.e("Position", String.valueOf(position) + getData().get(position).getAddDate() ) ;
        int seq = getData().get(position).getGroupSeq();
        final GroupData data = realm.where(GroupData.class).equalTo("group_seq",seq).findFirst();

        holder.tv_groupName.setText(data.getGroup_name());
        GlideApp.with(context)
                .load(data.getGroup_titleImg())
                .roundedCorners(context,10)
                .into(holder.img_groupImage);

        switch (data.getGroup_genre()){
            case "기악":
                holder.view_groupCategory.setBackgroundColor(Color.parseColor("#14C322"));
                break;
            case "음악":
                holder.view_groupCategory.setBackgroundColor(Color.parseColor("#C40000"));
                break;
            case "전통":
                holder.view_groupCategory.setBackgroundColor(Color.parseColor("#FFE900"));
                break;
            case "퍼포먼스":
                holder.view_groupCategory.setBackgroundColor(Color.parseColor("#0B427B"));
                break;
        }






    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        ImageView img_groupImage;
        TextView tv_groupName;
        View view_groupCategory;
        ImageView img_deleteBtn;
        public ItemViewHolder(View itemView) {
            super(itemView);
            img_groupImage = (ImageView)itemView.findViewById(R.id.bookmark_item_img_group);
            tv_groupName = (TextView)itemView.findViewById(R.id.bookmark_item_tv_groupname);
            img_deleteBtn = (ImageView)itemView.findViewById(R.id.bookmark_img_delete_btn);
            view_groupCategory = (View)itemView.findViewById(R.id.bookmark_item_view_categorybar);

            img_deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    interface OnClickListener{
        void onClick(int position);
    }
}


