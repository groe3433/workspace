package util;

public class ArrayOperations {
	/**
	 * Swap 2 elements in an array. Typically called from a SORT method. 
	 * 
	 * @param numStored
	 * @param numSwapped
	 * @param array
	 * @return
	 */
	public static int[] swapElements(int numStored, int numSwapped, int[] array) {
		int temp;
		temp = array[numStored];
		array[numStored] = array[numSwapped];
		array[numSwapped] = temp;
		return array;
	}
	
	/**
	 * INTERVIEW PROBLEM: given an array of ints, search a second array
	 * and return results into a third array. Final array should be
	 * how many of entries in first array are greater than EACH of the
	 * values in the second array. 
	 * 
	 * @param nums
	 * @param maxes
	 * @return
	 */
    public static int[] getMaxesArray(int[] nums, int[] maxes) {
    	int[] outputArray = new int[maxes.length];
    	int count = 0;
    	for(int i = 0; i < maxes.length; i++) {
    		for(int j = 0; j < nums.length; j++) {
    			if(nums[j] <= maxes[i]) {
    				count++;
    			}
    		}
    		outputArray[i] = count;
    		count = 0;
    	}
    	return outputArray;
    }
}
