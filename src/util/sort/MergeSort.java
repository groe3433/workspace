package util.sort;

import util.PrintOperations;

/**
 * Partition into sub-lists of 1 element, merge sub-lists until sorted (recursively). 
 * 
 * Merge sort is a fast, stable sorting routine with guaranteed O(n*log(n)) efficiency. 
 */
public class MergeSort {

    private int[] array;
    private int[] tempMergArr;
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
		PrintOperations.printSTDOUT("***** Merge Sort Used *****");
		MergeSort merge = new MergeSort();
		merge.sort(array);
		return merge.getArray();
	}

	/**
	 * 
	 * @param array
	 */
	private void sort(int[] array) {
        this.array = array;
        this.length = array.length;
        this.tempMergArr = new int[length];
        doMergeSort(0, length - 1);
	}

	/**
	 * 
	 * @param lowerIndex
	 * @param higherIndex
	 */
	private void doMergeSort(int lowerIndex, int higherIndex) {
        if (lowerIndex < higherIndex) {
            int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
            doMergeSort(lowerIndex, middle);
            doMergeSort(middle + 1, higherIndex);
            mergeParts(lowerIndex, middle, higherIndex);
        }
	}
	
	/**
	 * 
	 * @param lowerIndex
	 * @param middle
	 * @param higherIndex
	 */
    private void mergeParts(int lowerIndex, int middle, int higherIndex) {
        for (int numCount = lowerIndex; numCount <= higherIndex; numCount++) {
            tempMergArr[numCount] = array[numCount];
        }
        int numLowToMid = lowerIndex;
        int numMidToHigh = middle + 1;
        int numRealArray = lowerIndex;
        while (numLowToMid <= middle && numMidToHigh <= higherIndex) {
            if (tempMergArr[numLowToMid] <= tempMergArr[numMidToHigh]) {
                array[numRealArray] = tempMergArr[numLowToMid];
                numLowToMid++;
            } else {
                array[numRealArray] = tempMergArr[numMidToHigh];
                numMidToHigh++;
            }
            numRealArray++;
        }
        while (numLowToMid <= middle) {
            array[numRealArray] = tempMergArr[numLowToMid];
            numRealArray++;
            numLowToMid++;
        }
    }
}