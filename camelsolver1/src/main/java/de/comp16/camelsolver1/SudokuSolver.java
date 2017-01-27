package de.comp16.camelsolver1;

import java.util.HashSet;
import java.util.Set;

public class SudokuSolver {
	
	public static final int IMPOSSIBLE = 0;
	public static final int ONE = 1;
	public static final int MANY = 2;
	
	private int[][] current_values = new int[9][9];
	private boolean solved = false;
	
	public int solve(Sudoku sudoku) {
		
		if (numberGivenCells(sudoku) < 17) return MANY;
		
		current_values = sudoku.getValues();
		
		int[] startingCell = fewestCandidates();
		if (!solved) fillCell(startingCell[0], startingCell[1], 0);

		if (numberDifferentDigits(sudoku) < 8 && solved) return MANY;
		
		sudoku = new Sudoku(current_values);
		
		if (solved) return ONE;
		else return IMPOSSIBLE;
	}
	
	public int numberGivenCells(Sudoku sudoku) {
		int nrGivenCells = 0;
		for (int digit : sudoku.getValuesAsArray()) {
			if (digit != 0) nrGivenCells++;
		}
		return nrGivenCells;
	}
	
	public int numberDifferentDigits(Sudoku sudoku) {
		Set<Integer> givenDigits = new HashSet<Integer>();
		for (int digit : sudoku.getValuesAsArray()) {
			if (digit != 0) givenDigits.add(new Integer(digit));
		}
		return givenDigits.size();
	}

	//Füllt das Feld (x,y) nacheinander mit allen möglichen Kandidaten und ruft sich rekursiv selbst auf
	private void fillCell(int x, int y, int recursionLevel) {
		if (solved) return;
		Set<Integer> candidates = possibleAt(x, y);
//		System.out.println("[fillCell] candidates: "+candidates+" for cell ("+x+","+y+")");
		for (int candidate : candidates) {
			if (recursionLevel < 9001) {
				System.out.print("[fillCell] "+new String(new char[recursionLevel]).replace("\0", "-"));
				System.out.println("candidates for cell ("+x+","+y+"): "+candidates+" -trying "+candidate);
			}
			current_values[x][y] = candidate;
			int[] nextCell = fewestCandidates();
//			System.out.println("[fillCell] next cell: ("+nextCell[0]+","+nextCell[1]+")");
			if (nextCell[0] != -1) fillCell(nextCell[0], nextCell[1], recursionLevel+1);
			if (!solved) current_values[x][y] = 0;
		}
	}
	
	//Liefert den Punkt {x,y} zurück, an welchem die wenigsten Zahlen einsetzbar sind
	// - oder {-1,-1}, falls keine mehr einsetzbar ist
	private int[] fewestCandidates() {
		int[] coords = {-1, -1};
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

	Set<Integer> possibleAt(int x, int y) {
		return possibleInBlock(x, y, possibleInCol(y, possibleInRow(x) ));
	}
	
	Set<Integer> possibleInCol(int y) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i < 10; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInCol(y, allowed);
	}
	Set<Integer> possibleInCol(int y, Set<Integer> allowed) {
		for (int i = 0; i < 9; i++) {
			if (current_values[i][y] != 0) allowed.remove(new Integer(current_values[i][y]));
		}
		return allowed;
	}	
	
	Set<Integer> possibleInRow(int x) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i < 10; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInRow(x, allowed);
	}
	Set<Integer> possibleInRow(int x, Set<Integer> allowed) {
		for (int i = 0; i < 9; i++) {
			if (current_values[x][i] != 0) allowed.remove(new Integer(current_values[x][i]));
		}
		return allowed;
	}
	
	Set<Integer> possibleInBlock(int x, int y) {
		Set<Integer> allowed = new HashSet<Integer>();
		for (int i = 1; i < 10; i++) {
			allowed.add(new Integer(i));
		}
		return possibleInBlock(x, y, allowed);
	}
	Set<Integer> possibleInBlock(int x, int y, Set<Integer> allowed) {
		for (int i = Math.floorDiv(x, 3)*3; i < (Math.floorDiv(x, 3)*3)+3; i++) {
			for (int j = Math.floorDiv(y, 3)*3; j < (Math.floorDiv(y, 3)*3)+3; j++) {
				if (current_values[i][j] != 0) allowed.remove(new Integer(current_values[i][j]));
			}
		}
		return allowed;
	}
	
}
