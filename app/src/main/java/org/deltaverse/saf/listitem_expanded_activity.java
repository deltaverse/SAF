package org.deltaverse.saf;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class listitem_expanded_activity extends AppCompatActivity
{
    ImageView imageView,background,background3;
    TextView title,unavailable;
    TextView year;
    TextView rating;
    Button watchon;
    TextView desc;
    Button trailer;
    LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listitem_expanded_activity);
		background3 = findViewById(R.id.background3);
		unavailable = findViewById(R.id.unavailable);
		background3.setVisibility(View.GONE);
		unavailable.setVisibility(View.GONE);
		init();
	}

	public void init()
	{
		final String url = parse_json();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run()
            {
                try
                {
                    show_url_data(url);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
	}

	public String parse_json()
	{
		String url="";
		try
		{
			JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("data_object"));
			String title, release_date;
			String media_type = "";
			media_type = jsonObject.getString("media_type");
			switch (media_type)
			{
				case "tv":
					title = jsonObject.getString("name");
					release_date = jsonObject.getString("first_air_date");
					url = "/show"+to_url(title, release_date);
					break;
				case "movie":
					title = jsonObject.getString("title");
					release_date = jsonObject.getString("release_date");
					url = "/movie"+to_url(title, release_date);
					break;
				default:
					url = "";
					break;
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
		return url;
	}

	public String to_url(String title, String release_date)
	{
		String splits[] = title.split(" ");
		for(String split : splits)
		{
			split = split.trim();
			Log.i("to_url",split);

		}
		Log.i("to_url",String.valueOf(splits.length));
		String s="";
		for(int i = 0 ; i < splits.length-1 ; i ++)
		{
			if(splits[i].length()>0)
			{
				splits[i]=splits[i]+"-";
				s = s + splits[i];


			}
		}
		s = s + splits[splits.length-1];
		s = "/"+s
				.replaceAll(":","")
				.replaceAll("…","")
				.replaceAll("\\.","")
				.toLowerCase()+"-"+release_date.split("-")[0];
		return  s;
	}

	public void show_url_data(String base_url)
	{
		background = findViewById(R.id.background);
        imageView = findViewById(R.id.poster);
        title = findViewById(R.id.title);
		year = findViewById(R.id.year);
		rating = findViewById(R.id.rating);
		desc = findViewById(R.id.desc);
		watchon = findViewById(R.id.watchon);
		trailer = findViewById(R.id.trailer);
		layout = findViewById(R.id.linear);
		try
		{
			//POSTER
			Document doc = Jsoup.connect("https://reelgood.com"+base_url).get();
			Elements poster_classes = doc.getElementsByClass("css-b4kcmh e1181ybh0");
			if(poster_classes.size()!=0)
			{
				Elements poster_img = poster_classes.get(0).getElementsByTag("img");
				String poster_link = poster_img.get(0).attr("src");
				int lastIndex = poster_link.lastIndexOf('/');
				final String substr = poster_link.substring(0,lastIndex);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Picasso.get().load(substr+"/poster-780.jpg").into(imageView);
						Picasso.get().load(substr+"/poster-780.jpg").transform(new BlurTransformation(getApplicationContext(), 45, 1)).into(background);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						background3.setVisibility(View.VISIBLE);
						unavailable.setVisibility(View.VISIBLE);
					}
				});
			}

			//TITLE
			Elements title_classes = doc.getElementsByClass("css-1jw3688 e14injhv6");
			if (title_classes.size()!=0)
			{
				Elements title_h1 = title_classes.get(0).getElementsByTag("h1");
				final String title_name = title_h1.get(0).text();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						title.setText(title_name);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						title.setText("TITLE : N/A");
					}
				});
			}

			//GENRE & YEAR
			Elements genreyear_classes = doc.getElementsByClass("css-jmgx9u");
			if(genreyear_classes.size()!=0)
			{
				int i;
				StringBuilder s = new StringBuilder();
				if(genreyear_classes.size()!=2)
				{
					for (i=0;i<3;i++)
					{
						if (i!=2)
						{
							s.append(genreyear_classes.get(i).text()).append(",");
						}

						else
						{
							s.append(genreyear_classes.get(i).text());
						}
					}
				}
				else
				{
					for (i=0;i<2;i++)
					{
						if (i!=1)
						{
							s.append(genreyear_classes.get(i).text()).append(",");
						}

						else
						{
							s.append(genreyear_classes.get(i).text());
						}
					}
				}

				final StringBuilder s1 = s;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						year.setText(s1);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						year.setText("GENRE : N/A");
					}
				});
			}

			//DESCRIPTION
			Elements desc_classes = doc.getElementsByClass("css-zzy0ri e50tfam1");
			if(desc_classes.size()!=0)
			{
				Elements desc_p = desc_classes.get(0).getElementsByTag("p");
				final String description = desc_p.get(0).text();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						desc.setText(description);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						desc.setText("DESCRIPTION : N/A");
					}
				});
			}

			//IMDB & ROTTEN TOMATOES RATINGS
			Elements rating_classes = doc.getElementsByClass("css-xmin1q ey4ir3j3");
			if(rating_classes.size()!=0)
			{
				final String imdb_rating = rating_classes.get(0).text();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						rating.setText("IMDB : "+imdb_rating);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						rating.setText("IMDB : N/A");
					}
				});
			}

			//STREAMING ON
			Elements availon_classes = doc.getElementsByClass("css-3g9tm3 e1udhou113");
			if (availon_classes.size()!=0)
			{
				final String avail_on = availon_classes.get(0).text();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						watchon.setText(avail_on);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						watchon.setText("N/A");
					}
				});
			}

			//WATCH LINK
			Elements watchon_classes = doc.getElementsByClass("css-1j38j0s e126mwsw1");
			if(watchon_classes.size()!=0)
			{
				Elements watchon_a = watchon_classes.get(0).getElementsByTag("a");
				if(watchon_a.size()!=0)
				{
					final String watch_on = watchon_a.get(0).attr("href");
					watchon.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
						    goToUrl(watch_on);
						}
					});
				}
				else
				{
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							watchon.setText("N/A");
						}
					});
					watchon.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Toast. makeText(getApplicationContext(),"Watch Link Unavailable",Toast. LENGTH_SHORT).show();
						}
					});
				}
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						watchon.setText("N/A");
					}
				});
				watchon.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Toast. makeText(getApplicationContext(),"Watch Link Unavailable",Toast. LENGTH_SHORT).show();
					}
				});
			}

			//TRAILER
			Elements trailer_classes = doc.getElementsByClass("css-1cs4y7l euu2a730");
			if(trailer_classes.size()!=0)
			{
				Elements trailer_a = trailer_classes.get(1).getElementsByTag("a");
				final String trailer_link = trailer_a.get(0).attr("href");
				trailer.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						goToUrl(trailer_link);
					}
				});
			}
			else
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						trailer.setText("N/A");
					}
				});
				trailer.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						goToUrl("Trailer Link Unavailable");
					}
				});
			}

			try
			{
				//CAST & CREW
				Elements cast_classes = doc.getElementsByClass("css-1baulvz e1yfir8f5");
				LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
				for (Element e : cast_classes) {
					final View rootview = layoutInflater.inflate(R.layout.test_layout,layout,false);
					final ImageView imgView = rootview.findViewById(R.id.cast_image);
					final TextView textView = rootview.findViewById(R.id.crew_name);
					final TextView textView1 = rootview.findViewById(R.id.crew_role);

					String castimg_link = "";
					String desc_name = "";

					Elements cast_class = e.getElementsByClass("css-b4kcmh e1181ybh0");
					if(cast_class.size()!=0)
					{
						Elements cast_img = cast_class.get(0).getElementsByTag("img");
						castimg_link = cast_img.get(0).attr("src");
					}
					else
					{
						castimg_link = "https://www.abbabailbonds.com/wp-content/uploads/2019/10/User-icon.png";
					}

					Elements cast_nameclass = e.getElementsByTag("h3");
					final String cast_name = cast_nameclass.get(0).text();

					Elements cast_descclass = e.getElementsByTag("h4");
					if(cast_descclass.size()!=0)
					{
						desc_name = cast_descclass.get(0).text();
					}
					else
					{
						desc_name = "";
					}

					final String finaldesc_name = desc_name;
					final String finalCastimg_link = castimg_link;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Picasso.get().load(finalCastimg_link).transform(new CropCircleTransformation()).into(imgView);
							textView.setText(cast_name);
							textView1.setText(finaldesc_name);
							layout.addView(rootview);
						}
					});
				}
			}

			catch (Exception ignored)
			{

			}
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void goToUrl (String url) {
		Uri uriUrl = Uri.parse(url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		try
        {
			startActivity(launchBrowser);
		}
		catch (ActivityNotFoundException e)
        {
			Toast.makeText(getApplicationContext(),"Watch Link Unavailable",Toast.LENGTH_SHORT).show();
		}
	}
}
