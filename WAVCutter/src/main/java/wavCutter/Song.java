package main.java.wavCutter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Song {
	
	private int start = 0;
	private int end = 0;
	private String name = "";
	private static final String[] patterns = {"(\\d){1,2}(:)(\\d){1,2}(:)(\\d){1,2}","(\\d){1,2}(:)(\\d){1,2}"};
	private static final String validCharsString = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ _()";
	private static char[] validChars;
	
	public static void launcher(){
		validChars = validCharsString.toCharArray();
	}
	
	public Song(String s){
		this.parser(s);
	}

	public String toString(){
		return this.start + " - " + this.end + " : " + this.name;
	}
	
	private void parser(String s){
		Pattern r;
		Matcher m;
		
		for(String pattern: patterns){
			r = Pattern.compile(pattern);
			m = r.matcher(s);
			if(m.find()){
				this.start=parseDigits(m.group(0));
				this.name=formater(joinStrings(s.split(pattern))).trim();
				return;
		    }
		}
	}
	
	private static String formater(String s){
		char[] a = s.toCharArray();
		for(int i = 0; i < a.length; i++){
			if(!checkValid(a[i])){
				a[i] = ' ';
			}
		}
		return formaterrec(charArrayToString(a));
	}
	
	private static String charArrayToString(char[] a){
		String s = "";
		for(char c : a){
			s = s + c;
		}
		return s;
	}
	
	private static boolean checkValid(char c){
		for(char t : validChars){
			if(t == c) return true;
		}
		return false;
	}
	
	private static String formaterrec(String s){
		String t = joinStringsWith(s.split("  "), " ");
		if(t.equals(s)){
			return t;
		}else{
			return formaterrec(t);
		}
	}
	
	public static int parseDigits(String group) throws NumberFormatException{
		int res = 0;
		String[] sa = (group.split(":"));
		int p = sa.length-1;
		for(String s : sa){
			res = res + (Integer.parseInt(s) * power(60,p));
			p--;
		}
		return res;
	}
	
	private static int power(int b, int e){
		int res = 1;
		while(e > 0){
			res = res*b;
			e--;
		}
		return res;
	}

	private static String joinStrings(String[] split) {
		return joinStringsWith(split, "");
	}

	private static String joinStringsWith(String[] split, String deliminer) {
		try{
			String s = split[0];
			for(int i = 1; i < split.length; i++){
				s = s + deliminer + split[i];
			}
			return s;
		}catch(Exception ex){
			return null;
		}
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getStartS(){
		return parseS(this.start);
	}
	
	public static String parseS(int i){
		return ((int)i/3600) + ":" + toTwo((int)((i%3600)/60)) + ":" + toTwo(i%60);
	}
	
	public static String toTwo(int i){
		if(i < 10){
			return "0" + i;
		}
		return "" + i;
	}
	
	public String getEndS(){
		return parseS(this.end);
	}

	public static boolean isValid(Song song) {
		return song.getName().equals("") || song.getStart() == song.getEnd();
	}
}
