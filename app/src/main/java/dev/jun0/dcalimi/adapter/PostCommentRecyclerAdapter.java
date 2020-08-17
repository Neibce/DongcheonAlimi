package dev.jun0.dcalimi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.item.PostCommentItem;

public class PostCommentRecyclerAdapter extends RecyclerView.Adapter<PostCommentRecyclerAdapter.ViewHolder> {
    private List<PostCommentItem> mData = null;

    public PostCommentRecyclerAdapter(List<PostCommentItem> list){
        mData = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvBody;
        final TextView tvUploader;
        final TextView tvUploadDate;

        ViewHolder(View itemView) {
            super(itemView);

            tvBody = itemView.findViewById(R.id.tvBody) ;
            tvUploader = itemView.findViewById(R.id.tvUploader) ;
            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
        }
    }

    @NonNull
    @Override
    public PostCommentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_comment, parent, false) ;
        PostCommentRecyclerAdapter.ViewHolder vh = new PostCommentRecyclerAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentRecyclerAdapter.ViewHolder holder, final int position) {
        PostCommentItem item = mData.get(position);

        holder.tvBody.setText(item.getBody());
        holder.tvUploader.setText(item.getUploader());
        holder.tvUploadDate.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}
