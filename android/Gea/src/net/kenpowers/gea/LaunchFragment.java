package net.kenpowers.gea;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class LaunchFragment extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result=inflater.inflate(R.layout.launch_fragment, container, false);
		return result;
	}
}
