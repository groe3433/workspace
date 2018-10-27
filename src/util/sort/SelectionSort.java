package util.sort;

import util.ArrayOperations;
import util.PrintOperations;

/**
 * Shift higher values up, find indexes of lower values to be swapped down. 
 * 
 * Selection Sort has n(n − 1) / 2 ∈ Θ(n2) comparisons.
 * Each of these scans requires one swap for n − 1 elements. 
 */
public class SelectionSort {
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int[] getSortedList(int[] array) {
		PrintOperations.printSTDOUT("***** Selection Sort Used *****");
		for(int numShiftUp = 0; numShiftUp < array.length; numShiftUp++) {
			int numIndexToBeShiftedDown = numShiftUp;
			for(int numSearchForShifter = numShiftUp + 1; numSearchForShifter < array.length; numSearchForShifter++) {
				if(array[numSearchForShifter] < array[numIndexToBeShiftedDown]) {
					numIndexToBeShiftedDown = numSearchForShifter;
				}
			}
			array = ArrayOperations.swapElements(numShiftUp, numIndexToBeShiftedDown, array);
		}
		return array;
	}
}
