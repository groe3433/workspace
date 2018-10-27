package util.sort;

import util.ArrayOperations;
import util.PrintOperations;

/**
 * Working up, compare value with the 1 before it and swap if necessary. 
 * 
 * In the best case insertion sort has a linear running time Θ(n). 
 * In average or worst case insertion sort has a quadratic running time O(n2). 
 */
public class InsertionSort {
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int[] getSortedList(int[] array) {
		PrintOperations.printSTDOUT("***** Insertion Sort Used *****");
		for(int numWorkUp = 1; numWorkUp < array.length; numWorkUp++) {
			for(int numShiftDown = numWorkUp; numShiftDown > 0; numShiftDown--) {
				if(array[numShiftDown] < array[numShiftDown-1]) {
					array = ArrayOperations.swapElements(numShiftDown, numShiftDown-1, array);
				}
			}
		}
		return array;
	}
}
