package main;

import util.sort.BubbleSort;
import util.sort.InsertionSort;
import util.sort.MergeSort;
import util.sort.QuickSort;
import util.sort.SelectionSort;
import util.PrintOperations;

public class sortingmain {
	public static void main(String [] s) {
		int [] unsortedNumList1 = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList1);
		int [] sortedNumList1 = BubbleSort.getSortedList(unsortedNumList1);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList1);
		PrintOperations.printSTDOUT("------------------------------------------");
		int [] unsortedNumList1r = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList1r);
		int [] sortedNumList1r = BubbleSort.getReverseSortedList(unsortedNumList1r);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList1r);
		PrintOperations.printSTDOUT("------------------------------------------");
		int [] unsortedNumList2 = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList2);
		int [] sortedNumList2 = SelectionSort.getSortedList(unsortedNumList2);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList2);		
		PrintOperations.printSTDOUT("------------------------------------------");
		int [] unsortedNumList3 = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList3);
		int [] sortedNumList3 = InsertionSort.getSortedList(unsortedNumList3);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList3);		
		PrintOperations.printSTDOUT("------------------------------------------");
		int [] unsortedNumList4 = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList4);
		int [] sortedNumList4 = QuickSort.getSortedList(unsortedNumList4);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList4);		
		PrintOperations.printSTDOUT("------------------------------------------");
		int [] unsortedNumList5 = {9, 4, 6, 2, 6, 0, 1, 3, 7, 11, 2};
		PrintOperations.printSTDOUT("**UnSorted List: ", unsortedNumList5);
		int [] sortedNumList5 = MergeSort.getSortedList(unsortedNumList5);
		PrintOperations.printSTDOUT("**Sorted List: ", sortedNumList5);
	}
}
