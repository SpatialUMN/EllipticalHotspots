package elliptical;
import java.io.IOException;

public class RunElliptic {
	
	static String dataset_path = "input/path/of/activity.csv";
	static int Method = 0; //Naive = 0, Grid = 1
	static int step_size =50; //only used when method = 0
	static String writeFile = "path/to/naive/result.txt"; //naive result output path
	static double theta = 730; // log likelihood ratio threshold only used when method = 1
	
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
