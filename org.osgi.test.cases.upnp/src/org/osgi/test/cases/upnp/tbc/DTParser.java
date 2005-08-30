package org.osgi.test.cases.upnp.tbc;

/**
 * Parsing java.util.Data in date of type iso8601 Not ready for Time Zones
 * 
 */
public class DTParser {
	private String	toParse;

	public DTParser(String toParse) {
		this.toParse = toParse;
	}

	public String parser() {
		String parsed = null;
		parsed = getYear() + getMonth_Date() + "T" + getTime();
		return parsed;
	}

	private String getYear() {
		int len = toParse.length();
		String yyyy = toParse.substring(len - 4, len);
		//System.out.println("YEAR IS: " + yyyy);
		return yyyy;
	}

	private String getMonth_Date() {
		String monthdate = null;
		String mon = null;
		String beforeMM = toParse.substring(4);
		String mm = beforeMM.substring(0, beforeMM.indexOf(" "));
		System.out.println("MONTH IS: " + mm);
		if (mm.equals("Jan")) {
			mon = "" + 01;
		}
		else
			if (mm.equals("Feb")) {
				mon = "" + 02;
			}
			else
				if (mm.equals("Mar")) {
					mon = "" + 03;
				}
				else
					if (mm.equals("Apr")) {
						mon = "" + 04;
					}
					else
						if (mm.equals("May")) {
							mon = "" + 05;
						}
						else
							if (mm.equals("Jun")) {
								mon = "" + 06;
							}
							else
								if (mm.equals("Jul")) {
									mon = "" + 07;
								}
								else
									if (mm.equals("Aug")) {
										mon = "#" + 8;
									}
									else
										if (mm.equals("Sep")) {
											mon = "#" + 9;
										}
										else
											if (mm.equals("Oct")) {
												mon = "" + 10;
											}
											else
												if (mm.equals("Nov")) {
													mon = "" + 11;
												}
												else
													if (mm.equals("Dec")) {
														mon = "" + 12;
													}
		String date = beforeMM.substring(beforeMM.indexOf(" ") + 1, beforeMM
				.indexOf(" ") + 3);
		System.out.println("DATE IS: " + date);
		if (mon.startsWith("#")) {
			String temp = mon.substring(0, 1);
			temp = "" + 0;
			mon = temp + mon.substring(1);
		}
		monthdate = mon + date;
		return monthdate;
	}

	private String getTime() {
		String time = null;
		String temp = null;
		temp = toParse.substring(toParse.indexOf(" "));
		temp = temp.substring(temp.indexOf(" ") + 1);
		temp = temp.substring(temp.indexOf(" ") + 1);
		String cur = temp.substring(temp.indexOf(" ") + 1);
		time = cur.substring(0, cur.indexOf(" "));
		System.out.println("TIME IS: " + time);
		return time;
	}
}