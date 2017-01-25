package de.comp16.camelsolver1;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SudokuMessage {

	@JsonProperty("request-id")
	private String request_id;
	private String sender;
	private String instruction;
	private int[] sudoku;

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public int[] getSudoku() {
		return sudoku;
	}

	public void setSudoku(int[] sudoku) {
		this.sudoku = sudoku;
	}

}
