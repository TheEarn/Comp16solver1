package de.comp16.camelsolver1;

import java.util.HashSet;
import java.util.Set;

import de.comp16.camelsolver1.Sudoku.InvalidSudokuException;

/**
 * Provides sudoku solving functionality.<br>
 * Used by MessageHandler to determine the number of possible solutions (none, one, many) and 
 * - if applicable - to provide an admissible solution.
 * @see MessageHandler
 * @author Felix Steinmeier
 * @author Carina Krämer
 */
public class SudokuSolver {
	
	// return values
	public static final int IMPOSSIBLE = 0;
	public static final int ONE = 1;
	public static final int MANY = 2;
	// denotes the maximum level of recursion up to which console output is generated 
	public static final int DEBUGLEVEL = 6;
	// whether to stop after finding the first solution
	public static final boolean solveMultiple = false;
	
	private Sudoku startSudoku;
	private int[][] current_values;
	private int size;
	private Sudoku solvedSudoku;
	private boolean solvedMultiple = false;
	private boolean solvedOnce = false;
	
	public SudokuSolver(Sudoku sudoku) {
		this.current_values = sudoku.getValues();
		try {
			this.startSudoku = new Sudoku(current_values);
			this.size = startSudoku.getSize();
		} catch (InvalidSudokuException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a new object containing the current Sudoku.<br>
	 * Provides the solution if solve() returned ONE or the last state of calculations, otherwise.
	 * @return The current Sudoku
	 */
	public Sudoku getCurrentSudoku() {
		return solvedSudoku;
//		try {
//			return new Sudoku(current_values);
//		} catch (InvalidSudokuException e) {
//			e.printStackTrace();
//			return null; // this should never happen
//		}
	}

	/**
	 * Attempts to solve the previously given sudoku
	 * @return An int indicating one, many or no possible solutions 
	 */
	public int solve() {
		if (startSudoku == null) throw new RuntimeException("solve: no Sudoku given");
		if (size >= 9 && numberGivenCells(startSudoku) < 17) return MANY;
		
		int[] startingCell = fewestCandidates();
		if (!solvedOnce) fillCell(startingCell[0], startingCell[1], 0);

		if (numberDifferentDigits(startSudoku) < size-1 && (solvedMultiple || solvedOnce)) return MANY;
		if (solvedOnce && solvedMultiple) return MANY;
		if (solvedMultiple || solvedOnce) return ONE;
		else return IMPOSSIBLE;
	}
	
	/**
	 * Returns the number of given cells in the provided sudoku, i.e. the number of cells not containing 0.
	 * @param sudoku the sudoku
	 * @return number of given cells
	 */
	public static int numberGivenCells(Sudoku sudoku) {
		int nrGivenCells = 0;
		for (int digit : sudoku.getValuesAsArray()) {
			if (digit != 0) nrGivenCells++;
		}
		return nrGivenCells;
	}
	
	/**
	 * Returns the number of different digits given in the provided sudoku.
	 * @param sudoku the sudoku
	 * @return number of different digits
	 */
	public static int numberDifferentDigits(Sudoku sudoku) {
		Set<Integer> givenDigits = new HashSet<Integer>();
		for (int digit : sudoku.getValuesAsArray()) {
			if (digit != 0) givenDigits.add(new Integer(digit));
		}
		return givenDigits.size();
	}

	//Füllt das Feld (x,y) nacheinander mit allen möglichen Kandidaten und ruft sich rekursiv selbst auf
	/**
	 * Fills cell (x,y) of current sudoku with all possible candidates successively and calls itself recursively
	 * @param x Row index of specified cell
	 * @param y Column index of specified cell
	 * @param recursionLevel Recursion depth (used for formatted output only)
	 */
	private void fillCell(int x, int y, int recursionLevel) {
		Set<Integer> candidates = possibleAt(x, y);
//		System.out.println("[fillCell] candidates: "+candidates+" for cell ("+x+","+y+")");
		for (int candidate : candidates) {
			if (recursionLevel < DEBUGLEVEL) {
				System.out.print("[fillCell] "+new String(new char[recursionLevel]).replace("\0", "-"));
				System.out.println("candidates for cell ("+x+","+y+"): "+candidates+" -trying "+candidate);
			}
			current_values[x][y] = candidate;
			int[] nextCell = fewestCandidates();
//			System.out.println("[fillCell] next cell: ("+nextCell[0]+","+nextCell[1]+")");
//			if (nextCell[0] != -1) fillCell(nextCell[0], nextCell[1], recursionLevel+1);
			if (!solvedOnce) fillCell(nextCell[0], nextCell[1], recursionLevel+1);
			if (!solvedOnce) current_values[x][y] = 0;
		}
	}
	
	//Liefert den Punkt {x,y} zurück, an welchem die wenigsten Zahlen einsetzbar sind
	// - oder {-1,-1}, falls keine mehr einsetzbar ist
	/**
	 * Provides the first cell {x,y} (respective to x,y-ordering) which 
	 * allows the fewest candidates to be filled in.<br>
	 * If there are no possible candidates {-1,-1} is returned;
	 *  in this case the <em>current</em> sudoku is either completely solved or unsolvable.
	 * @return An int[2] representing the cell (x,y)
	 */
	private int[] fewestCandidates() {
		int[] coords =new int[2];
		int smallestNumber = Integer.MAX_VALUE;
		for (int i = 0; i < current_values.length; i++) {
			for (int j = 0; j < current_values[i].length; j++) {
				if (current_values[i][j] == 0) {
//					System.out.println("[fewestCandidates] looking at ("+i+","+j+"): "+possibleAt(i, j).toString());
					int currentNumber = possibleAt(i, j).size();
					if (currentNumber < smallestNumber) {
						smallestNumber = currentNumber;
						coords[0]=i; coords[1]=j;
					}
				}
			}
		}
		
		if (smallestNumber == Integer.MAX_VALUE) {
			if (!solvedOnce) {
				solvedOnce = true;
				try {
					solvedSudoku = new Sudoku(current_values);
					System.out.println("Found first solution!\n"+solvedSudoku.toString());
				} catch (InvalidSudokuException e) {
					e.printStackTrace(); // should never happen
				}
			} else {
				solvedMultiple = true;
			}
		}
		return coords;
	}

	/**
	 * Returns an array of Integers representing all values between 1 and size of the sudoku (incl.)
	 * 	which may be filled in given cell (x,y) according to standard sudoku rules.
	 * @param x Row index of specified cell
	 * @param y Column index of specified cell
	 * @return The allowed values
	 */
	Set<Integer> possibleAt(int x, int y) {
		return possibleInBlock(x, y, possibleInCol(y, possibleInRow(x)));
	}
	
	Set<Integer> possibleInCol(int y) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i <= size; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInCol(y, allowed);
	}
	Set<Integer> possibleInCol(int y, Set<Integer> allowed) {
		for (int i = 0; i < size; i++) {
			if (current_values[i][y] != 0) allowed.remove(new Integer(current_values[i][y]));
		}
		return allowed;
	}	
	
	Set<Integer> possibleInRow(int x) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i <= size; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInRow(x, allowed);
	}
	Set<Integer> possibleInRow(int x, Set<Integer> allowed) {
		for (int i = 0; i < size; i++) {
			if (current_values[x][i] != 0) allowed.remove(new Integer(current_values[x][i]));
		}
		return allowed;
	}
	
	Set<Integer> possibleInBlock(int x, int y) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i <= size; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInBlock(x, y, allowed);
	}
	Set<Integer> possibleInBlock(int x, int y, Set<Integer> allowed) {
		int bSize = (int) Math.sqrt(size);
		for (int i = Math.floorDiv(x, bSize)*bSize; i < (Math.floorDiv(x, bSize)*bSize)+bSize; i++) {
			for (int j = Math.floorDiv(y, bSize)*bSize; j < (Math.floorDiv(y, bSize)*bSize)+bSize; j++) {
				if (current_values[i][j] != 0) allowed.remove(new Integer(current_values[i][j]));
			}
		}
		return allowed;
	}
	
}
