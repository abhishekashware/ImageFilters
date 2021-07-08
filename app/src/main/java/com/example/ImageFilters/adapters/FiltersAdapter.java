package com.example.ImageFilters.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ImageFilters.Models.ImageFilter;
import com.example.ImageFilters.R;
import com.example.ImageFilters.listeners.ImageFilterListener;

import java.util.List;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {

    public List<ImageFilter> filters;
    public ImageFilterListener listener;
    private int selectedFilterPos=0;
    private int previousFilterPos=0;
    public FiltersAdapter(List<ImageFilter> filters,ImageFilterListener listener) {
        this.filters = filters;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.item_container_filter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                ImageFilter filter=filters.get(position);
                holder.imageView.setImageBitmap(filter.bitmap);
                holder.filterName.setText(filter.filterName);
                holder.filterName.setTextColor(ContextCompat.getColor(holder.filterName.getContext(),(selectedFilterPos==position)?R.color.purple_500:R.color.black));
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedFilterPos!=position){
                            listener.onFilterSelected(filter);
                            previousFilterPos=selectedFilterPos;
                            selectedFilterPos=position;
                            notifyItemChanged(previousFilterPos);
                            notifyItemChanged(selectedFilterPos);
                        }

                    }
                });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView filterName;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageHolder);
            filterName=itemView.findViewById(R.id.filterName);
            linearLayout=itemView.findViewById(R.id.root);
        }
    }
}
