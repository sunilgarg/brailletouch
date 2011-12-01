package com.typingtest;

import java.lang.Math;

public class StatsCalc {

	public static int[] statsMSD(String sentence, String typed) {
		int i, j;
		String a = " " + sentence;
		String b = " " + typed;

		// minimum string distance (insertions, deletions, and substitutions)
		int[][] msd = new int[a.length()][b.length()];
		// (insertions only ??)
		int[][] mfa = new int[a.length()][b.length()];

		int min, sub, notInA;

		/* initialization */
		for (i = 0; i < a.length(); i++) {
			msd[i][0] = i;
			mfa[i][0] = 0;
		}
		for (j = 0; j < b.length(); j++) {
			msd[0][j] = j;
			mfa[0][j] = j;
		}

		/* calculation */
		for (i = 1; i < a.length(); i++) {
			for (j = 1; j < b.length(); j++) {
				min = msd[i - 1][j] + 1;
				notInA = msd[i][j - 1] + 1;
				sub = msd[i - 1][j - 1];
				if (a.charAt(i) != b.charAt(j)) {
					sub++;
				}
				// mfa[i][j] = mfa[i - 1][j];
				if (sub < min) {
					min = sub;
					// mfa[i][j] = mfa[i - 1][j - 1];
				}
				if (notInA < min) {
					min = notInA;
					// mfa[i][j] = mfa[i][j - 1] + 1;
				}
				msd[i][j] = min;
			}
		}

		for (i = 1; i < a.length(); i++) {
			for (j = 1; j < b.length(); j++) {
				min = mfa[i - 1][j] + 1;
				notInA = mfa[i][j - 1] + 1;
				sub = mfa[i - 1][j - 1];
				if (a.charAt(i) != b.charAt(j)) {
					// sub++;
				}
				if (sub < min) {
					min = sub;
				}
				if (notInA < min) {
					min = notInA;
				}
				mfa[i][j] = min;
			}
		}

		/*
		 * System.out.println("msd"); for (int u=0; u<a.length(); u++) { for
		 * (int v=0; v<b.length(); v++) System.out.print(msd[u][v] +" ");
		 * System.out.println(); } System.out.println();
		 */
		/*
		 * System.out.println("mfa"); for (int u=0; u<a.length(); u++) { for
		 * (int v=0; v<b.length(); v++) System.out.print(mfa[u][v] +" ");
		 * System.out.println(); } System.out.println();
		 */
		/* return value */
		int[] ret = new int[2];
		ret[0] = msd[a.length() - 1][b.length() - 1];
		ret[1] = mfa[a.length() - 1][b.length() - 1];
		//

		// System.out.println("msd = "+ret[0]+" mfa = "+ret[1]);
		return ret;
	}// end statsMSD (String,String)

	public static int statsF(String input) {
		// F = Fixes (e.g. control chars, like backspace)
		int ret = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == (char)8) {
				ret++;
			}
		}
		return ret;
	}// end statsF (Vector)

	public static int statsIF(String sentence, String typed) {
		// IF = Incorrect, but Fixed
		return typed.length() - statsF(typed);
	}// end statsIF (String,String,Vector)

	public static int statsC(String sentence, String typed) {
		// C = Correct input
		int[] msd = statsMSD(sentence, typed);
		// int length = Math.min(typed.length(), sentence.length());
		int length = Math.max(typed.length(), sentence.length());

		// return length - (msd[0] - msd[1]);
		return length - msd[0];
	}// end statsC (String,String)

	public static int statsINF(String sentence, String typed) {
		// INF = Incorrect, but Not Fixed
		return statsMSD(sentence, typed)[0];
	}// end statsINF (String,String)

	/**
	 * return value is {C, F, IF, INF}
	 * 
	 * input stream of keystrokes, breaks down into:
	 * 
	 * C = Correct F = Fixes (backspace, e.g. control chars) IF = Incorrect and
	 * Fixed INT F = Incorrect but Not Fixed
	 * 
	 */

	public static int[] statsALL(String sentence, String typed) {
		int[] ret = new int[4];

		ret[0] = statsC(sentence, typed);
		ret[1] = statsF(typed);
		ret[2] = statsIF(sentence, typed);
		ret[3] = statsINF(sentence, typed);
		return ret;
	}
	
	public static double getTotalError(String sentence, String typed) {
		int [] ret = statsALL(sentence, typed);
		double totalError = ((double) (ret[3] + ret[2])) / ((double) (ret[0] + ret[3] + ret[2]));
		return totalError;
	}


//		c = statsC(sentence, typed);
//		inf = statsINF(sentence, typed);
//		f = statsF(keyPresses);
//		ifix = statsIF(sentence, typed, keyPresses);
//
//		System.out.println("C = " + c + "\nINF = " + inf + "\nF = " + f	+ "\nIF = " + ifix);
//		System.out.println("Total Error: " + ((double) (inf + ifix)) / ((double) (c + inf + ifix)));

}// end class StatsCalc