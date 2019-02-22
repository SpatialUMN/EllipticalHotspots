package elliptical;


public class HotspotResults {
	double logLR;
	double centerX;
	double centerY;
	double radiusX;
	double radiusY;
	double sumPoints;
	
	public HotspotResults(double logLR, double centerX, double centerY,double radiusX,double radiusY, double sumPoints) {
		this.logLR = logLR;
		this.centerX = centerX;
		this.centerY = centerY;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.sumPoints = sumPoints;
	}
	
	
	public static void main(String[] args) {
		
	}
}
