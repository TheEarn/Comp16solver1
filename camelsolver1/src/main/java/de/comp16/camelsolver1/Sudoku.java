package de.comp16.camelsolver1;

/**
 * Class representing a sudoku, i.e. a 9x9 array of integers [0-9] with 0 denoting a not yet filled cell.
 * @author Felix Steinmeier
 * @author Carina Krämer *
 */
public class Sudoku {
	
	public Sudoku(int[][] values) {
		this.values = values;
	}
	public Sudoku(int[] array) {
		this.values = new int[9][9];
		for (int i = 0; i < array.length; i++) {
			this.values[Math.floorDiv(i, 9)][i % 9] = array[i];
		}
	}

	private int[][] values = new int[9][9];

	public int[][] getValues() {
		return values;
	}
	
	public int[] getValuesAsArray() {
		int[] array = new int[values.length*values[0].length];
		int arrayIndex = 0;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				array[arrayIndex++] = values[i][j];
			}
		}
		return array;
	}

	public void setValues(int[][] values) {
		this.values = values;
	}
	
	public void setValue(int row, int column, int value) {
		this.values[row][column] = value;
	}
	
	public int getValue(int row, int column) {
		return values[row][column];
	}

	public String toString() {
		String output = "";
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				output+=" "+values[i][j];
			}
			output+="\n";
		}
		return output;
	}
}
