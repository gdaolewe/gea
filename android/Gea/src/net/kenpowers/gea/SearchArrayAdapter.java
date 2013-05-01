package net.kenpowers.gea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchArrayAdapter extends ArrayAdapter<MusicServiceObject> {
	private Context context;
	private MusicServiceObject[] objects;
	private boolean fullNames;
	public SearchArrayAdapter(Context context, int textViewResourceId, MusicServiceObject[] objects, boolean fullNames) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.objects = objects;
	}
	
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
		 View view = convertView;
		 LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 view = inflater.inflate(R.layout.search_result, null);
		 MusicServiceObject obj = objects[position];
		 if (obj != null) {
			 TextView text = (TextView)view.findViewById(R.id.searchResultText);
			 if (text != null) {
				 if (fullNames)
					 text.setText(obj.toString());
				 else
					 text.setText(obj.getName());
			 }
			 ImageView icon = (ImageView)view.findViewById(R.id.listIcon);
			 if (icon != null) {
				 if (obj.getType().equals("track"))
					 icon.setImageResource(R.drawable.song);
				 else if (obj.getType().equals("album"))
					 icon.setImageResource(R.drawable.album);
				 else if (obj.getType().equals("artist"))
					 icon.setImageResource(R.drawable.artist);
			 }
		 }
         return view;    
	 }
	
}
