package elliptical;
import java.io.IOException;

public class RunElliptic {
	
	static String dataset_path = "input/path/of/activity.csv";
	static double theta = 730; // log likelihood ratio threshold 
	
	
	static int Method = 1; //Naive = 0, Grid = 1
	public static void main(String[] args) throws IOException {
		
		if (Method == 0) {
			NaiveApproach myNaiveApproach = new NaiveApproach();
			myNaiveApproach.naiveEllipseDetection(dataset_path, step_size);
		}
		else {
			
			GridApproach myGridApproach = new GridApproach();
			myGridApproach.gridEllipseDetection(dataset_path);
		}
	}

}
