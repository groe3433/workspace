package util.sort;

import util.ArrayOperations;
import util.PrintOperations;

/**
 * Choose pivot, reorder list until pivot is in correct place. Recursively do
 * the same for the sub-lists.
 * 
 * The complexity of quick sort in the average case is Θ(n*log(n)) and in the
 * worst case is Θ(n2).
 */
public class QuickSort {

	private int array[];
	private int length;

	/**
	 * 
	 * @return
	 */
	public int[] getArray() {
		return array;
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int[] getSortedList(int[] array) {
		PrintOperations.printSTDOUT("***** Quick Sort Used *****");
		QuickSort sorter = new QuickSort();
		sorter.sort(array);
		return sorter.getArray();
	}

	/**
	 * 
	 * @param array
	 */
	private void sort(int[] array) {
		if (array == null || array.length == 0) {
			return;
		}
		this.array = array;
		length = array.length;
		// lowerIndex is at the beginning, and higherIndex is at the end. 
		// Kick it off the first time for the FULL list.
		quickSort(0, length - 1);
	}

	/**
	 * 
	 * 
	 * @param lowerIndex
	 * @param higherIndex
	 */
	private void quickSort(int lowerIndex, int higherIndex) {
		int numLowIndexCount = lowerIndex;
		int numHighIndexCount = higherIndex;
		// roughly calculate the pivot to be in the middle of the list. 
		int pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
		// walk each index count to the pivot. 
		while (numLowIndexCount <= numHighIndexCount) {
			// pause when you get a value that is HIGHER. 
			while (array[numLowIndexCount] < pivot) {
				numLowIndexCount++;
			}
			// pause when you get a value that is LOWER. 
			while (array[numHighIndexCount] > pivot) {
				numHighIndexCount--;
			}
			// check them, swap them, and continue. 
			if (numLowIndexCount <= numHighIndexCount) {
				array = ArrayOperations.swapElements(numLowIndexCount, numHighIndexCount, array);
				numLowIndexCount++;
				numHighIndexCount--;
			}
		}
		if (lowerIndex < numHighIndexCount) {
			// numLowIndexCount has now been moved up to the pivot point. 
			// Kick it off again for the lower list.
			quickSort(lowerIndex, numHighIndexCount);
		}
		if (numLowIndexCount < higherIndex) {
			// numHighIndexCount has now been moved down to the pivot point.
			// Kick it off again for the upper list.
			quickSort(numLowIndexCount, higherIndex);
		}
	}
}