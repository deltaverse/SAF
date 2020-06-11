package org.deltaverse.saf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

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
	TextView searching_tag, Movies_headline, TV_headline, My_list_headline;
	LinearLayout search_layout, popular_layout, netflix_layout, prime_layout;
	LinearLayout movie_tv_container;
	ImageView clear_search, back_button, search_icon_main, sliding_dot;
	RequestQueue requestQueue;
	Typeface typeface_light, typeface_bold;
	ConstraintLayout main_constraint_layout;
	ConstraintSet constraintSet1;
	ConstraintSet constraintSet2;
	ConstraintSet temp;
	int sliding_dot_status = 0;
	int resume_tag = 0;
	@Override
	public void onBackPressed() {
		if(clear_search.getVisibility()==View.VISIBLE){
			clear_search.performClick();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(resume_tag == -1){
			startActivity(new Intent(SearchActivity.this, SearchActivity.class));
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		init();
	}

	public void init() {
		movieObjects = new ArrayList<>();
		movie_tv_container = (LinearLayout) findViewById(R.id.movie_tv_container);
		popular_layout = (LinearLayout) findViewById(R.id.popular_layout);
		netflix_layout = (LinearLayout) findViewById(R.id.netflix_layout);
		prime_layout = (LinearLayout) findViewById(R.id.amazon_layout);
		main_constraint_layout = (ConstraintLayout) findViewById(R.id.main_layout);
		typeface_light = ResourcesCompat.getFont(getApplicationContext(), R.font.comfortaa_light);
		typeface_bold = ResourcesCompat.getFont(getApplicationContext(), R.font.comfortaa_bold);
		sliding_dot = (ImageView) findViewById(R.id.sliding_dot);
		search_icon_main = (ImageView) findViewById(R.id.search_icon_main);
		My_list_headline = (TextView) findViewById(R.id.my_list_heading);
		Movies_headline = (TextView) findViewById(R.id.movie_heading);
		TV_headline = (TextView) findViewById(R.id.tv_heading);
		search_layout = (LinearLayout) findViewById(R.id.search_layout_include);
		clear_search = (ImageView) findViewById(R.id.clear_button);
		back_button = (ImageView) findViewById(R.id.back_button);
		search_text = (EditText) findViewById(R.id.search_editText);
		searching_tag = (TextView) findViewById(R.id.seaching_tag);
		gridView = (GridView) findViewById(R.id.gridview);
		searchAdapter = new SearchAdapter(getApplicationContext(), movieObjects);
		gridView.setAdapter(searchAdapter);
		Toast.makeText(getApplicationContext(), String.valueOf(gridView.getWidth()), Toast.LENGTH_LONG).show();

		requestQueue = Volley.newRequestQueue(this);

		constraintSet1 = new ConstraintSet();
		constraintSet1.clone(main_constraint_layout);
		constraintSet2 = new ConstraintSet();
		constraintSet2.clone(main_constraint_layout);
		constraintSet2.connect(R.id.search_icon_main, ConstraintSet.START, R.id.main_layout, ConstraintSet.START, 0);
		constraintSet2.clear(R.id.search_icon_main, ConstraintSet.END);
		constraintSet2.clear(R.id.tv_heading, ConstraintSet.END);
		temp = new ConstraintSet();


		search_content_handler();
		search_clear_handler();
		scroll_listener();
		grid_item_click_listener();
		Movies_heading_click_lisener();
		TV_heading_click_listener();
		Search_icon_main_click_listener();
		My_list_heading_click_listener();
		load_popular_movies();
		load_netflix_movies();
		load_prime_movies();
	}

	public void load_popular_movies() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "movies/browse/popular-movies").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, popular_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								Picasso.get().load(img_href).into(imageView);
								popular_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void load_netflix_movies() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "movies/source/netflix?filter-sort=1").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, netflix_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								TransitionManager.beginDelayedTransition(netflix_layout);
								Picasso.get().load(img_href).into(imageView);
								netflix_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void load_prime_movies() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "movies/source/amazon?filter-sort=1").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, prime_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								Picasso.get().load(img_href).into(imageView);
								prime_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	//TO-DO : change urls in below 3 method to TV's
	public void load_popular_tv() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "tv/curated/trending-picks").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, popular_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								Picasso.get().load(img_href).into(imageView);
								popular_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void load_netflix_tv() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "tv/source/netflix").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, netflix_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								Picasso.get().load(img_href).into(imageView);
								netflix_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void load_prime_tv() {
		final LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String base_url = "https://reelgood.com";
				try {
					Document doc = Jsoup.connect(base_url+"/" + "tv/source/amazon").get();
					Element table = doc.getElementsByTag("table").get(0);
					Element table_body = table.getElementsByTag("tbody").get(0);
					Elements table_rows = table_body.getElementsByTag("tr");
					System.out.println("Table size : " + table_rows.size() + "\n");
					for (int i = 0; i < 10; i++) {
						Element table_row = table_rows.get(i);
						Element banner_root = table_row.getElementsByTag("td").get(0);
						Element link_root = banner_root.getElementsByTag("a").get(0);
						Element img_root = link_root.getElementsByTag("picture").get(0).getElementsByTag("img").get(0);
						final String img_href = img_root.attr("src");
						final String link_href = link_root.attr("href");
						System.out.println("Poster_url :" + img_href + "\n" + "On_click : " + base_url + link_href);
						final View rootview = layoutInflater.inflate(R.layout.movie_item_layout, prime_layout, false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ImageView imageView = rootview.findViewById(R.id.item_image_view);
								imageView.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(),base_url+link_href,Toast.LENGTH_SHORT).show();
										On_movie_tv_container_item_clicked(base_url+link_href);
									}
								});
								Picasso.get().load(img_href).into(imageView);
								prime_layout.addView(rootview);
							}
						});


					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void load_movies() {
		load_popular_movies();
		load_netflix_movies();
		load_prime_movies();
	}

	public void load_tv() {
		load_popular_tv();
		load_netflix_tv();
		load_prime_tv();
	}

	public void clear_all_layouts() {
		if (popular_layout != null) {
			popular_layout.removeAllViews();
		}
		if (netflix_layout != null) {
			netflix_layout.removeAllViews();
		}
		if (prime_layout != null) {
			prime_layout.removeAllViews();
		}
	}

	public void Search_icon_main_click_listener() {
		search_icon_main.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			@SuppressLint("ResourceType")
			@Override
			public void onClick(View v) {
				TransitionManager.beginDelayedTransition(main_constraint_layout);
				constraintSet2.applyTo(main_constraint_layout);
				search_icon_main.setImageResource(R.drawable.ic_search_black_24dp);
				search_icon_main.setBackground(getDrawable(R.color.transparent));
				Movies_headline.setVisibility(View.INVISIBLE);
				TV_headline.setVisibility(View.INVISIBLE);
				My_list_headline.setVisibility(View.INVISIBLE);
				sliding_dot.setVisibility(View.INVISIBLE);
				movie_tv_container.setVisibility(View.INVISIBLE);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								search_layout.setVisibility(View.VISIBLE);
							}
						});
					}
				}, 690);
			}
		});
	}

	public void Movies_heading_click_lisener() {
		Movies_headline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sliding_dot_status != 0) {
					sliding_dot_status = 0;
					TransitionManager.beginDelayedTransition(main_constraint_layout);
					Movies_headline.setTypeface(typeface_bold);
					TV_headline.setTypeface(typeface_light);
					My_list_headline.setTypeface(typeface_light);
					ConstraintSet constraintSet = new ConstraintSet();
					constraintSet.clone(main_constraint_layout);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.START, R.id.movie_heading, ConstraintSet.START);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.END, R.id.movie_heading, ConstraintSet.END);
					constraintSet.applyTo(main_constraint_layout);
					constraintSet1.clone(main_constraint_layout);
					clear_all_layouts();
					load_movies();
					movie_tv_container.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public void TV_heading_click_listener() {
		TV_headline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sliding_dot_status != 1) {
					sliding_dot_status = 1;
					TransitionManager.beginDelayedTransition(main_constraint_layout);
					TV_headline.setTypeface(typeface_bold);
					Movies_headline.setTypeface(typeface_light);
					My_list_headline.setTypeface(typeface_light);
					ConstraintSet constraintSet = new ConstraintSet();
					constraintSet.clone(main_constraint_layout);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.START, R.id.tv_heading, ConstraintSet.START);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.END, R.id.tv_heading, ConstraintSet.END);
					constraintSet.applyTo(main_constraint_layout);
					constraintSet1.clone(main_constraint_layout);
					clear_all_layouts();
					load_tv();
					movie_tv_container.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public void My_list_heading_click_listener() {
		My_list_headline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				movie_tv_container.setVisibility(View.INVISIBLE);
				if (sliding_dot_status != 2) {
					sliding_dot_status = 2;
					TransitionManager.beginDelayedTransition(main_constraint_layout);
					My_list_headline.setTypeface(typeface_bold);
					TV_headline.setTypeface(typeface_light);
					Movies_headline.setTypeface(typeface_light);
					ConstraintSet constraintSet = new ConstraintSet();
					constraintSet.clone(main_constraint_layout);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.START, R.id.my_list_heading, ConstraintSet.START);
					constraintSet.connect(R.id.sliding_dot, ConstraintSet.END, R.id.my_list_heading, ConstraintSet.END);
					constraintSet.applyTo(main_constraint_layout);
					constraintSet1.clone(main_constraint_layout);
					clear_all_layouts();
				}
			}
		});
	}


	//todo
	//Receive this data on listview_expanded_activity and display it
	public void On_movie_tv_container_item_clicked(String url){
//		Intent i = new Intent(SearchActivity.this, <To-go Activity>.class);
//		i.putExtra("url_data",url);
//		startActivity(i);
	}

	public void grid_item_click_listener() {
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Intent i = new Intent(SearchActivity.this, listitem_expanded_activity.class);
					i.putExtra("data_object", movieObjects.get(position).getData_Object().toString());
					startActivity(i);
					resume_tag = -1;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void scroll_listener() {
		gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("scrollstate", String.valueOf(scrollState));
				InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(search_text.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				Log.i("onscroll", String.valueOf(visibleItemCount));
			}
		});
	}


	public void search_clear_handler() {
		clear_search.setOnClickListener(new View.OnClickListener() {
			@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
			@Override
			public void onClick(View v) {
				search_text.setText("");
				search_layout.setVisibility(View.INVISIBLE);
				TransitionManager.beginDelayedTransition(main_constraint_layout);
				constraintSet1.applyTo(main_constraint_layout);
				Movies_headline.setVisibility(View.VISIBLE);
				TV_headline.setVisibility(View.VISIBLE);
				My_list_headline.setVisibility(View.VISIBLE);
				sliding_dot.setVisibility(View.VISIBLE);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								search_icon_main.setImageResource(R.drawable.ic_search_real_black_24dp);
								search_icon_main.setBackground(getDrawable(R.drawable.round_bg));
								switch (sliding_dot_status){
									case 0 :
									case 1 :
										Toast.makeText(getApplicationContext(),String.valueOf(sliding_dot_status),Toast.LENGTH_SHORT).show();
										movie_tv_container.setVisibility(View.VISIBLE);
										break;
									case 2 :
										Toast.makeText(getApplicationContext(),String.valueOf(sliding_dot_status),Toast.LENGTH_SHORT).show();
										break;
								}
							}
						});
					}
				}, 369);
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
				searching_tag.setText("Searching for \"" + xquery + "\"");
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
				if (jsonArray.length() == 0) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							searching_tag.setText("No results found");
						}
					});
				} else {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject data_object = jsonArray.getJSONObject(i);
						String Media_Type = data_object.getString("media_type");
						if (data_object.getString("media_type").equals("movie") || data_object.getString("media_type").equals("tv")) {
							if (!data_object.getString("poster_path").equals("null")) {
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
