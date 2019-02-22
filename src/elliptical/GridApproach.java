package elliptical;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PrimitiveIterator.OfDouble;
import java.util.concurrent.CountedCompleter;

import javax.naming.LimitExceededException;


public class GridApproach {
	
	//String dataset_path = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/denver_transfer.csv";
	//String dataset_path = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random3K/random3K1.csv";
	
	int ctot; // amount of data points
	double studyarea_xmax = -1000000;
	double studyarea_ymax = -1000000;
	double studyarea_xmin = Double.MAX_VALUE;
	double studyarea_ymin = Double.MAX_VALUE; // range of study area;
	int nBlock_x, nBlock_y;
	//double studyarea_xmax = 1000, studyarea_ymax = 1000, studyarea_xmin = 0, studyarea_ymin = 0; // range of study area;
	double step_size = 2.5; // length of step discreting the parametric space
	double theta = 1000; // log likelihood ratio threshold	
	ArrayList<int[]> boundaryCell = new ArrayList<int[]>(); // given two radius, store the index of boundary cells, one size need one boundryCell at most
	//ArrayList<double[]> finalLR = new ArrayList<double[]>(); // store the final results
	
	void computeBoundaryCells(int radius_x, int radius_y) {
		// compute the relative coordinates of boundary cells given two radius (use the center cell as the origin of axis)
		// only 1/4 of the cells are computed and stored
		// border cell index are put into "boundaryCell", the size of that arraylist changes through each different radius_y
		// loop along the y axis (i.e., row by row)
		boundaryCell.clear();
		int border_last = radius_x;
		boolean flagLast = false; // true if first boundary in each row, used to set the border_last

		for (int i = 1; i <= radius_y; i++){
			//System.out.println(i);
			flagLast = true;
			for (int j = border_last; j >= 1; j--){ // loop each row from far-most to near-most
				//System.out.println("border_last = "+border_last);
				if ( ((radius_y+0.5)*Math.sqrt((radius_x+0.5)*(radius_x+0.5) - (j-0.5)*(j-0.5))/(radius_x+0.5) > (i-0.5)) // bottom left constraint
						&& ((radius_y+0.5)*Math.sqrt((radius_x+0.5)*(radius_x+0.5) - (j+0.5)*(j+0.5))/(radius_x+0.5) < (i+0.5)) ){// up right constraint
					// found a boundary cell using bottom-left and up-left corners
					int[] cellIndex = new int[2];
					cellIndex[0] = j;
					cellIndex[1] = i;
					//cellIndex[0] = new int(j);
					//cellIndex[1] = new Double(i);
					boundaryCell.add(cellIndex);
					if (flagLast == true){
						border_last = j; // border set to the farthest boundary cell of the previous row
						flagLast = false;
					}
				}
				else if ((radius_y+0.5)*Math.sqrt((radius_x+0.5)*(radius_x+0.5) - (j+0.5)*(j+0.5))/(radius_x+0.5) >= (i+0.5)){ // find a "inside cell"
					break; // find cells completely inside the ellipse if the second condition (up-right corner) violated
				}
			}
		}
		// the case of the "axis row and column", since i, j go between 1 not 0 to border
		
		// first, do x-axis
		int x = (int) Math.ceil((radius_x+0.5) * Math.sqrt((radius_y+0.5)*(radius_y+0.5)-0.25) / (radius_y+0.5) - 0.5);
		for (int i = x; i <= radius_x; i++){
			int[] cellIndex = new int[2];
			cellIndex[0] = i;
			cellIndex[1] = 0;
			boundaryCell.add(cellIndex);
		}
		
		// then, do y-axis
		int y = (int) Math.ceil((radius_y+0.5) * Math.sqrt((radius_x+0.5)*(radius_x+0.5)-0.25) / (radius_x+0.5) - 0.5);
		for (int i = y; i <= radius_y; i++){		
			int[] cellIndex = new int[2];
			cellIndex[0] = 0;
			cellIndex[1] = i;
			boundaryCell.add(cellIndex);
		}
	}
	
