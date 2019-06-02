package com.example.healthy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthy.Model.Food;
import com.example.healthy.R;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements Filterable {
    private List<Food> foodList;
    private List<Food> filteredFoodList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FoodAdapter(Context context,List<Food> foodList,OnItemClickListener onItemClickListener){
        this.context = context;
        this.foodList = foodList;
        this.filteredFoodList = foodList;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textViewFoodName.setText(filteredFoodList.get(i).getFoodName());
        viewHolder.textViewFoodDescription.setText(filteredFoodList.get(i).getFoodDetails());
        viewHolder.textViewFoodCalories.setText(MessageFormat.format("Kalori : {0}", filteredFoodList.get(i).getFoodCalories()));
        Picasso.with(context)
                .load(filteredFoodList.get(i).getFoodURL())
                .placeholder(R.drawable.loading_icon)
                .into(viewHolder.imageViewFoodURL);
        viewHolder.bind(filteredFoodList.get(i),onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return filteredFoodList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<Food> filtered = new ArrayList<>();
                if (query.isEmpty()){
                    filtered = foodList;
                }else {
                    for (Food food : foodList){
                        if (food.getFoodName().toLowerCase().contains(query.toLowerCase())){
                            filtered.add(food);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredFoodList = (ArrayList<Food>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFoodName,textViewFoodDescription,textViewFoodCalories;
        ImageView imageViewFoodURL;
         ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textViewFoodName);
            textViewFoodCalories = itemView.findViewById(R.id.textViewFoodCalory);
            textViewFoodDescription = itemView.findViewById(R.id.textViewFoodDescription);
            imageViewFoodURL = itemView.findViewById(R.id.imageViewFoodURL);
        }

        void bind(final Food food, final OnItemClickListener onItemClickListener) {
             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     onItemClickListener.onItemClick(food);
                 }
             });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(Food food);
    }
}
