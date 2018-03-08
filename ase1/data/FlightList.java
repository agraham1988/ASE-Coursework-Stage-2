package ase1.data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class FlightList {
	HashMap<String, Flight> flights;
	
	public FlightList() {
		loadFlights();
	}
	
	/**
	 * Loads the flights from the comma seperated txt file.
	 * Parses each line of the text file as a different flight, and adds them to the collection of flights.
	 */
	private void loadFlights() {
		File f = new File("flight.txt");
		Scanner scanner;
		
		//instantiate Flight HashMap
		flights = new HashMap<String, Flight>();
		
		//added try catch
		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()) {     
				String inputLine = scanner.nextLine();   //do something with this line     
				String parts[] = inputLine.split(",");
				
				Flight currentFlight = new Flight(parts[0],
						parts[1],
						parts[2],
						Integer.parseInt(parts[3]),
						Float.parseFloat(parts[4]),
						Float.parseFloat(parts[5]),
						Float.parseFloat(parts[6]));

				flights.put(parts[0], currentFlight);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Flight get(String flightCode) {
		return flights.get(flightCode);
	}
	
	public Collection<Flight> getValues() {
		return flights.values();
	}
}