	double computeExactLogLR(int radius_x, int radius_y, int center_x, int center_y, LookUpTable2D myLookUpTable2D, CountGridCells[][] countGridCells, double B){
		// given a rectangle (indicated by parameters), compute the log likelihood ratio of the ellipse bounded
		int []endOfInside; // the cells completely inside the ellipse is from index from 0 to endOfInside[xx]
		int temp;
		int sum = 0;
		double logLR = 0;
		int nPointsBoundaryCell; // number of points inside a boundary cell
		
		/*
		System.out.println("radius_x = "+radius_x+" radius_y = "+radius_y);
		for (int i = 0; i < boundaryCell.size(); i++){
			System.out.println("("+boundaryCell.get(i)[0]+", "+boundaryCell.get(i)[1]+")");
		}
		*/
		
		if (radius_x >= radius_y){ // major axis is on "x", go through "y" is faster
			endOfInside = new int[radius_y+1];
			
			for (int i = 0; i < radius_y+1; i++){
				endOfInside[i] = Integer.MAX_VALUE;
			}
			for (int i = 0; i < boundaryCell.size(); i++){
				temp = boundaryCell.get(i)[0];
				if (temp < endOfInside[boundaryCell.get(i)[1]]){
					endOfInside[boundaryCell.get(i)[1]] = temp;
				}
			}
			for (int i = 0; i < radius_y+1; i++){
				endOfInside[i]--;
				/*
				if ((radius_x == 2) && (radius_y == 2))
					System.out.println("endofInside["+i+"] = "+endOfInside[i]);
				*/
			}
			for (int i = 1; i < radius_y+1; i++) { // be cautious about the overlapping area (x and y axes)
				if (endOfInside[i] >= 0){
					//System.out.println("iiii = "+i);
					sum += myLookUpTable2D.getRegionSum(center_x-endOfInside[i], center_y-i, center_x+endOfInside[i], center_y-i);
					sum += myLookUpTable2D.getRegionSum(center_x-endOfInside[i], center_y+i, center_x+endOfInside[i], center_y+i);	
				}
			}
			sum += myLookUpTable2D.getRegionSum(center_x-endOfInside[0], center_y, center_x+endOfInside[0], center_y);
		}
		
		else { // the other case (i.e., major axis is on "y")
			endOfInside = new int[radius_x+1];
			
			for (int i = 0; i < radius_x+1; i++){
				endOfInside[i] = Integer.MAX_VALUE;
			}
			for (int i = 0; i < boundaryCell.size(); i++){
				temp = boundaryCell.get(i)[1];
				if (temp < endOfInside[boundaryCell.get(i)[0]]){
					endOfInside[boundaryCell.get(i)[0]] = temp;
				}
			}
			for (int i = 0; i < radius_x; i++){
				endOfInside[i]--;
			}
			for (int i = 1; i < radius_x+1; i++){
				if (endOfInside[i] >= 0){
					sum += myLookUpTable2D.getRegionSum(center_x-i, center_y-endOfInside[i], center_x-i, center_y+endOfInside[i]);
					sum += myLookUpTable2D.getRegionSum(center_x+i, center_y-endOfInside[i], center_x+i, center_y-endOfInside[i]);
				}
			}
			sum += myLookUpTable2D.getRegionSum(center_x, center_y-endOfInside[0], center_x, center_y+endOfInside[0]);
		}
		// By far, points completely inside have been computed
		//System.out.println("sum of completely bounded cells = "+sum);
		// check those points inside boundaryCell
		//System.out.println("boundary cell size: "+ boundaryCell.size());

		for (int i = 0; i < boundaryCell.size(); i++){
			// up right quarter
			if (boundaryCell.get(i)[1] != 0) {
				nPointsBoundaryCell = countGridCells[center_x+boundaryCell.get(i)[0]][center_y+boundaryCell.get(i)[1]].countGrid;
				for (int j = 0; j < nPointsBoundaryCell; j++){
					if (ifInsideEllipse(center_x, center_y, radius_x, radius_y,
							Dataset.activitySet.get( countGridCells[center_x+boundaryCell.get(i)[0]][center_y+boundaryCell.get(i)[1]].points.get(j) ) ) == true){
						sum++; // a point is inside an ellipse
					}
				}
			}
		}	
		for (int i = 0; i < boundaryCell.size(); i++) {
			// up left quarter
			if (boundaryCell.get(i)[1] != 0){
				nPointsBoundaryCell = countGridCells[center_x-boundaryCell.get(i)[0]][center_y+boundaryCell.get(i)[1]].countGrid;
				for (int j = 0; j < nPointsBoundaryCell; j++){
					if (ifInsideEllipse(center_x, center_y, radius_x, radius_y,
						Dataset.activitySet.get( countGridCells[center_x-boundaryCell.get(i)[0]][center_y+boundaryCell.get(i)[1]].points.get(j) ) ) == true){
						sum++; // a point is inside an ellipse
					}
				}
			}
		}
		for (int i = 0; i < boundaryCell.size(); i++) {
			// bottom right quarter
			if (boundaryCell.get(i)[0] != 0){
				nPointsBoundaryCell = countGridCells[center_x+boundaryCell.get(i)[0]][center_y-boundaryCell.get(i)[1]].countGrid;
				for (int j = 0; j < nPointsBoundaryCell; j++){
					if (ifInsideEllipse(center_x, center_y, radius_x, radius_y,
							Dataset.activitySet.get( countGridCells[center_x+boundaryCell.get(i)[0]][center_y-boundaryCell.get(i)[1]].points.get(j) ) ) == true){
						sum++; // a point is inside an ellipse
					}
				}
			}
		}
		for (int i = 0; i < boundaryCell.size(); i++) {
			// bottom left quarter
			if (boundaryCell.get(i)[1] != 0){
				nPointsBoundaryCell = countGridCells[center_x-boundaryCell.get(i)[0]][center_y-boundaryCell.get(i)[1]].countGrid;
				for (int j = 0; j < nPointsBoundaryCell; j++){
					if (ifInsideEllipse(center_x, center_y, radius_x, radius_y,
							Dataset.activitySet.get( countGridCells[center_x-boundaryCell.get(i)[0]][center_y-boundaryCell.get(i)[1]].points.get(j) ) ) == true){
						sum++; // a point is inside an ellipse
					}
				}
			}
		}	
		
		logLR = logLikelihoodRatio(ctot, (double)sum, B);
		if (logLR >= theta){
			//System.out.println("logLR = "+logLR+" center = ("+((center_x+0.5)*step_size)+", "+((center_y+0.5)*step_size)+"), radius = ("+((radius_x+0.5)*step_size)+", "+((radius_y+0.5)*step_size)+") B = "+B+" sum = "+sum);
			//System.out.println("logLR = "+logLR+" center = ("+((center_x+0.5)*step_size*0.4356822/1000-105.1093359)+", "+((center_y+0.5)*step_size*0.2469863/1000+39.6157503)+"), radius = ("+((radius_x+0.5)*step_size*0.4356822/1000)+", "+((radius_y+0.5)*step_size*0.2469863/1000)+") B = "+B+" sum = "+sum);

			//System.out.println("found one!");
		}
		return logLR;
	}
	
