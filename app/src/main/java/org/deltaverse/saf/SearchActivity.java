package org.deltaverse.saf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {
	GridView gridView;
	SearchAdapter searchAdapter;
	ArrayList<Data_Holder> movieObjects;
	EditText search_text;
	TextView searching_tag;
	LinearLayout search_layout;
	ImageView clear_search, back_button;
	RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
	}
	public void init(){
		movieObjects = new ArrayList<>();
		search_layout = (LinearLayout) findViewById(R.id.search_layout_include);
		clear_search = (ImageView) findViewById(R.id.clear_button);
		back_button = (ImageView) findViewById(R.id.back_button);
		search_text = (EditText) findViewById(R.id.search_editText);
		searching_tag = (TextView) findViewById(R.id.seaching_tag);
		gridView = (GridView) findViewById(R.id.gridview);
		searchAdapter = new SearchAdapter(getApplicationContext(),movieObjects);
		gridView.setAdapter(searchAdapter);
		Toast.makeText(getApplicationContext(),String.valueOf(gridView.getWidth()),Toast.LENGTH_LONG).show();

		requestQueue = Volley.newRequestQueue(this);

		search_content_handler();
		search_clear_handler();
		scroll_listener();
		grid_item_click_listener();
	}
	public void grid_item_click_listener(){
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Intent i = new Intent(SearchActivity.this,listitem_expanded_activity.class);
					i.putExtra("data_object",movieObjects.get(position).getData_Object().toString());
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void scroll_listener(){
		gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("scrollstate",String.valueOf(scrollState));
				InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(search_text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("onscroll",String.valueOf(visibleItemCount));
			}
		});
	}



	public void search_clear_handler() {
		clear_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				search_text.setText("");
			}
		});
	}
	public void search_content_handler() {
		final Timer[] timer = new Timer[1];
		search_text.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (timer[0] != null) {
					timer[0].cancel();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// user typed: start the timer
				timer[0] = new Timer();
				timer[0].schedule(new TimerTask() {
					@Override
					public void run() {
						movieObjects.clear();
						try {
							if (search_text.getText().length() > 0) {

								search_volley(search_text.getText().toString());

							} else {
								movieObjects.clear();
								searching_tag.setVisibility(View.INVISIBLE);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										searchAdapter.notifyDataSetChanged();
									}
								});
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}, 600);

			}
		});
	}
	public void search_volley(String query) throws UnsupportedEncodingException {
		final String xquery = query;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gridView.setVisibility(View.INVISIBLE);
				searching_tag.setText("Searching for \""+xquery+"\"");
				searching_tag.setVisibility(View.VISIBLE);
			}
		});
		String url = "https://api.themoviedb.org/3/search/multi?api_key=d27b254e5920503094a9fc58d352a6fb&language=en-US&query=" + URLEncoder.encode(query, "UTF-8") + "&page=1&include_adult=false";
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new ResponseListener(), new ErrorListener());
		requestQueue.add(jsonObjectRequest);
	}

	private class ResponseListener implements Response.Listener {
		@Override
		public void onResponse(Object response) {
			try {
				JSONObject jsonObject = (JSONObject) response;
				Log.i("Result", jsonObject.toString());

				JSONArray jsonArray = jsonObject.getJSONArray("results");
				if(jsonArray.length()==0){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							searching_tag.setText("No results found");
						}
					});
				}else{
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject data_object = jsonArray.getJSONObject(i);
						String Media_Type = data_object.getString("media_type");
						if (data_object.getString("media_type").equals("movie") || data_object.getString("media_type").equals("tv")) {
							if(!data_object.getString("poster_path").equals("null")){
								movieObjects.add(new Data_Holder(data_object));
							}
						}
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								searching_tag.setVisibility(View.INVISIBLE);
								gridView.setVisibility(View.VISIBLE);
							}
						});
						Log.i("Post", Media_Type);
					}
					searchAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				e.printStackTrace();

			}
		}
	}

	private class ErrorListener implements Response.ErrorListener {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.i("result", error.toString());
		}
	}

}
