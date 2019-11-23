package com.stevetest.app;

/**
 * Get Temperature from w1 device in the RaspberryPi.
 * 
 * @author Steven Stier
 * @version 0.0.1
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class App{
	public static void main(final String[] args) throws InterruptedException, IOException {
		System.out.println("Press Any Key to exit reading.");
		final String w1DevicesPath = "/sys/bus/w1/devices/"; // path to where to find the one Wire device Files
		final String deviceName = "28-0000081bf384"; // device name //
		final String filename = "/w1_slave"; // Name of file to ready
		final String fullPathandFile = w1DevicesPath + deviceName + filename;
		String strTemp;
		// get java path to device file
		try {
			final Path full_path = FileSystems.getDefault().getPath(fullPathandFile);
			do {
				strTemp = readTempFromFile(full_path);
				System.out.println(strTemp);
				// wait 2 seconds
				Thread.sleep(2000);
			} while (System.in.available() == 0);
		} catch (final Exception ex) {
			System.out.println("Exception in Main:" + ex.getMessage());
		}
		System.out.println("Exiting application.");

	}

	public static String readTempFromFile(final Path pathDeviceFile) {
		int iniPos, endPos;
		String strTemp = "";
		final String strTempIdentifier = "t=";
		String strCrc = "";
		final String strCrcIdentifier = "crc=";

		double tempc = 0.0d;
		double tempf = 0.0d;
		List<String> lines; // used to store each line of in the file
		// System.out.println("Starting readTempfromFIle");
		try {
			// Get all the lines of the text from the file
			lines = Files.readAllLines(pathDeviceFile, Charset.defaultCharset());
			// Scroll through each line looking for the idendifiers
			for (final String line : lines) {
				// Get the crc Element
				if (line.contains(strCrcIdentifier)) {
					iniPos = line.indexOf(strCrcIdentifier) + 4;
					endPos = iniPos + 2;
					strCrc = line.substring(iniPos, endPos);
				}
				// Get the Temperature from the file
				if (line.contains(strTempIdentifier)) {
					iniPos = line.indexOf(strTempIdentifier) + 2;
					endPos = line.length();
					strTemp = line.substring(iniPos, endPos);
				}
			}
		} catch (final IOException ex) {
            System.out.println("Error while reading file:" + ex.getMessage());
			}
		if (strCrc.equals("00")) {	
	  		System.out.println("Temperature signal crc is OO"); 
	  		return "??";
        	}
		else {
			// TODO - might need the check to make sure strTemp contains an integer
			tempc = Double.parseDouble(strTemp) / 1000;
			tempf = (tempc*9/5) + 32.0;   // Concert to deg f
			tempf = tempf * 10.0; // these next three line should round the temp to 1 decimal place
			tempf = Math.round(tempf);
			tempf = tempf/10.0;

			return "Temperature="+ tempf + " Deg F";
            }
    	}
}