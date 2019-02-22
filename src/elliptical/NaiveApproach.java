package elliptical;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class NaiveApproach {
	
// implements naive approach of ellipse detection
		
	//String dataset_path = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/3K.csv";
	//String dataset_path = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/ellipse_toy_2/data.csv";
	//String dataset_path = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/denver.csv";
	
	String writeFile = "/Users/tangxun/Desktop/ellipse_output.txt";


	int ctot; // amount of data points
	double studyarea_xmax = -10000000;
	double studyarea_ymax = -10000000;
	double studyarea_xmin = Double.MAX_VALUE;
	double studyarea_ymin = Double.MAX_VALUE; // range of study area;
	//double studyarea_xmax = 1000, studyarea_ymax = 1000, studyarea_xmin = 0, studyarea_ymin = 0; // range of study area;
	//double step_size = 5; // length of step discreting the parametric space
	double theta = 120; // log likelihood ratio threshold
	//ArrayList<double[]> finalLR = new ArrayList<double[]>(); // store the final results
	//ArrayList<HotspotResults> myHotspotResults = new ArrayList<HotspotResults>();
	
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
	
	int countPointsInEllipse(double center_coord_x, double center_coord_y, double radius_x_length, double radius_y_length){
		// given an ellipse, count how many points inside
		int count = 0;
		
		for (int i = 0; i < Dataset.activitySet.size(); i++){
			if ( (Math.abs(Dataset.activitySet.get(i).x - center_coord_x) <= radius_x_length) && (Math.abs(Dataset.activitySet.get(i).y - center_coord_y) <= radius_y_length) ){
				// the case that a point in inside the ellipse's bounding box
				double boundary_distance = radius_y_length * Math.sqrt(radius_x_length*radius_x_length - (Dataset.activitySet.get(i).x-center_coord_x)*(Dataset.activitySet.get(i).x-center_coord_x)) / radius_x_length;
				double actual_distance = Math.abs(Dataset.activitySet.get(i).y - center_coord_y);
				if (actual_distance <= boundary_distance){
					//tmpPointIndex.add(i); // add index of points inside the ellipse into the temporary list
					count++;
				}
			}	
		}	
		return count;
	}
	
	void naiveEllipseDetection (String dataset_path, double step_size) throws NumberFormatException, IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
		
		Dataset myDataset = new Dataset();
		ctot = myDataset.readDataset(dataset_path);
		//ArrayList<Integer> tmpPointIndex = new ArrayList<Integer>();
		
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
		
		int counter = 0;
		double studyArea = (studyarea_xmax - studyarea_xmin) * (studyarea_ymax - studyarea_ymin);
		int nBlock_x = (int)Math.ceil((studyarea_xmax - studyarea_xmin) / (double)step_size);
		int nBlock_y = (int)Math.ceil((studyarea_ymax - studyarea_ymin) / (double)step_size);

		System.out.println("Naive Approach.");
		System.out.println("Activity Set |A| = " + ctot);
		System.out.println("Study Area S = " + studyArea);
		System.out.println("Step length = " + step_size);
		System.out.println("Number of blocks ("+nBlock_x+", "+ nBlock_y+")");
		
		//long startTime1 = System.currentTimeMillis();

		double maxLR = 0;
		// main loop for enumerating all possible ellipses
		for (int center_x = 0; center_x < nBlock_x; center_x++){ // x and y coordinates of center block of the enumerated ellipse
			for (int center_y = 0; center_y < nBlock_y; center_y++){
				int limit_radius_x = Math.min(center_x, nBlock_x-center_x-1);
				int limit_radius_y = Math.min(center_y, nBlock_y-center_y-1);
				
				for (int radius_x = 1; radius_x <= limit_radius_x; radius_x++){  // radius on x axis
					for (int radius_y = 1; radius_y <= limit_radius_y; radius_y++){ // radius on y axis 
						
						//tmpPointIndex.clear(); // make the temporary list empty each iteration
			
						double center_coord_x = step_size * (center_x + 0.5); // exact coordinates of center, starting from 0
						double center_coord_y = step_size * (center_y + 0.5); 
						double radius_x_length = step_size * (radius_x + 0.5);
						double radius_y_length = step_size * (radius_y + 0.5);
						//System.out.println(center_coord_x+", "+center_coord_y+", "+radius_x_length+", "+radius_y_length);
						
						int c = countPointsInEllipse(center_coord_x, center_coord_y, radius_x_length, radius_y_length);
						double B = ctot *  Math.PI * radius_x_length * radius_y_length / studyArea;
						double logLR = logLikelihoodRatio(ctot, c, B);
						if (logLR > maxLR){
							maxLR = logLR;
						}
						
						if ((logLR >= theta)){
							bw.write("center: ("+((center_x+0.5)*step_size)+", "+((center_y+0.5)*step_size)+"), radius_x: "+ ((radius_x+0.5)*step_size) + ", radius_y: "+((radius_y+0.5)*step_size) +", count: " +c + ", B: " +B + ", logLR = " + logLR+"\n");
						}
												
						counter++; // number of enumerated ellipses in total
					}
				}
			}
		}
		System.out.println("maxLR = " + maxLR);
		bw.close();
		//long startTime2 = System.currentTimeMillis();
		//System.out.println("Naive approach: "+(startTime2-startTime1)+" milliseconds");
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		String dataset_path1 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/3K.csv";
		String dataset_path2 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/4K.csv";
		String dataset_path3 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/5K.csv";
		String dataset_path4 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/6K.csv";
		String dataset_path5 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/7K.csv";
		String dataset_path_denver = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/denver_transfer_downtown_unique.csv";
		String dataset_path_denver_downtown_random1 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown1.csv";
		String dataset_path_denver_downtown_random2 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown2.csv";
		String dataset_path_denver_downtown_random3 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown3.csv";
		String dataset_path_denver_downtown_random4 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown4.csv";
		String dataset_path_denver_downtown_random5 = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_denver_downtown/random_denver_downtown5.csv";
		String data_path_toy2 = "/Users/tangxun/Desktop/elliptical_journal/dataset/final_toy_data.csv";	
		String data_path_random20_prefix = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/random_20/random_20_";	
		String data_path_random20_suffix = ".csv";	
		String data_path_orlando_transfer = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/orlando_transfer.csv";
		String data_path_toy2_newString = "/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/ultimate_toy/final_toy_data.csv";
		
		NaiveApproach myNaiveApproach = new NaiveApproach();
		long time0 = System.currentTimeMillis();		
		/*
		myNaiveApproach.naiveEllipseDetection(dataset_path1, 3);
		long time1 = System.currentTimeMillis();
		System.out.println("naive approach 3K dataset, step_size = 3, cost = " + (time1-time0) + " milliseconds");
		
		myNaiveApproach.naiveEllipseDetection(dataset_path1, 7);
		long time2 = System.currentTimeMillis();
		System.out.println("naive approach 3K dataset, step_size = 7, cost = " + (time2-time1) + " milliseconds");

		myNaiveApproach.naiveEllipseDetection(dataset_path1, 9);
		long time3 = System.currentTimeMillis();
		System.out.println("naive approach 3K dataset, step_size = 9, cost = " + (time3-time2) + " milliseconds");
		*/
//		
//		for(int i = 1; i <= 100; i++){
//			String path = data_path_random20_prefix+i+data_path_random20_suffix;
//			myNaiveApproach.naiveEllipseDetection(path, 1);
//		}
		//myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random1, 10);
		myNaiveApproach.naiveEllipseDetection(data_path_toy2, 50);
		long time1 = System.currentTimeMillis();
		System.out.println("cost = " + (time1-time0) + " milliseconds");
		/*
		myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random1, 5);
		myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random2, 5);
		myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random3, 5);
		myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random4, 5);
		myNaiveApproach.naiveEllipseDetection(dataset_path_denver_downtown_random5, 5);
*/
		
		System.out.println("end");	
	}
}
