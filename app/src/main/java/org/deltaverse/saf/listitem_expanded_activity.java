package org.deltaverse.saf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class listitem_expanded_activity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listitem_expanded_activity);
		init();
	}
	public void init(){
		//Final url i.e., to be appended to base url
		String url = parse_json();
		Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();

	}
	public String parse_json(){
		String url="";
		try {
			JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("data_object"));
			String title, release_date;
			String media_type = "";
			media_type = jsonObject.getString("media_type");
			switch (media_type) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	public String to_url(String title, String release_date){
		String splits[] = title.split(" ");
		for(String split : splits){
			split = split.trim();
		}
		String s="";
		for(int i = 0 ; i < splits.length-1 ; i ++){
			if(splits[i].length()>0){
				splits[i]=splits[i]+"-";
				s = s+ splits[i];
			}
		}
		s = s + splits[splits.length-1];
		s = "/"+s.replaceAll(":","").toLowerCase()+"-"+release_date.split("-")[0];
		return  s;
	}
}
