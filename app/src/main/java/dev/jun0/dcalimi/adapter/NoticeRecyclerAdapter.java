package dev.jun0.dcalimi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.jun0.dcalimi.item.NoticeListItem;
import dev.jun0.dcalimi.R;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder> {
    private OnItemClickListener mListener = null ;
    private final ArrayList<NoticeListItem> mData;

    public NoticeRecyclerAdapter(ArrayList<NoticeListItem> list) {
        mData = list ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final float scale;
        final TextView title;
        final TextView uploader;
        final TextView uploadDate;
        final CardView cardview;
        final LinearLayout.LayoutParams layoutParams;

        ViewHolder(View itemView) {
            super(itemView) ;

            scale = itemView.getResources().getDisplayMetrics().density;
            title = itemView.findViewById(R.id.title) ;
            uploader = itemView.findViewById(R.id.uploader) ;
            uploadDate = itemView.findViewById(R.id.uploadDate) ;
            cardview = itemView.findViewById(R.id.materialCardView);
            layoutParams = (LinearLayout.LayoutParams) cardview.getLayoutParams();

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

    @NonNull
    @Override
    public NoticeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_post_card, parent, false) ;
        NoticeRecyclerAdapter.ViewHolder vh = new NoticeRecyclerAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeRecyclerAdapter.ViewHolder holder, final int position) {
        NoticeListItem item = mData.get(position);

        holder.title.setText(item.getTitle());
        holder.uploader.setText(item.getUploader());
        holder.uploadDate.setText(item.getDateStr());

        holder.cardview.setTransitionName("transCard" + position);
        holder.title.setTransitionName("transTitle" + position);
        holder.uploader.setTransitionName("transUploader" + position);
        holder.uploadDate.setTransitionName("transUploadDate" + position);

        int dpAsPixels = (int) (16 * holder.scale + 0.5f);
        if (position == 0)
            holder.layoutParams.topMargin = dpAsPixels;
        else
            holder.layoutParams.topMargin = 0;
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
}
