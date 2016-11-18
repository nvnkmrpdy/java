package com.geo.tz.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.LatLng;

/**
 * Reads a csv file with UTC datetime, latitude and longitude columns and generates an output file with datetime localized as per the timezone of latitude and longitude data.
 * @author Naveen Kumar
 *
 */
public class LatLongDemo {
	
	private static final String COMMA = ",";
	private static final String SPACE = " ";
	private static final String TEE = "T";
	private static final String NEW_LINE = "\n";
	private static final String UTC = "UTC";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String API_KEY = "AIzaSyDvhAalGNI-uo1x_EtxwiH6tZ9mVyf7_ZY";
    private static GeoApiContext context = new GeoApiContext().setApiKey(API_KEY)
													    		.setQueryRateLimit(3)
													            .setConnectTimeout(10, TimeUnit.SECONDS)
													            .setReadTimeout(10, TimeUnit.SECONDS)
													            .setWriteTimeout(10, TimeUnit.SECONDS);

	public static void main(String[] args) throws Exception {
		
		if(args.length < 2) {
			printUsage();
		}
		
		File inputFile = new File(args[0]);
		// Input file doesn't exist.
		if(!inputFile.exists()) {
			System.err.println("The input file " + args[0] + " does not exist.");
			System.exit(0);
		}
		
		File outputFile = new File(args[1]);
		try {
			if(outputFile.createNewFile()) {
				System.out.println("The output file " + args[1] + " is successfully created.");
			} else {
				System.out.println("The output file " + args[1] + " already exists. Its content will be overwritten.");
			}
		} catch(IOException ex) {
			System.err.println("Unable to create the output file: " + args[1]);
			ex.printStackTrace();
			System.exit(0);
		} catch(SecurityException ex) {
			System.err.println("Can not create the output file " + args[1] + ". Access denied.");
			ex.printStackTrace();
			System.exit(0);
		}
		
		// Read input file and parse the data.
		// Using try-with resources for auto-closure of streams.
		try (BufferedReader bufferedFileReader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
			
			String lineRead = null;
			
			while ((lineRead = bufferedFileReader.readLine()) != null) {
				bufferedWriter.write(geoCodeAndConvertTime(lineRead.trim()));
			}
			
			System.out.println("Output written to the file " + args[1]);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	// Prints the usage to run the exported jar application.
	private static void printUsage() {
		System.out.println("Usage: java -jar LocalizedDateTimeConverter.jar <input_file> <output_file>");
		System.exit(0);
	}
	
	/**
	 * Converts UTC datetime to localized datetime as per geo data.
	 * @param input
	 * @return String
	 */
	private static String geoCodeAndConvertTime(String input) {
		
		StringBuilder sb = new StringBuilder();
		
		try {
			
			String[] inputData = input.split(COMMA);
			String dateTime = inputData[0];
			double lat = Double.parseDouble(inputData[1]);
			double lng = Double.parseDouble(inputData[2]);
			
			LatLng latLng = new LatLng(lat, lng);

			// Invoke Google's geocoding timezone API to get the timezone for the location.
			PendingResult<TimeZone> pendingResult = TimeZoneApi.getTimeZone(context, latLng);
			TimeZone timeZone = pendingResult.await();
			
			// Parse the input date as a date object.
			Date date = getDateFromString(dateTime, TimeZone.getTimeZone(UTC));
			
			// Convert between timezones.
			Date dateInOutTimeZone = getDateFromString(getDateString(date, timeZone));
			
			// Generate entry for the output.
			sb.append(input).append(COMMA).append(timeZone.getID()).append(COMMA).append(getDateString(dateInOutTimeZone)).append(NEW_LINE);
			
		} catch(Exception e) {
			System.err.println("Unable to parse the line: " + input);
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	/**
	 * Creates a date object on parsing a string date as per input timezone.
	 * @param strDate
	 * @param tz
	 * @return Date
	 * @throws Exception
	 */
	private static Date getDateFromString(String strDate, TimeZone tz) throws Exception {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setTimeZone(tz);
		return df.parse(strDate);
	}
	
	/**
	 * Creates a date object on parsing a string date.
	 * @param strDate
	 * @return Date
	 * @throws Exception
	 */
	private static Date getDateFromString(String strDate) throws Exception {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.parse(strDate);
	}
	
	/**
	 * Generates date as string fro an input date object and timezone.
	 * @param date
	 * @param tz
	 * @return String
	 * @throws Exception
	 */
	private static String getDateString(Date date, TimeZone tz) throws Exception {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setTimeZone(tz);
		return df.format(date);
	}
	
	/**
	 * Generates date as string from an input date object.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private static String getDateString(Date date) throws Exception {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(date).replace(SPACE, TEE);
	}
}
