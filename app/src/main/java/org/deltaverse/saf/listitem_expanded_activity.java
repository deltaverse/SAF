package org.deltaverse.saf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class listitem_expanded_activity extends AppCompatActivity
{
    ImageView imageView;
    TextView title;
    TextView year;
    TextView rating;
    Button watchon;
    EditText desc;
    Button trailer;
    LinearLayout layout;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listitem_expanded_activity);
		init();
	}

	public void init()
	{
		//Final url i.e., to be appended to base url
		final String url = parse_json();
		//Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
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
		}
		String s="";
		for(int i = 0 ; i < splits.length-1 ; i ++)
		{
			if(splits[i].length()>0)
			{
				splits[i]=splits[i]+"-";
				s = s+ splits[i];
			}
		}
		s = s + splits[splits.length-1];
		s = "/"+s.replaceAll(":","").toLowerCase()+"-"+release_date.split("-")[0];
		return  s;
	}

	public void show_url_data(String base_url)
	{
        imageView = findViewById(R.id.poster);
        title = findViewById(R.id.title);
		year = findViewById(R.id.year);
		rating = findViewById(R.id.rating);
		desc = findViewById(R.id.desc);
		watchon = findViewById(R.id.watchon);
		trailer = findViewById(R.id.trailer);
		layout = findViewById(R.id.linear);
		//desc.setEnabled(false);
		desc.setFocusable(false);
		try
		{
			//TITLE
			Document doc = Jsoup.connect("https://reelgood.com"+base_url).get();
			Elements title_classes = doc.getElementsByClass("css-1jw3688 e14injhv6");
			Elements title_h1 = title_classes.get(0).getElementsByTag("h1");
			final String title_name = title_h1.get(0).text();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					title.setText(title_name);
				}
			});

			//GENRE & YEAR
			Elements genreyear_classes = doc.getElementsByClass("css-jmgx9u");
			int i;
			StringBuilder s = new StringBuilder();
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
			year.setText(s);

			//DURATION (NOT ABLE TO IMPLEMENT COZ INDEX IS DIFFERENT FOR DIFFERENT MOVIES)


			//DESCRIPTION
			Elements desc_classes = doc.getElementsByClass("css-zzy0ri e50tfam1");
			Elements desc_p = desc_classes.get(0).getElementsByTag("p");
			final String description = desc_p.get(0).text();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					desc.setText(description);
				}
			});

			//IMDB & ROTTEN TOMATOES RATINGS
			Elements rating_classes = doc.getElementsByClass("css-xmin1q ey4ir3j3");
			final String imdb_rating = rating_classes.get(0).text();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					rating.setText("IMDB : "+imdb_rating);
				}
			});

			//POSTER
			Elements poster_classes = doc.getElementsByClass("css-b4kcmh e1181ybh0");
			Elements poster_img = poster_classes.get(0).getElementsByTag("img");
			String poster_link = poster_img.get(0).attr("src");
			int lastIndex = poster_link.lastIndexOf('/');
			final String substr = poster_link.substring(0,lastIndex);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Picasso.get().load(substr+"/poster-780.jpg").into(imageView);
				}
			});

			//STREAMING ON
			Elements availon_classes = doc.getElementsByClass("css-3g9tm3 e1udhou113");
			final String avail_on = availon_classes.get(0).text();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					watchon.setText(avail_on);
				}
			});

			//WATCH LINK
			Elements watchon_classes = doc.getElementsByClass("css-1j38j0s e126mwsw1");
			Elements watchon_a = watchon_classes.get(0).getElementsByTag("a");
			final String watch_on = watchon_a.get(0).attr("href");
			watchon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					goToUrl(watch_on);
				}
			});

			//TRAILER
			Elements trailer_classes = doc.getElementsByClass("css-1cs4y7l euu2a730");
			Elements trailer_a = trailer_classes.get(1).getElementsByTag("a");
			final String trailer_link = trailer_a.get(0).attr("href");
			trailer.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					goToUrl(trailer_link);
				}
			});

			try
			{
				//CAST & CREW
				Elements cast_classes = doc.getElementsByClass("css-1baulvz e1yfir8f5");
				for (Element e : cast_classes) {
					Elements cast_class = e.getElementsByClass("css-b4kcmh e1181ybh0");
					Elements cast_img = cast_class.get(0).getElementsByTag("img");
					String castimg_link = cast_img.get(0).attr("src");
					ImageView imgView = new ImageView(this);
					imgView.setId(i);
					imgView.setPadding(2, 2, 2, 2);
					Picasso.get().load(castimg_link).into(imgView);
					layout.addView(imgView);
					//System.out.println("cast image: " + castimg_link);
					Elements cast_nameclass = e.getElementsByTag("h3");
					String cast_name = cast_nameclass.get(0).text();
					//System.out.println("cast name: " + cast_name);
					Elements cast_descclass = e.getElementsByTag("h4");
					String desc_name = cast_descclass.get(0).text();
					//System.out.println("cast role: " + desc_name);
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
		startActivity(launchBrowser);
	}
}
