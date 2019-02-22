package elliptical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Dataset {
	
	static ArrayList<Data_Point> activitySet = new ArrayList<Data_Point>(); // activitySet
	static ArrayList<Data_Point> activitySet_original = new ArrayList<Data_Point>(); // pointSet, a copy of activitySet
	
	int readDataset(String path) throws NumberFormatException, IOException{
		// read CSV dataset into two arraylists, returns number of activities
		BufferedReader br = null;
		String splitBy = ","; // numbers split by ","
		String line ="";
		
		br = new BufferedReader(new FileReader(path));
		br.readLine(); // skip first line of title
			
		activitySet.clear();
		activitySet_original.clear();
		while ((line = br.readLine()) != null) { // read dataset line by line
			if (line.length() == 0){
				continue;
			}
			String [] rowContent = line.split(splitBy);
			activitySet.add(new Data_Point(Double.parseDouble(rowContent[1]), Double.parseDouble(rowContent[2]))); // second and third element stored into point_x
		}
		br.close();
		
		for (int i = 0; i < Dataset.activitySet.size(); i++){ // duplicate the activity set
			Dataset.activitySet_original.add(Dataset.activitySet.get(i));
		}
		
		return Dataset.activitySet.size(); // return size of dataset
	}
		
	public static void main(String[] args) throws NumberFormatException, IOException {
		Dataset test = new Dataset();
		int size = test.readDataset("/Users/tangxun/Desktop/ellipse_hotspot_detection/datasets/denver_transfer_downtown_unique.csv");
		System.out.println("size: "+size);
		int sum = 0;
		/*
		for (int i = 0; i < size; i++){
			if ((Dataset.activitySet.get(i).x - 52.5) * (Dataset.activitySet.get(i).x - 52.5) + (Dataset.activitySet.get(i).y - 52.5) * (Dataset.activitySet.get(i).y - 52.5) == 12.5*12.5)
				sum++;
				//System.out.println(i+": "+Dataset.activitySet.get(i).x+" , "+Dataset.activitySet_original.get(i).x);
			//System.out.println(i+": "+Dataset.activitySet_x.get(i)+", "+", "+Dataset.activitySet_y.get(i));
		}
		*/
		
		System.out.println("sum = "+sum);
	}
}
