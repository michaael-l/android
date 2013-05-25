package com.laskowski.simplegpstracker.util;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class PolyLineLatLngBoundsTuple {
	private LatLngBounds bounds;
	private PolylineOptions options;

	public LatLngBounds getBounds() {
		return bounds;
	}

	public void setBounds(LatLngBounds bounds) {
		this.bounds = bounds;
	}

	public PolylineOptions getOptions() {
		return options;
	}

	public void setOptions(PolylineOptions options) {
		this.options = options;
	}

}
