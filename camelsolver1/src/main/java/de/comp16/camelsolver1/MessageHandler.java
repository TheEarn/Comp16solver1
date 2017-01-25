package de.comp16.camelsolver1;

public class MessageHandler {
	
	public void postMessage(SudokuMessage message) {
		
		System.out.println(message.getInstruction());
		System.out.println(message.getRequest_id());
		System.out.println(message.getSender());
		System.out.println(message.getSudoku().length);
		for (int i = 0; i < message.getSudoku().length; i++) {
			System.out.printf("%2d", message.getSudoku()[i]);
		}
		
		if (message.getInstruction()=="solve") {
			
		} else if (message.getInstruction()=="ping") {
			
		}
	}

}
