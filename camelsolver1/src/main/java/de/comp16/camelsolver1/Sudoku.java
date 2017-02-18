package de.comp16.camelsolver1;

/**
 * Class representing a sudoku, i.e. a 9x9 array of integers [0-9] with 0 denoting a not yet filled cell.
 * @author Felix Steinmeier
 * @author Carina Krämer *
 */
public class Sudoku {
	
	private int size; // size of one dimension, e.g. 9 in case of a 9x9 sudoku with 81 cells
	private int[][] values;
	
	
	public Sudoku(int[][] values) throws InvalidSudokuException {
		this.size = values.length;
		if (size == 0 || Math.sqrt(size) != Math.floor(Math.sqrt(size)))
			throw new InvalidSudokuException(size+" is not a valid sudoku x-dimension!");
		for (int i = 0; i < values.length; i++) {
			if (values[i].length != size)
				throw new InvalidSudokuException("[Sudoku] "+values[i].length+" does not match x-dimension"+size+"!");
			for (int j = 0; j < values[i].length; j++) {
				if (values[i][j] < 0  || values[i][j] > size)
					throw new InvalidSudokuException("[Sudoku] sudoku contains invalid values!");
			}
		}
		this.values = values;
	}
	public Sudoku(int[] array) throws InvalidSudokuException {
		double givenSize = Math.sqrt(array.length);
		if (Math.sqrt(givenSize) != Math.floor(Math.sqrt(givenSize)))
			throw new InvalidSudokuException("[Sudoku] int["+array.length+"] is not a valid sudoku size!");
		this.size = (int) givenSize;
		this.values = new int[size][size];
		for (int i = 0; i < array.length; i++) {
			if (array[i] < 0  || array[i] > size)
				throw new InvalidSudokuException("[Sudoku] sudoku contains invalid value "+array[i]+"!");
			values[Math.floorDiv(i, size)][i % size] = array[i];
		}
	}

	public int[][] getValues() {
		return values;
	}
	
	public int getSize() {
		return size;
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

	public class InvalidSudokuException extends Exception {
		private static final long serialVersionUID = 127201099702369520L;
		public InvalidSudokuException() { super(); }
		  public InvalidSudokuException(String message) { super(message); }
		  public InvalidSudokuException(String message, Throwable cause) { super(message, cause); }
		  public InvalidSudokuException(Throwable cause) { super(cause); }
		}
}
