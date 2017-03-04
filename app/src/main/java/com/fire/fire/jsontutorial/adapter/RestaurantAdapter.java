package com.fire.fire.jsontutorial.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fire.fire.jsontutorial.R;
import com.fire.fire.jsontutorial.models.Restaurant;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by brad on 2017/02/10.
 */

public class RestaurantAdapter extends RealmRecyclerViewAdapter<Restaurant, RestaurantAdapter
        .RestaurantHolder> {

    private Activity mActivity;

    public RestaurantAdapter(@NonNull Context context, Activity activity, @Nullable
            OrderedRealmCollection<Restaurant> data) {
        super(context, data, true);
        mActivity = activity;

    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant, parent, false);
        return new RestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantHolder holder, int position) {
        Restaurant restaurant = getItem(position);

        holder.setName(restaurant.getName());
        holder.setAddress(restaurant.getAddress());
        holder.setCost("Average cost for 2: " + restaurant.getCurrency() + restaurant.getCost());
        holder.setRating(restaurant.getRating());
        //  holder.setDistance(restaurant.getDistance());

        Glide.with(mActivity)
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImageView);
    }


    public class RestaurantHolder extends RecyclerView.ViewHolder {

        ImageView restaurantImageView;
        TextView restaurantNameTextView;
        TextView restaurantAddressTextView;
        TextView restaurantRatingTextView;
        TextView costTextView;
        TextView distanceTextView;

        public RestaurantHolder(View itemView) {
            super(itemView);

            restaurantImageView = (ImageView) itemView.findViewById(R.id.imageview_restaurant);
            restaurantNameTextView = (TextView) itemView.findViewById(R.id.textview_restaurant_name);
            restaurantAddressTextView = (TextView) itemView.findViewById(R.id.restaurant_address_textview);
            restaurantRatingTextView = (TextView) itemView.findViewById(R.id.rating);
            costTextView = (TextView) itemView.findViewById(R.id.cost_for_two_textview);
            distanceTextView = (TextView) itemView.findViewById(R.id.restaurant_distance_textview);
        }

        public void setName(String name) {
            restaurantNameTextView.setText(name);
        }

        public void setAddress(String address) {
            restaurantAddressTextView.setText(address);
        }

        public void setRating(String rating) {
            restaurantRatingTextView.setText(rating);
        }

        public void setCost(String cost) {
            costTextView.setText(cost);
        }

        public void setDistance(String distance) {
            distanceTextView.setText(distance);
        }


    }
}
