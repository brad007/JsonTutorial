package com.fire.fire.jsontutorial.ui.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fire.fire.jsontutorial.R;
import com.fire.fire.jsontutorial.adapter.RestaurantAdapter;
import com.fire.fire.jsontutorial.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRestaurantRecyclerView;
    private RestaurantAdapter mAdapter;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        new FetchDataTask().execute();
    }

    private void init() {
        mRestaurantRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler);
        mRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRestaurantRecyclerView.setHasFixedSize(true);
        mRealm = Realm.getDefaultInstance();
        mAdapter = new RestaurantAdapter(MainActivity.this, MainActivity.this, mRealm.where
                (Restaurant.class).findAllAsync());
        mRestaurantRecyclerView.setAdapter(mAdapter);
    }

    public class FetchDataTask extends AsyncTask<Void, Void, Void> {

        private String mZomatoString;
        private Realm mRealm;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRealm = Realm.getDefaultInstance();
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Uri builtUri = Uri.parse(getString(R.string.zomato_api));
            URL url;
            try {
                url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key", "acfd3e623c5f01289bd87aaaff1926c1");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    //Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }


                mZomatoString = buffer.toString();
                JSONObject jsonObject = new JSONObject(mZomatoString);

                Log.v("response", jsonObject.toString());

                JSONArray restaurantsArray = jsonObject.getJSONArray("restaurants");

                final ArrayList<Restaurant> restaurantCollection = new ArrayList<>();
                //list = new ArrayList<>();
                for (int i = 0; i < restaurantsArray.length(); i++) {

                    Log.v("BRAD_", i + "");
                    String name;
                    String address;
                    String currency;
                    String imageUrl;
                    long lon;
                    long lat;
                    long cost;
                    float rating;


                    JSONObject jRestaurant = (JSONObject) restaurantsArray.get(i);
                    jRestaurant = jRestaurant.getJSONObject("restaurant");
                    JSONObject jLocattion = jRestaurant.getJSONObject("location");
                    JSONObject jRating = jRestaurant.getJSONObject("user_rating");


                    name = jRestaurant.getString("name");
                    address = jLocattion.getString("address");
                    lat = jLocattion.getLong("latitude");
                    lon = jLocattion.getLong("longitude");
                    currency = jRestaurant.getString("currency");
                    cost = jRestaurant.getInt("average_cost_for_two");
                    imageUrl = jRestaurant.getString("thumb");
                    rating = (float) jRating.getDouble("aggregate_rating");


                    Restaurant restaurant = new Restaurant();
                    restaurant.setName(name);
                    restaurant.setAddress(address);
                    restaurant.setLatitude(lat);
                    restaurant.setLongitude(lon);
                    restaurant.setCurrency(currency);
                    restaurant.setCost(String.valueOf(cost));
                    restaurant.setImageUrl(imageUrl);
                    restaurant.setRating(String.valueOf(rating));

                    restaurantCollection.add(restaurant);
                }
                final ArrayList<Restaurant> restaurantCopy = restaurantCollection;
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(restaurantCopy);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
