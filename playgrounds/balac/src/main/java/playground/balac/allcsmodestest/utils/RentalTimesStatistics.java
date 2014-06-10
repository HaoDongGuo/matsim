package playground.balac.allcsmodestest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.matsim.core.utils.io.IOUtils;

public class RentalTimesStatistics {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		final BufferedReader readLink = IOUtils.getBufferedReader("C:/Users/balacm/Desktop/STRC_Temp/CSTW_Stats.txt");
		int[] rentalTimes = new int[30];
		int[] distance = new int[50];

		int[] rentalStart = new int[35];
		Set<Double> bla = new HashSet<Double>();

		//final BufferedReader readLink = IOUtils.getBufferedReader("C:/Users/balacm/Desktop/coordinates.txt");
		String s = readLink.readLine();
		s = readLink.readLine();
		int count = 0;
		int count1 = 0;
		double di = 0.0;
		while(s != null) {
			String[] arr = s.split("\\s");
			double time = Double.parseDouble(arr[6]);
			distance[(int)(time*0.9/130.0)]++;
			bla.add(Double.parseDouble(arr[0]));
			double startTime = Double.parseDouble(arr[1]);
			rentalStart[(int)((startTime) / 3600)]++;			

			double endTime = Double.parseDouble(arr[2]);
			rentalTimes[(int)((endTime - startTime) / 3600)]++;
			di += Double.parseDouble(arr[5]);
			if (endTime - startTime < 1800) 
				count1++;
			count++;
			s = readLink.readLine();		
			
		}
		System.out.println(di/count);
		for (int i = 0; i < rentalTimes.length; i++) 
			System.out.println((double)rentalTimes[i]/(double)count * 100.0);
		System.out.println(count1);	
		for (int i = 0; i < distance.length; i++) 
			System.out.println((double)distance[i]/(double)count * 100.0);
		System.out.println();
		for (int i = 0; i < rentalStart.length; i++) 
			System.out.println((double)rentalStart[i]/(double)count * 100.0);
		
	}

}