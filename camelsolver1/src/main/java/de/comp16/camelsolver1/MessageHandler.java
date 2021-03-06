package de.comp16.camelsolver1;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.comp16.camelsolver1.Sudoku.InvalidSudokuException;

/**
 * Handles messages for Components 2016s Solver1.
 * 
 * @author Felix Steinmeier
 * @author Carina Krämer
 */
public class MessageHandler {
	
	public static final String[] SOLVE_INSTRUCTION= new String[]{"solved:impossible","solved:one","solved:many"};
	
	@EndpointInject(uri="direct:out")
	ProducerTemplate outTemplate;
	
	/**
	 * Processes a received message in the form of a SudokuMessage POJO.
	 * @param in_message Received message
	 */
	public void postMessage(SudokuMessage in_message) {
		
		String nowAsISO = ZonedDateTime.now().format( DateTimeFormatter.ISO_INSTANT ).replace(':', '-');
		if (in_message == null) {
			System.out.println("[MessageHandler] Incoming Message (at "+nowAsISO+") IS NULL! (JSON->POJO binding failed)");
			return;
		}
		if (in_message.getInstruction() == null || in_message.getRequest_id() == null || 
				in_message.getSender() == null || in_message.getSudoku() == null ) {
			System.out.println("[MessageHandler] Incoming Message (at "+nowAsISO+") INVALID! (missing field)");
			return;
		}
		System.out.println("[MessageHandler] Incoming Message (at "+nowAsISO+"):");
		printMessage(in_message);
		
		if (in_message.getInstruction().equals("solve")) {
			Sudoku toSolve;
			try {
				toSolve = new Sudoku(in_message.getSudoku());
			} catch (InvalidSudokuException e) {
				e.printStackTrace();
				return;
			}
			System.out.println("[MessageHandler] Incoming Sudoku:\n"+toSolve.toString());
			
			SudokuSolver solver = new SudokuSolver(toSolve);
			int result = solver.solve();
			Sudoku solvedSudoku = solver.getCurrentSudoku();
			//TODO difficulty?
			
			switch (result) {
			case SudokuSolver.ONE:
				System.out.println("\n[postMessage] Solved!");
				break;
			case SudokuSolver.IMPOSSIBLE:
				System.out.println("\n[postMessage] Impossible!");
				break;
			case SudokuSolver.MANY:
				System.out.println("\n[postMessage] Multiple Solutions!");
				break;
			}
			System.out.println("[postMessage] Resulting Sudoku:\n"+solvedSudoku.toString());

			SudokuMessage answer = new SudokuMessage();
			answer.setRequest_id(in_message.getRequest_id());
			answer.setSudoku(solvedSudoku.getValuesAsArray());
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

	public static SudokuMessage registerAtBroker(boolean registering) {
		SudokuMessage registerMessage = new SudokuMessage();
		registerMessage.setRequest_id(java.util.UUID.randomUUID().toString());
		registerMessage.setSudoku(new int[81]);
		registerMessage.setInstruction(registering? "register:solver" : "unregister:solver");
		registerMessage.setSender(MyRouteBuilder.OWN_URI);
		return registerMessage;
	}
	
	public void sendMessage(SudokuMessage out_message) {
		out_message.setSender(MyRouteBuilder.OWN_URI);
		
		String nowAsISO = ZonedDateTime.now().format( DateTimeFormatter.ISO_INSTANT ).replace(':', '-');
		System.out.println("[sendMessage] Sending Message (at "+nowAsISO+"):");
		printMessage(out_message);

		//Send message to broker
		outTemplate.sendBody(out_message);
	}
	
	/**
	 * Prints the given SudokuMessage on Console (via Jackson's PrettyPrinter) 
	 * @param message The message to be printed
	 */
	public void printMessage(SudokuMessage message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert object to JSON string and pretty print
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
			System.out.println(jsonInString);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
}
