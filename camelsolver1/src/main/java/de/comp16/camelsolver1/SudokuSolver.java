package de.comp16.camelsolver1;

import java.util.HashSet;
import java.util.PriorityQueue;
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
	
	public static final int IMPOSSIBLE = 0;
	public static final int ONE = 1;
	public static final int MANY = 2;
	
	public static final int DEBUGLEVEL = 5;
	
	private Sudoku startSudoku;
	private int[][] current_values;
	private int size;
	private boolean solved = false;

	
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
		try {
			return new Sudoku(current_values);
		} catch (InvalidSudokuException e) {
			e.printStackTrace();
			return null; // this should never happen
		}
	}

	/**
	 * Attempts to solve the previously given sudoku
	 * @return An int indicating one, many or no possible solutions 
	 */
	public int solve() {
		if (startSudoku == null) throw new RuntimeException("solve: no Sudoku given");
		if (size == 9 && numberGivenCells(startSudoku) < 17) return MANY;
		
		Candidates startingCandidates = new Candidates();
		if (!solved) fillCell(startingCandidates, 0);

		if (solved && numberDifferentDigits(startSudoku) < size-1) return MANY;
		if (solved) return ONE;
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
	private void fillCell(Candidates candidates, int recursionLevel) {
		int x = candidates.getFewestCandidatesCell()[0];
		int y = candidates.getFewestCandidatesCell()[1];
		Set<Integer> currentCandidates = new HashSet<Integer>();
		currentCandidates.addAll(candidates.getCandidates(x, y));
//		System.out.println("[fillCell] candidates: "+candidates+" for cell ("+x+","+y+")");
		for (int currentCandidate : currentCandidates) {
			if (recursionLevel < DEBUGLEVEL) {
				System.out.print("[fillCell] "+new String(new char[recursionLevel]).replace("\0", "-"));
				System.out.println("candidates for cell ("+x+","+y+"): "+currentCandidates+" -trying "+currentCandidate);
			}
			current_values[x][y] = currentCandidate;
			Candidates newCandidates = new Candidates(candidates);
			newCandidates.removeCandidates(x, y, currentCandidate);
//			System.out.println("[fillCell] next cell: ("+nextCell[0]+","+nextCell[1]+")");
//			if (nextCell[0] != -1) fillCell(nextCell[0], nextCell[1], recursionLevel+1);
			if (!solved) fillCell(newCandidates, recursionLevel+1);
			if (!solved) current_values[x][y] = 0;
		}
	}
	
	//Liefert den Punkt {x,y} zurück, an welchem die wenigsten Zahlen einsetzbar sind
	/**
	 * Provides the first cell {x,y} (respective to x,y-ordering) which 
	 * allows the fewest candidates to be filled in.<br>
	 * If there are no possible candidates the <em>current</em> sudoku is either completely solved or unsolvable.
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
		
		if (smallestNumber == Integer.MAX_VALUE) solved = true;
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

	class Candidates {
		
		private Set<Integer>[][] candidateArray;
		private int[] currentFewestCandidates;
//		private int emptyCells = 0;

		public Candidates() {
//			this.emptyCells = size*size;
			this.candidateArray = (Set<Integer>[][]) new HashSet[size][size];
			int smallestNumber = Integer.MAX_VALUE;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (current_values[i][j] == 0) {
						candidateArray[i][j] = possibleAt(i, j);
						if (candidateArray[i][j].size() < smallestNumber) {
							smallestNumber = candidateArray[i][j].size();
							currentFewestCandidates = new int[]{i,j};
						}
					} else {
						candidateArray[i][j] = new HashSet<Integer>();
//						emptyCells++;
					}
				}
			}
			
		}
		public Candidates(Candidates toCopy) {
			this.candidateArray = (Set<Integer>[][]) new HashSet[size][size];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					this.candidateArray[i][j] = new HashSet<Integer>();
					this.candidateArray[i][j].addAll(toCopy.getCandidates(i, j));
				}
			}
//			this.candidateArray = toCopy.getCandidateArray().clone();
			this.currentFewestCandidates = toCopy.getFewestCandidatesCell().clone();
//			this.emptyCells = toCopy.emptyCells;
		}

//		public int getEmptyCells() {
//			return emptyCells;
//		}
		public Set<Integer>[][] getCandidateArray() {
			return candidateArray;
		}
		public int[] getFewestCandidatesCell() {
			return currentFewestCandidates;
		}
		public int getSmallestNumber() {
			return candidateArray[currentFewestCandidates[0]][currentFewestCandidates[1]].size();
		}
		public Set<Integer> getCandidates(int x, int y) {
			return candidateArray[x][y];
		}
		
		public void removeCandidates(int x, int y, int value) {
			candidateArray[x][y] = new HashSet<Integer>();
			boolean wasEmpty;
			for (int i = 0; i < size; i++) {
//				wasEmpty = (candidateArray[x][i].size() == 0);
				candidateArray[x][i].remove(new Integer(value));
				if (candidateArray[x][i].size() > 0 && candidateArray[x][i].size() < getSmallestNumber()) currentFewestCandidates = new int[]{x,i};
//				if (!wasEmpty && candidateArray[x][i].size() == 0) emptyCells--;
				
//				wasEmpty = (candidateArray[i][y].size() == 0);
				candidateArray[i][y].remove(new Integer(value));
				if (candidateArray[i][y].size() > 0 && candidateArray[i][y].size() < getSmallestNumber()) currentFewestCandidates = new int[]{i,y};
//				if (!wasEmpty && candidateArray[i][y].size() == 0) emptyCells--;
			}
			int bSize = (int) Math.sqrt(size);
			for (int i = Math.floorDiv(x, bSize)*bSize; i < (Math.floorDiv(x, bSize)*bSize)+bSize; i++) {
				for (int j = Math.floorDiv(y, bSize)*bSize; j < (Math.floorDiv(y, bSize)*bSize)+bSize; j++) {
//					wasEmpty = (candidateArray[i][j].size() == 0);
					candidateArray[i][j].remove(new Integer(value));
					if (candidateArray[i][j].size() > 0 && candidateArray[i][j].size() < getSmallestNumber()) currentFewestCandidates = new int[]{i,j};
//					if (!wasEmpty && candidateArray[i][j].size() == 0) emptyCells--;
				}
			}
//			if (emptyCells == 0) solved = true;
			updateFewestCandidates();
		}
		
		private void updateFewestCandidates() {
			int smallestNumber = Integer.MAX_VALUE;
			boolean allFilled = true;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (candidateArray[i][j].size() != 0) {
						if (candidateArray[i][j].size() < smallestNumber) {
							smallestNumber = candidateArray[i][j].size();
							currentFewestCandidates = new int[]{i,j};
						}
					}
					if (current_values[i][j] == 0) allFilled = false;
				}
			}
			if (allFilled) solved = true;
		}


	}
}
