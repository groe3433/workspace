package util.sort;

import util.ArrayOperations;
import util.PrintOperations;

/**
 * Bubble higher values up, swapping each time. 
 * 
 * Bubble sort has worst-case and average complexity both О(n2).
 * Performance of bubble sort over an already-sorted list (best-case) is O(n). 
 */
public class BubbleSort {
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int[] getSortedList(int[] array) {
		PrintOperations.printSTDOUT("***** Bubble Sort Used *****");
		int numSize = array.length;
		int numSwapIndex;
		for(int numShiftDown = numSize; numShiftDown >= 0; numShiftDown--) {
			for(int numBubbleUp = 0; numBubbleUp < numSize - 1; numBubbleUp++) {
				numSwapIndex = numBubbleUp + 1;
				if(array[numBubbleUp] > array[numSwapIndex]) {
					array = ArrayOperations.swapElements(numBubbleUp, numSwapIndex, array);
				}
			}
		}
		return array;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int[] getReverseSortedList(int[] array) {
		PrintOperations.printSTDOUT("***** Bubble Sort Used (reverse) *****");
		int numSize = array.length;
		int numSwapIndex;
		for(int numShiftDown = numSize; numShiftDown >= 0; numShiftDown--) {
			for(int numBubbleUp = 0; numBubbleUp < numSize - 1; numBubbleUp++) {
				numSwapIndex = numBubbleUp + 1;
				if(array[numBubbleUp] <= array[numSwapIndex]) {
					array = ArrayOperations.swapElements(numBubbleUp, numSwapIndex, array);
				}
			}
		}
		return array;
	}
}
