package github.dbr.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LogData {

	private String[] categories;
	private boolean[] enabled;
	private ArrayList<float[]> data = new ArrayList<>();
	private Color[] colors;
	
	public LogData(String[] categories) {
		this.categories = categories;
		colors = new Color[categories.length];
		enabled = new boolean[categories.length];
		Arrays.fill(enabled, true);
		for(int i = 0; i < colors.length; i++) {
			colors[i] = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
		}
	}
	
	public void addData(float[] data) {
		this.data.add(data);
	}
	
	public void addData(String[] data) {
		float[] newData = new float[data.length];
		
		for(int i = 0; i < data.length; i++) {
			newData[i] = Float.parseFloat(data[i]);
		}
		
		this.data.add(newData);
	}
	
	public int getLength() {
		return data.size();
	}
	
	public float getMin(int dataSet) {
		float min = 0;
		
		for(float[] f : data) {
			if(f[dataSet] < min) {
				min = f[dataSet];
			}
		}
		
		return min;
	};
	
	public float getMax(int dataSet) {
		float max = 0;
		
		for(float[] f : data) {
			if(f[dataSet] > max) {
				max = f[dataSet];
			}
		}
		
		return max;
	};
	
	public float get(int index, int set) {
		return data.get(index)[set];
	}
	
	public Color getColor(int index) {
		return colors[index];
	}

	public float getHighestMax() {
		float max = 0;
		for(int i = 0; i < categories.length; i++) {
			float value = getMax(i);
			if(value > max && enabled[i]) {
				max = value;
			}
		}
		return Math.max(1, (max > 0) ? max * 1.5f : max / 1.5f);
	}
	
	public float getLowestMin() {
		float min = 0;
		for(int i = 0; i < categories.length; i++) {
			float value = getMin(i);
			if(value < min && enabled[i]) {
				min = value;
			}
		}
		return Math.min(-1, (min > 0) ? min / 1.5f : min * 1.5f);
	}
	
	public int getCategoryCount() {
		return categories.length;
	}

	public boolean isEnabled(int c) {
		return enabled[c];
	}

	public void setEnabled(int index, boolean b) {
		enabled[index] = b;
	}

	public String getCategory(int i) {
		return categories[i];
	}
}