	boolean ifInsideEllipse (int center_x, int center_y, int radius_x, int radius_y, Data_Point point){
		// given an ellipse and a point, check if the point is inside the ellipse
		double xShift = point.x - (center_x+0.5) * step_size;
		double yShift = point.y - (center_y+0.5) * step_size;
		double onEllipseSquare = (radius_y+0.5)*(radius_y+0.5)*step_size*step_size * (1-xShift*xShift / ( (radius_x+0.5)*(radius_x+0.5)*step_size*step_size ) );
		if (onEllipseSquare >= yShift*yShift) { // if point is really inside the ellipse
			return true;
		}
		else {
			return false;
		}
	}
	
	double logLikelihoodRatio(int ctot, double c, double B){
		// given a c and B, compute the log likelihood ratio
		
		double logc = Math.log(c);
		double logb = Math.log(B);
		double logctotc = Math.log((double)(ctot-c));
		double logctotb = Math.log((double)(ctot-B));
		double logLR;
		
		if (c > B){
			logLR = c * (logc - logb) + (double)(ctot - c) * (logctotc-logctotb);
		}
		else{
			logLR = 0;
		}
		return logLR;
	}
	
	void gridEllipseDetection(String dataset_path) throws NumberFormatException, IOException{
		
		Dataset myDataset = new Dataset();
		ctot = myDataset.readDataset(dataset_path); // get number of points
		LookUpTable2D myLookUpTable2D = new LookUpTable2D();
		
		for (int i = 0; i < ctot; i++){
			if (Dataset.activitySet.get(i).x > studyarea_xmax){
				studyarea_xmax = Dataset.activitySet.get(i).x;
			}
			if (Dataset.activitySet.get(i).x < studyarea_xmin){
				studyarea_xmin = Dataset.activitySet.get(i).x;
			}
			if (Dataset.activitySet.get(i).y > studyarea_ymax){
				studyarea_ymax = Dataset.activitySet.get(i).y;
			}
			if (Dataset.activitySet.get(i).y < studyarea_ymin){
				studyarea_ymin = Dataset.activitySet.get(i).y;
			}
		}
		double studyArea = (studyarea_xmax - studyarea_xmin) * (studyarea_ymax - studyarea_ymin);
		
		nBlock_x = (int)Math.round((studyarea_xmax - studyarea_xmin) / (double)step_size);
		nBlock_y = (int)Math.round((studyarea_ymax - studyarea_ymin) / (double)step_size);
		
		System.out.println("Grid-based Approach.");
		System.out.println("Activity Set |A| = " + ctot);
		System.out.println("Study Area S = " + studyArea);
		System.out.println("Step length = " + step_size);
		System.out.println("Number of blocks ("+nBlock_x+", "+ nBlock_y+")");
		
		long startTime1 = System.currentTimeMillis();

		
		//double cellArea = step_size * step_size;
		CountGridCells[][] countGridCells = new CountGridCells[nBlock_x][nBlock_y]; // a matrix of geo-grid
		for (int i = 0; i < nBlock_x; i++) { 
			for (int j = 0; j < nBlock_y; j++) {
				countGridCells[i][j] = new CountGridCells();
			}
		}
				
		for (int i = 0; i < ctot; i++){
			int xCoord = (int)Math.floor((Dataset.activitySet.get(i).x - studyarea_xmin) / step_size); // which cell a point goes to
			int yCoord = (int)Math.floor((Dataset.activitySet.get(i).y - studyarea_ymin) / step_size);
			//System.out.println("i:" + i+" xCoord = "+xCoord+" yCoord = "+yCoord+" x= "+Dataset.activitySet.get(i).x+", y= "+Dataset.activitySet.get(i).y);

			if (xCoord >= nBlock_x)
				xCoord--;
			if (yCoord >= nBlock_y)
				yCoord--;

			countGridCells[xCoord][yCoord].points.add(i); // grid cell objects only store index of points
			countGridCells[xCoord][yCoord].countGrid++;
		}
		
		myLookUpTable2D.buildLookUpTable(countGridCells, nBlock_x, nBlock_y); // build a 2-D lookup table for
		
		double upperBoundLR; // upper bound of a given rectangle
		double c; // count of points inside a rectangle
		boolean flagBoundaryAlreadyGot = false; // a flag that labels if the boundary of (radius_x, radius_y) is computed
		double exactLogLR;
		double maxLR = 0;
		
		// ----------- main part of the algorithm, nested loop
		for (int radius_x = 1; radius_x <= Math.ceil((double)nBlock_x/2)-1; radius_x++){
			for (int radius_y = 1; radius_y <= Math.ceil((double)nBlock_y/2)-1; radius_y++){
				flagBoundaryAlreadyGot = false;
				// two-layer loop for ellipse size, inside loop for center
				double B = ctot * Math.PI * (radius_x+0.5) * (radius_y+0.5) * step_size * step_size / studyArea;
				for (int center_x = radius_x; center_x <= nBlock_x-radius_x-1; center_x++){
					for (int center_y = radius_y; center_y <= nBlock_y-radius_y-1; center_y++){
						c = myLookUpTable2D.getRegionSum(center_x-radius_x, center_y-radius_y, center_x+radius_x, center_y+radius_y);
						upperBoundLR = logLikelihoodRatio(ctot, c, B);
						if (upperBoundLR >= theta) { // once the upperbound is larger than the theta, check the exact loglikelihood
							//System.out.println("upperbound c = "+c);

							if (flagBoundaryAlreadyGot == false){
								computeBoundaryCells(radius_x, radius_y);
								flagBoundaryAlreadyGot = true;
							}
							exactLogLR = computeExactLogLR(radius_x, radius_y, center_x, center_y, myLookUpTable2D, countGridCells, B);
							
							/*
							if (exactLogLR >= theta){
								if (exactLogLR > maxLR){
									maxLR = exactLogLR;
								}
								//System.out.println("logLR = "+exactLogLR+" center = ("+center_x+", "+center_y+"), radius = ("+radius_x+", "+radius_y+") B = "+B);
								//System.out.println("logLR = "+logLR+" center = ("+((center_x+0.5)*step_size)+", "+((center_y+0.5)*step_size)+"), radius = ("+((radius_x+0.5)*step_size)+", "+((radius_y+0.5)*step_size)+") B = "+B+" sum = "+sum);
							}
							*/
						}
					}
				}
			}
		}
		long startTime2 = System.currentTimeMillis();
		//System.out.print(" maxLR = " + maxLR);
		//if (maxLR >= theta){
			//System.out.println("maxLR is as big as " + maxLR);
		//}
		System.out.println("Grid based approach: "+(startTime2-startTime1)+" milliseconds");
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {

		String dataset_path1 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/3K.csv";
		String dataset_path2 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/30K.csv";
		String dataset_path3 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/40K.csv";
		String dataset_path4 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/50K.csv";
		String dataset_path5 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/60K.csv";
		String dataset_path6 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/70K.csv";

		
		GridApproach myGridApproach = new GridApproach();
		myGridApproach.gridEllipseDetection(dataset_path1);
		/*
		for (int i = 11; i <= 100 ; i++){
			
			myGridApproach.gridEllipseDetection("/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown"+i+".csv");

		}
		*/
	}

}
