package elliptical;
import java.io.IOException;

public class RunElliptic {
	public static void main(String[] args) throws IOException {
		String dataset_path = "path/to/your/activity/file.txt";
		int Method = 1; //Naive = 0, Grid = 1
		int step_size =50;
		
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
