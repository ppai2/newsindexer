
package edu.buffalo.cse.irf14.analysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.logging.*;

public class DateFilter extends TokenFilter {

	public final Logger logger = Logger.getLogger(DateFilter.class.getName());
	TokenStream stream = new TokenStream();
	
	public DateFilter(TokenStream stream) {
		super(stream);
		this.stream = stream;
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (stream.hasNext()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TokenStream getStream() {
		//TokenStream tts = checkDate(stream);
		ListIterator<Token> streamIt = stream.getTsIterator();
		String tsStr = "";
		ArrayList<String> tsList = new ArrayList<String>();
		
		while (streamIt.hasNext()) {
			Token t = streamIt.next();
			String termText = t.getTermText();
			tsStr += termText + " ";
			tsList.add(termText);
		}
		tsStr = tsStr.trim();
		tsList = extractDate(tsStr, tsList);
		tsList = extractTime(tsStr, tsList);
		
		TokenStream dateStream = new TokenStream();
		for (String s: tsList) {
			Token t = new Token();
			t.setTermText(s);
			dateStream.addToken(t);
		}
		
		return dateStream;
	}
	
	public ArrayList<String> extractDate(String tsStr, ArrayList<String> tsList) {
		String str = "";
		String year = "";
		String month = "";
		String day = "";
		String dateStr = "";
		boolean foundDate = false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		HashMap<String, Integer> monthNumber = new HashMap<String, Integer>();
		monthNumber.put("Jan", 1);
		monthNumber.put("January", 1);
		monthNumber.put("Feb", 2);
		monthNumber.put("February", 2);
		monthNumber.put("Mar", 3);
		monthNumber.put("March", 3);
		monthNumber.put("Apr", 4);
		monthNumber.put("April", 4);
		monthNumber.put("May", 5);
		monthNumber.put("Jun", 6);
		monthNumber.put("June", 6);
		monthNumber.put("Jul", 7);
		monthNumber.put("July", 7);
		monthNumber.put("Aug", 8);
		monthNumber.put("August", 8);
		monthNumber.put("Sep", 9);
		monthNumber.put("September", 9);
		monthNumber.put("Oct", 10);
		monthNumber.put("October", 10);
		monthNumber.put("Nov", 11);
		monthNumber.put("November", 11);
		monthNumber.put("Dec", 12);
		monthNumber.put("December", 12);
		
		ArrayList<String> monthsList = new ArrayList<String>();
		monthsList.add("Jan");
		monthsList.add("January");
		monthsList.add("Feb");
		monthsList.add("February");
		monthsList.add("Mar");
		monthsList.add("March");
		monthsList.add("Apr");
		monthsList.add("April");
		monthsList.add("May");
		monthsList.add("Jun");
		monthsList.add("June");
		monthsList.add("Jul");
		monthsList.add("July");
		monthsList.add("Aug");
		monthsList.add("August");
		monthsList.add("Sept");
		monthsList.add("September");
		monthsList.add("Oct");
		monthsList.add("October");
		monthsList.add("Nov");
		monthsList.add("November");
		monthsList.add("Dec");
		monthsList.add("December");
		
		for (String m : monthsList) {
			if (tsList.contains(m)) {
				// MMMM found
				foundDate = true;
				int mIndex = tsList.indexOf(m);
				String before = "";
				String after = "";
				String mAfter = "";
				boolean hasBefore = true;
				boolean hasAfter = true;
				boolean hasmAfter = true;
				boolean commaBefore = false;
				boolean commaAfter = false;
				boolean commamAfter = false;
				boolean commaM = false;
				int beforeInt = 0;
				int afterInt = 0;
				int mAfterInt = 0;
				
				if (mIndex > 0)
					before = tsList.get(mIndex-1).trim();
				else
					hasBefore = false;
				
				if (mIndex+1 < tsList.size())
					after = tsList.get(mIndex+1).trim();
				if (mIndex+2 < tsList.size())
					mAfter = tsList.get(mIndex+2).trim();
				
				if (m.endsWith(",")) {
					commaM = true;
				}
				if (before.endsWith(",")) {
					commaBefore = true;
					before = before.substring(0, before.length()-1);
				}
				if (after.endsWith(",")) {
					commaAfter = true;
					after = after.substring(0, after.length()-1);
				}
				if (mAfter.endsWith(",")) {
					commamAfter = true;
					mAfter = mAfter.substring(0, mAfter.length()-1);
				}
				
				try {
					beforeInt = Integer.parseInt(before);
				} catch (NumberFormatException nfe) {
					// before is not a number
					hasBefore = false;
				}
				
				try {
					afterInt = Integer.parseInt(after);
				} catch (NumberFormatException nfe) {
					// after is not a number
					hasAfter = false;
				}
				
				try {
					mAfterInt = Integer.parseInt(mAfter);
				} catch (NumberFormatException nfe) {
					// mAfter is not a number
					hasmAfter = false;
				}
				// --------------------------------------------------------------------------------------------------------------
				if (hasBefore && hasAfter && !hasmAfter) {
					// BEFORE MONTH AFTER
					
					if (before.matches("\\d\\d\\d\\d")) {
						// BEFORE is YEAR
						year = before;
					} else if (before.matches("\\d\\d")) {
						// BEFORE is double-digit DAY
						day = before;
					} else {
						// BEFORE is single-digit DAY
						day = "0" + before;
					}
					
					if (after.matches("\\d\\d\\d\\d")) {
						// AFTER is YEAR
						year = after;
					} else if (after.matches("\\d\\d")) {
						// AFTER is double-digit DAY
						day = after;
					} else {
						// AFTER is single-digit DAY
						day = "0" + after;
					}
					
					int monthNum = monthNumber.get(m);
					if (monthNum < 10)
						month = "0" + monthNum;
					else
						month = "" + monthNum;
					
					if (year == "")
						year = "1900";
					if (day == "")
						day = "01";
					
					dateStr = year + month + day;
					
					if (commaAfter)
						dateStr += ",";
					
					tsList.add(mIndex-1, dateStr);
					tsList.remove(mIndex);
					tsList.remove(mIndex);
					tsList.remove(mIndex);
					
				} else if (!hasBefore && hasAfter && hasmAfter) {
					// MONTH AFTER mAFTER
					
					if (after.matches("\\d\\d\\d\\d")) {
						// AFTER is YEAR
						year = after;
					} else if (after.matches("\\d\\d")) {
						// AFTER is double-digit DAY
						day = after;
					} else {
						// AFTER is single-digit DAY
						day = "0" + after;
					}
					
					if (mAfter.matches("\\d\\d\\d\\d")) {
						// mAFTER is YEAR
						year = mAfter;
					} else if (mAfter.matches("\\d\\d")) {
						// mAFTER is double-digit DAY
						day = mAfter;
					} else {
						// mAFTER is single-digit DAY
						day = "0" + mAfter;
					}
					
					int monthNum = monthNumber.get(m);
					if (monthNum < 10)
						month = "0" + monthNum;
					else
						month = "" + monthNum;
					
					if (year == "")
						year = "1900";
					if (day == "")
						day = "01";
					
					dateStr = year + month + day;
					if (commamAfter)
						dateStr += ",";
					
					tsList.add(mIndex, dateStr);
					tsList.remove(mIndex+1);
					tsList.remove(mIndex+1);
					tsList.remove(mIndex+1);
					
				} else if (hasBefore && !hasAfter && !hasmAfter) {
					// BEFORE MONTH
					
					if (before.matches("\\d\\d\\d\\d")) {
						// BEFORE is YEAR
						year = before;
					} else if (before.matches("\\d\\d")) {
						// BEFORE is double-digit DAY
						day = before;
					} else {
						// BEFORE is single-digit DAY
						day = "0" + before;
					}
					
					int monthNum = monthNumber.get(m);
					if (monthNum < 10)
						month = "0" + monthNum;
					else
						month = "" + monthNum;
					
					if (year == "")
						year = "1900";
					if (day == "")
						day = "01";
					
					dateStr = year + month + day;
					
					if (commaM)
						dateStr += ",";
					
					tsList.add(mIndex-1, dateStr);
					tsList.remove(mIndex);
					tsList.remove(mIndex);
					
				} else if (!hasBefore && hasAfter && !hasmAfter) {
					// MONTH AFTER
					
					if (after.matches("\\d\\d\\d\\d")) {
						// AFTER is YEAR
						year = after;
					} else if (after.matches("\\d\\d")) {
						// AFTER is double-digit DAY
						day = after;
					} else {
						// AFTER is single-digit DAY
						day = "0" + after;
					}
					
					int monthNum = monthNumber.get(m);
					if (monthNum < 10)
						month = "0" + monthNum;
					else
						month = "" + monthNum;
					
					if (year == "")
						year = "1900";
					if (day == "")
						day = "01";
					
					dateStr = year + month + day;
					
					if (commaAfter)
						dateStr += ",";
					
					tsList.add(mIndex, dateStr);
					tsList.remove(mIndex+1);
					tsList.remove(mIndex+1);
				}
				
			}
		}
		
		if (foundDate)
			return tsList;
		else {
			ArrayList<String> itList = new ArrayList<String>();
			for (String s: tsList) {
				itList.add(s);
			}
			
			for (String s : itList) {
				if (s.equalsIgnoreCase("BC") || s.equalsIgnoreCase("B.C.") || s.equalsIgnoreCase("AD") || s.equalsIgnoreCase("A.D.")) {
					
					int bcIndex = tsList.indexOf(s);
					String before = "";
					int beforeInt;
					boolean hasBefore = true;
					boolean hasAfter = true;
					String after = "";
					int afterInt;
					
					if (bcIndex > 0)
						before = tsList.get(bcIndex-1);
					else
						hasBefore = false;
					
					if (bcIndex < tsList.size()-1)
						after = tsList.get(bcIndex+1);
					else
						hasAfter = false;
					
					if (hasBefore) {
						try {
							beforeInt = Integer.parseInt(before);
						} catch (NumberFormatException nfe) {
							// BEFORE is not a number
							hasBefore = false;
						}
					}
					
					if (hasAfter) {
						try {
							afterInt = Integer.parseInt(after);
						} catch (NumberFormatException nfe) {
							// AFTER is not a number
							hasAfter = false;
						}
					}
					
					if (hasBefore && !hasAfter) {
						if (before.matches("\\d"))
							year = "000" + before;
						else if (before.matches("\\d\\d"))
							year = "00" + before;
						else if (before.matches("\\d\\d\\d"))
							year = "0" + before;
						else
							year = before;
						
						if (month == "")
							month = "01";
						if (day == "")
							day = "01";
						dateStr = year + month + day;
						if (s.equalsIgnoreCase("BC") || s.equalsIgnoreCase("B.C."))
							dateStr = "-" + dateStr;
						
						tsList.add(bcIndex-1, dateStr);
						tsList.remove(bcIndex);
						tsList.remove(bcIndex);
						
					} else if (!hasBefore && hasAfter) {
						if (after.matches("\\d"))
							year = "000" + after;
						else if (after.matches("\\d\\d"))
							year = "00" + after;
						else if (after.matches("\\d\\d\\d"))
							year = "0" + after;
						else
							year = after;
						
						if (month == "")
							month = "01";
						if (day == "")
							day = "01";
						dateStr = year + month + day;
						if (s.equalsIgnoreCase("BC") || s.equalsIgnoreCase("B.C."))
							dateStr = "-" + dateStr;
						
						tsList.add(bcIndex, dateStr);
						tsList.remove(bcIndex+1);
						tsList.remove(bcIndex+1);
					}
				} else if (s.contains("BC") || s.contains("bc") || s.contains("AD") || s.contains("ad")) {
					
					boolean hasPeriod = false;
					boolean hasComma = false;
					boolean hasDate = false;
					boolean isBc = false;
					int index = -1;
					index = tsList.indexOf(s);
					
					if (s.endsWith("AD.") || s.endsWith("ad.") || s.endsWith("BC.") || s.endsWith("bc.")) {
						hasPeriod = true;
						s = s.substring(0, s.length()-1).trim();
					}
					if (s.endsWith("AD,") || s.endsWith("ad,") || s.endsWith("BC,") || s.endsWith("bc,")) {
						hasComma = true;
						s = s.substring(0, s.length()-1).trim();
					}
					
					if (s.matches(".*\\d[AD].*")) {
						hasDate = true;
						year = s.substring(0, s.lastIndexOf("AD"));
					} else if (s.matches(".*\\d[ad].*")) {
						hasDate = true;
						year = s.substring(0, s.lastIndexOf("ad"));
					} else if (s.matches(".*\\d[BC].*")) {
						hasDate = true;
						isBc = true;
						year = s.substring(0, s.lastIndexOf("BC"));
					} else if (s.matches(".*\\d[bc].*")) {
						hasDate = true;
						isBc = true;
						if (s.lastIndexOf("bc") < s.length() && s.lastIndexOf("bc") > -1)
							year = s.substring(0, s.lastIndexOf("bc"));
					}
					if (year.length() == 1) {
						year = "000" + year;
					} else if (year.length() == 2) {
						year = "00" + year;
					} else if (year.length() == 3) {
						year = "0" + year;
					}
					
					if (year != "") {
						if (month == "")
							month = "01";
						if (day == "")
							day = "01";
					}
					
					dateStr = year + month + day;
					
					if (isBc)
						dateStr = "-" + dateStr;
					if (hasComma)
						dateStr += ",";
					else if (hasPeriod)
						dateStr += ".";
					
					if (hasDate) {
						tsList.add(index, dateStr);
						tsList.remove(index+1);
					}
				} else if (s.matches("\\d\\d\\d\\d\\.") || s.matches("\\d\\d\\d\\d\\,") || s.matches("\\d\\d\\d\\d")) {
					boolean hasComma = false;
					boolean hasPeriod = false;
					int index = tsList.indexOf(s);
					
					if (s.endsWith(","))
						hasComma = true;
					else if (s.endsWith("."))
						hasPeriod = true;
					
					if (hasComma || hasPeriod) {
						s = s.substring(0, s.length()-1);
					}
					year = s;
					
					if (hasComma)
						year += ",";
					else if (hasPeriod)
						year += ".";
					
					month = "01";
					day = "01";
					
					dateStr = year + month + day;
					
					tsList.add(index, dateStr);
					tsList.remove(index+1);
					
				} else if (s.matches("\\d\\d\\d\\d\\-\\d\\d.") || s.matches("\\d\\d\\d\\d\\-\\d\\d,") || s.matches("\\d\\d\\d\\d\\-\\d\\d")) {
					boolean hasComma = false;
					boolean hasPeriod = false;
					boolean validDate = false;
					int index = tsList.indexOf(s);
					String startYear = "";
					int sYear = 0;
					int eYear = 0;
					String endYear = "";
					
					if (s.endsWith(","))
						hasComma = true;
					else if (s.endsWith("."))
						hasPeriod = true;
					
					if (hasComma || hasPeriod) {
						s = s.substring(0, s.length()-1);
					}
					
					startYear = s.substring(0, s.lastIndexOf("-"));
					endYear = s.substring(s.lastIndexOf("-")+1);
					
					sYear = Integer.parseInt(startYear.substring(startYear.length()-2));
					eYear = Integer.parseInt(endYear);
					
					if (eYear > sYear)
						validDate = true;
					
					if (validDate) {
						month = "01";
						day = "01";
						
						endYear = startYear.substring(0, startYear.length()-2) + endYear;
						
						dateStr = startYear + month + day + "-" + endYear + month + day;
						if (hasComma)
							dateStr += ",";
						else if (hasPeriod)
							dateStr += ".";
						
						tsList.add(index, dateStr);
						tsList.remove(index+1);
					}
				} else if (s.matches("\\d\\d\\\\d\\d\\\\d\\d\\d\\d") || s.matches("\\d\\d\\-\\d\\d\\-\\d\\d\\d\\d") || s.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d") || 
						s.matches("\\d\\d\\\\d\\d\\\\d\\d\\d\\d,") || s.matches("\\d\\d\\-\\d\\d\\-\\d\\d\\d\\d,") || s.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d,") ||
						s.matches("\\d\\d\\\\d\\d\\\\d\\d\\d\\d.") || s.matches("\\d\\d\\-\\d\\d\\-\\d\\d\\d\\d.") || s.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d.")) {
					boolean hasComma = false;
					boolean hasPeriod = false;
					
					int index = tsList.indexOf(s);
					
					if (s.endsWith(","))
						hasComma = true;
					else if (s.endsWith("."))
						hasPeriod = true;
					
					if (s.contains("\\")) {
						String[] dateHolder = s.split("\\");
						month = dateHolder[0];
						day = dateHolder[1];
						year = dateHolder[2];
					} else if (s.contains("-")) {
						String[] dateHolder = s.split("-");
						month = dateHolder[0];
						day = dateHolder[1];
						year = dateHolder[2];
					} else if (s.contains("/")) {
						String[] dateHolder = s.split("/");
						month = dateHolder[0];
						day = dateHolder[1];
						year = dateHolder[2];
					}
					
					dateStr = year + month + day;
					if (hasComma)
						dateStr += ",";
					else if (hasPeriod)
						dateStr += ".";
					
					tsList.add(index, dateStr);
					tsList.remove(index+1);
				}
			}
		}
		
		return tsList;
	}
	
	public ArrayList<String> extractTime(String tsStr, ArrayList<String> tsList) {
		
		boolean validTime = false;
		String hour = "";
		String minute = "";
		String timeStr = "";
		
		ArrayList<String> timeList = new ArrayList<String>();
		timeList.add("am");
		timeList.add("am,");
		timeList.add("am.");
		timeList.add("AM");
		timeList.add("AM,");
		timeList.add("AM.");
		timeList.add("a.m.");
		timeList.add("a.m.,");
		timeList.add("A.M.");
		timeList.add("A.M.,");
		
		timeList.add("pm");
		timeList.add("pm,");
		timeList.add("pm.");
		timeList.add("PM");
		timeList.add("PM,");
		timeList.add("PM.");
		timeList.add("p.m.");
		timeList.add("p.m.,");
		timeList.add("P.M.");
		timeList.add("P.M.,");
		
		for (String ampm: timeList) {
			if (tsList.contains(ampm)) {
				int ampmIndex = tsList.indexOf(ampm);
				String before = "";
				boolean hasBefore = false;
				boolean isAm = false;
				boolean isPm = false;
				boolean hasComma = false;
				boolean hasPeriod = false;
				
				if (ampm.endsWith(","))
					hasComma = true;
				else if (ampm.endsWith(".") && !(ampm.endsWith(".m.") || ampm.endsWith(".M.")))
					hasPeriod = true;
				
				if (ampm.contains("p") || ampm.contains("P"))
					isPm = true;
				else if (ampm.contains("a") || ampm.contains("A"))
					isAm = true;
				
				if (ampmIndex > 0) {
					hasBefore = true;
					before = tsList.get(ampmIndex-1).trim();
				}
				
				if (before.matches("\\d\\d\\:\\d\\d") || before.matches("\\d\\:\\d\\d") || before.matches("\\d") || before.matches("\\d\\d")) {
					validTime = true;
					
					if (before.contains(":")) {
						hour = before.substring(0, before.indexOf(":"));
						minute = before.substring(before.indexOf(":")+1);
						if (hour.length() == 1)
							hour = "0" + hour;
					} else {
						if (before.length() == 1)
							hour = "0" + before;
						else if (before.length() == 2)
							hour = before;
						minute = "00";
					}
				}
				
				if (isPm && validTime) {
					int hourInt = Integer.parseInt(hour);
					hourInt += 12;
					hour = "" + hourInt;
				}
				
				if (validTime) {
					timeStr = hour + ":" + minute + ":00";
					
					if (hasComma)
						timeStr += ",";
					else if (hasPeriod)
						timeStr += ".";
					
					tsList.add(ampmIndex-1, timeStr);
					tsList.remove(ampmIndex);
					tsList.remove(ampmIndex);
				}
			}
			
			for (String t: tsList) {
				if (t.endsWith(ampm)) {
					int tIndex = tsList.indexOf(t);
					boolean isPm = false;
					boolean hasComma = false;
					boolean hasPeriod = false;
					
					if (t.endsWith(","))
						hasComma = true;
					if (t.endsWith(".") && !(t.endsWith(".m.") || t.endsWith(".M.")))
						hasPeriod = true;
					
					if (t.contains("P") || t.contains("p"))
						isPm = true;
					
					if (t.matches("\\d\\d\\:\\d\\d"+ampm) || t.matches("\\d\\:\\d\\d"+ampm) || t.matches("\\d"+ampm) || t.matches("\\d\\d"+ampm)) {
						validTime = true;
						String tempTime = t.substring(0, t.indexOf(ampm));
						
						if (tempTime.contains(":")) {
							hour = tempTime.substring(0, tempTime.indexOf(":"));
							minute = tempTime.substring(tempTime.indexOf(":")+1);
							if (hour.length() == 1)
								hour = "0" + hour;
						} else {
							if (tempTime.length() == 1)
								hour = "0" + tempTime;
							else if (tempTime.length() == 2)
								hour = tempTime;
							minute = "00";
						}
						
						if (isPm && validTime) {
							int hourInt = Integer.parseInt(hour);
							hourInt += 12;
							hour = "" + hourInt;
						}
						
						if (validTime) {
							timeStr = hour + ":" + minute + ":00";
							
							if (hasComma)
								timeStr += ",";
							else if (hasPeriod)
								timeStr += ".";
							
							tsList.add(tIndex, timeStr);
							tsList.remove(tIndex+1);
						}
					}
				}
			}
		}
		
		return tsList;
	}
	
}