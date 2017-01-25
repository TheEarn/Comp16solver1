package de.comp16.camelsolver1;

public class SudokuSolver {
	
	public static final int IMPOSSIBLE = 0;
	public static final int ONE = 1;
	public static final int MANY = 2;
	
	public int solve(Sudoku sudoku) {
		sudoku.setValue(0, 1, 4);
		sudoku.setValue(0, 2, 2);
		return IMPOSSIBLE;
	}
}
