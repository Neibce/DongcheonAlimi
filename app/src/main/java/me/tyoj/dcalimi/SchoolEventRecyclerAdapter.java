package me.tyoj.dcalimi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SchoolEventRecyclerAdapter extends RecyclerView.Adapter<SchoolEventRecyclerAdapter.ViewHolder> {
    private ArrayList<SchoolEventListItem> mData = null ;

    SchoolEventRecyclerAdapter(ArrayList<SchoolEventListItem> list) {
        mData = list ;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        float scale;
        TextView date;
        TextView day;
        TextView event;
        CardView card;
        LinearLayout.LayoutParams layoutParams;

        ViewHolder(View itemView) {
            super(itemView) ;

            scale = itemView.getResources().getDisplayMetrics().density;
            date = itemView.findViewById(R.id.date) ;
            day = itemView.findViewById(R.id.day) ;
            event = itemView.findViewById(R.id.event) ;
            card = itemView.findViewById(R.id.materialCardView);
            layoutParams = (LinearLayout.LayoutParams) card.getLayoutParams();
        }
    }

    @NonNull
    @Override
    public SchoolEventRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_event_card, parent, false) ;
        SchoolEventRecyclerAdapter.ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolEventRecyclerAdapter.ViewHolder holder, final int position) {
        SchoolEventListItem item = mData.get(position);

        holder.date.setText(item.getDate());
        holder.day.setText(item.getDay());
        holder.event.setText(item.getEvent());

        if (position == 0)
            holder.layoutParams.topMargin = (int) (16 * holder.scale + 0.5f);
        else
            holder.layoutParams.topMargin = 0;
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}
