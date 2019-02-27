package elliptical;
import java.io.IOException;

public class RunElliptic {
	
	static String dataset_path = "input/path/of/activity.csv";
	static int step_size =50; 
	static String writeFile = "path/to/naive/result.txt"; //naive result output path
	
	
	static int Method = 0;
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
