package de.comp16.camelsolver1;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageHandler {
	
	public static final String OWN_URI = "http://localhost:8080/rest_api/solve?httpMethodRestrict=POST";
	public static final String BROKER_URI = "rabbitmq://136.199.51.111/inExchange?username=kompo&password=kompo&skipQueueDeclare=true";
	public static final String[] SOLVE_INSTRUCTION= new String[]{"solved:impossible","solved:one","solved:many"};
	
	
	public void postMessage(SudokuMessage in_message) {
		
		System.out.println(in_message.getInstruction());
		System.out.println(in_message.getRequest_id());
		System.out.println(in_message.getSender());
		System.out.println(in_message.getSudoku().length);
		for (int i = 0; i < in_message.getSudoku().length; i++) {
			System.out.printf("%2d", in_message.getSudoku()[i]);
		}
		//TODO Validate message
		
		if (in_message.getInstruction().equals("solve")) {
			Sudoku toSolve = new Sudoku(in_message.getSudoku());
			int result = new SudokuSolver().solve(toSolve);
			//TODO difficulty?
			
			SudokuMessage answer = new SudokuMessage();
			answer.setRequest_id(in_message.getRequest_id());
			answer.setSudoku(toSolve.getValuesAsArray());
			answer.setInstruction(SOLVE_INSTRUCTION[result]);
			sendMessage(answer);		
			
		} else if (in_message.getInstruction()=="ping") {
			SudokuMessage answer = new SudokuMessage();
			answer.setInstruction("pong");
			answer.setRequest_id(in_message.getRequest_id());
			answer.setSudoku(in_message.getSudoku());
			sendMessage(answer);
		}
	}

	public void sendMessage(SudokuMessage out_message) {
		out_message.setSender(OWN_URI);
		
		System.out.println(out_message.toString());
		//TODO: Send message to broker
		
		//TEMP: Ausgabe auf Konsole und in Datei
		String nowAsISO = ZonedDateTime.now().format( DateTimeFormatter.ISO_INSTANT ).replace(':', '-');
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert object to JSON string and save into a file directly
			File newdir = new File("var/out_messages");
			newdir.mkdirs();
			mapper.writeValue(new File("var/out_messages/message"+nowAsISO+".json"), out_message);

			// Convert object to JSON string and pretty print
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(out_message);
			System.out.println(jsonInString);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
