package de.comp16.camelsolver1;

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

	private int[][] values;

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

}
