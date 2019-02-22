package elliptical;

import javax.security.auth.x500.X500Principal;


public class LookUpTable2D {
// a 2 dimensional look up table
	int [][] lookUpTable;
	int size_x;
	int size_y;
	
	void buildLookUpTable(CountGridCells [][] countGridCells, int size_x, int size_y){
		
		lookUpTable = new int[size_x][size_y];
		this.size_x = size_x;
		this.size_y = size_y;
		
		for (int i = 0; i < size_x; i++){
			for (int j = 0; j < size_y; j++){
				if (i == 0){
					lookUpTable[i][j] = countGridCells[i][j].countGrid;
				}
				else {
					lookUpTable[i][j] = lookUpTable[i-1][j] + countGridCells[i][j].countGrid;
				}
			}
		}
		for (int i = 0; i < size_x; i++){
			for (int j = 0; j < size_y; j++){
				if (j == 0){
					;
				}
				else {
					lookUpTable[i][j] = lookUpTable[i][j-1] + lookUpTable[i][j];
				}
			}
		}
	}
	
	int getRegionSum (int leftBottom_x, int leftBottom_y, int rightUp_x, int rightUp_y){
		// retrive information from the lookup table, suppose left-bottom is the origin
		if (leftBottom_x == 0){
			if (leftBottom_y == 0){
				return lookUpTable[rightUp_x][rightUp_y];
			}
			else {
				return lookUpTable[rightUp_x][rightUp_y] - lookUpTable[rightUp_x][leftBottom_y-1];
			}
		}
		else {
			if (leftBottom_y == 0){
				return lookUpTable[rightUp_x][rightUp_y] - lookUpTable[leftBottom_x-1][rightUp_y];
			}
			else {
				return lookUpTable[rightUp_x][rightUp_y] + lookUpTable[leftBottom_x-1][leftBottom_y-1]
						- lookUpTable[rightUp_x][leftBottom_y-1] - lookUpTable[leftBottom_x-1][rightUp_y];
			}
		}
	}
	
	public static void main(String[] args) {
		LookUpTable2D myLookUpTable2D = new LookUpTable2D();
		CountGridCells [][] myCountGridCells = new CountGridCells[5][5];
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				myCountGridCells[i][j] = new CountGridCells();
			}
		}
		
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				myCountGridCells[i][j].countGrid = 2*i+j;
			}
		}
		
		myLookUpTable2D.buildLookUpTable(myCountGridCells, 5, 5);
		
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				System.out.print(myCountGridCells[i][j].countGrid+" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");

		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				System.out.print(myLookUpTable2D.lookUpTable[i][j]+" ");
			}
			System.out.print("\n");
		}
		
		System.out.println("abc:" + myLookUpTable2D.getRegionSum(2, 1, 3, 2));
	}
}

