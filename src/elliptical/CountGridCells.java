package elliptical;


import java.util.ArrayList;


public class CountGridCells {

	public ArrayList<Integer> points; // a cell contains a set of points, by their indices
	public int countGrid; // number of points
	
	public CountGridCells(){
		this.points = new ArrayList<Integer>();
		this.countGrid = 0;
	}
}