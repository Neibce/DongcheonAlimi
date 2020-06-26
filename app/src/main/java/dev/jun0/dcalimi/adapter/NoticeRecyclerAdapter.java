package dev.jun0.dcalimi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.jun0.dcalimi.R;
import dev.jun0.dcalimi.item.PostItem;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener mListener = null ;
    private final List<PostItem> mPostList;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public NoticeRecyclerAdapter(List<PostItem> list) {
        mPostList = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener ;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView uploader;
        final TextView uploadDate;
        final CardView cardview;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle) ;
            uploader = itemView.findViewById(R.id.tvUploader) ;
            uploadDate = itemView.findViewById(R.id.tvUploadDate) ;
            cardview = itemView.findViewById(R.id.materialCardView);

            cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null)
                            mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            PostItem item = mPostList.get(position);

            itemViewHolder.title.setText(item.getTitle());
            itemViewHolder.uploader.setText(item.getUploader());
            itemViewHolder.uploadDate.setText(item.getDate());

            itemViewHolder.cardview.setTransitionName("transCard" + position);
            itemViewHolder.title.setTransitionName("transTitle" + position);
            itemViewHolder.uploader.setTransitionName("transUploader" + position);
            itemViewHolder.uploadDate.setTransitionName("transUploadDate" + position);
        } else if (holder instanceof LoadingViewHolder) {
            //showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mPostList.size() ;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
