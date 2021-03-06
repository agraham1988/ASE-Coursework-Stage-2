package ase1;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ase1.data.Flight;
import ase1.data.FlightList;
import ase1.data.PassengerList;

public class CheckInHandler {
	
	private PassengerList passengers;
	private FlightList flights;
	
	/**
	 * Constructor for the CheckInHandler.
	 * Instantiates a {@link PassengerList} object, and populates the collections of passengers and flights
	 * using the loadFlights and loadPassengers methods.
	 */
	public CheckInHandler() {
		flights = new FlightList();
		passengers = new PassengerList(flights);
	}
	
	/**
	 * Checks the details of the passenger who wants to check in.
	 * Checks the passengers name to the provided booking reference. As well as checking to make sure that
	 * the booking reference hasn't already checked in, and that it is on on record.
	 * 
	 * @param	boookingReference	The booking reference code of the passenger
	 * @param	lastName			Last name of the passenger, to check against that associated with booking reference
	 * @return	A boolean to show if the last name provided matches that of passenger with booking reference
	 * @throws	IllegalReferenceCodeException	If the booking reference does match a passenger that is to be checked in or any passenger on the system.
	 */
	public boolean checkDetails(String bookingReference, String lastName) throws IllegalReferenceCodeException{
		if( passengers.getNotCheckedIn().containsKey(bookingReference) ){	// Check that the booking reference provided matches, a passenger to be checked in
			//Strings should compared with .equals in Java
			if(passengers.getNotCheckedIn().get(bookingReference).getLastName().equals(lastName)){	// Checks if the passenger 
				return true; // Return true to show that the details match with a passenger to be checked in.
			}
			else{
				return false; // return false if they do not match,
			}
		}
		else if(passengers.getCheckedIn().containsKey(bookingReference)){ // Throw an exception if the matching passenger is already checked in
			throw new IllegalReferenceCodeException(bookingReference+": Is already checked in.");
		}
		else{ // Throw an exception if there is no passenger that matches this booking reference code
			throw new IllegalReferenceCodeException(bookingReference+": There is no booking reference on record.");
		}
		
	}

	/**
	 * Processes the passenger that wishes to be checked in.
	 * Calculates the fees to be payed based on the dimensions and weight of the baggage.
	 * Assigns the passenger as checked in, before adding to the number of passengers as well as the weight, and volume of the baggage on the flight.
	 * 
	 * @param	bookingReference	The booking reference of the passenger to be processed
	 * @param	dimensions			The width, height and depth of the baggage in a single array.
	 * @param	weight				The weight of the passengers baggage.
	 * @return 	The fee due from the passenger for any excesses on the baggage, -1 represents there was an error with check in
	 * @throws	IllegalReferenceCodeException	If there is no passenger with a matching booking reference code.
	 */
	public float processPassenger(String bookingReference, float[] dimensions, float weight) throws IllegalReferenceCodeException{
		float fee;	// Fee due from passenger, calculated from the weight Fee, volume fee and the multiplier for the passengers flight
		float weightFee = 0f, volFee = 0f;
		float multiplier = 1f;

		float vol = dimensions[0]*dimensions[1]*dimensions[2]; // Calculate the volume of the baggage
		
		// Cant have a negative value for a scalar like weight or volume, so it is assumed for now that this was a mistake
		// So the value will be taken, could throw an exception and request user to try again
		
		if(vol<0) {vol*=-1;}
		if(weight<0) {weight*=-1;}
		
		
		Flight flight = passengers.get(bookingReference).getFlight(); // Get the information on the flight the passenger is going on
		multiplier = flight.getFeeMultiplier();
		// Find the maximum baggage allowances for each passenger on that flight, 
		// take into account some passengers may go beyond the limit!

		float factorOfSafety = 0.8f;
		float maxWeight = (float) (( flight.getMaxBaggageWeight() / flight.getPassengerCapacity() )*factorOfSafety);
		float maxVol = (float) (( flight.getMaxBaggageVolume() / flight.getPassengerCapacity() )*factorOfSafety);
		
		// basic calculation to find the fees from the excess
		weightFee = weight-maxWeight;
		volFee = vol-maxVol;
		
		// If the passengers baggage is below the maximums, no fee is applied,
		if (weightFee<0){ weightFee = 0; }
		if (volFee<0){ volFee = 0; }
		
		fee = (weightFee+volFee)*multiplier;
		
		if(passengers.checkInPassenger(bookingReference)){ // Attempt to check in the passenger
			flight.addPassengerAndBaggage(vol,weight,fee);	// If they are checked in add baggage, and incrememnt number of passengers
		}
		else{
			// If for some reason the passenger cannot be checked in, we need to return an error
			throw new IllegalReferenceCodeException
			("There is no passenger with this booking reference to be checked in,\n they may be already checked in: "+bookingReference);
		}

		// Output the final fee due from the passenger,
		return fee;
	}
	
	/**
	 * Pass up the number of passengers still to be checked in.
	 * Asks {@link PassengerList}, to get the number of passengers who are still to be checked in.
	 * 
	 * @return The number of passengers still to be checked in.
	 */
	public int getNumToCheckIn() {
		return passengers.getNumToCheckIn();
	}
	
	/**
	 * Takes the reports from each individual flight and compiles them into one output.
	 * Loops through all the flights and gets their reports to be displayed by the gui.
	 * 
	 * @return The compilation of all the reports from all the flights. seperated by a two new lines
	 */
	public String generateReports() {
		// Follow this pattern
		//		Flight code: #
		//		Number of Passengers: #
		//		Total Baggage Weight: #
		//		Total Baggage Volume: #
		//		Total Excess Fees: #
		//		Capacity Exceeded: yes/no
		String finalReport = "";
		for(Flight f: flights.getValues()){
			finalReport += f.generateReport()+"\n";
		}
		
		//write to file
		try {
			PrintWriter out = new PrintWriter("report.txt");
			out.print(finalReport);
			out.close();
		} catch (FileNotFoundException e) {
		}
		
		//return for GUI
		return finalReport;


	}
}
