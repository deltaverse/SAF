package org.deltaverse.saf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SearchAdapter extends BaseAdapter {
	private final Context context;
	private final ArrayList<Data_Holder> movieObjects;
	SearchAdapter(Context context, ArrayList<Data_Holder> movieObjects){
		this.context = context;
		this.movieObjects = movieObjects;
	}
	@Override
	public int getCount() {
		return movieObjects.size();
	}

	@Override
	public Object getItem(int position) {
		return movieObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Data_Holder data_holder = movieObjects.get(position);
		if(convertView == null){
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.movie_item_layout,null);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image_view);
			try{
				String backdrop_url = data_holder.getData_Object().getString("poster_path");
				if(backdrop_url.length()>0){
					Picasso.get().load("https://image.tmdb.org/t/p/w500"+backdrop_url).into(imageView);

				}

			}catch (Exception e){
				e.printStackTrace();
			}
		}else{
			ImageView imageView = convertView.findViewById(R.id.item_image_view);

			try{
				String backdrop_url = data_holder.getData_Object().getString("poster_path");
				if(backdrop_url.length()>0){
					Picasso.get().load("https://image.tmdb.org/t/p/w500"+backdrop_url).into(imageView);
				}


			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return convertView;
	}
}
