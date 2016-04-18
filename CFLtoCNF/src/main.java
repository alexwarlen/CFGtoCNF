import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.*;
import javax.swing.*;


public class main extends JFrame{
	
	
	
	public static ArrayList<ArrayList<String>> cflInput = new ArrayList<ArrayList<String>>();
	public static int numLines;
    public static ArrayList<String> extraNonTerminals = new ArrayList<String>();
    public static String[] extras =     {"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public static String[] mapExtras = {"", "","","","","","","","","","","","","","","",""};


	public static void main(String[] args) {
		extraNonTerminals.addAll(Arrays.asList(extras));
        Scanner keyboard = new Scanner(System.in);
		
		System.out.println("How many lines?");
		numLines = Integer.parseInt(keyboard.nextLine());
		
		for (int i = 0; i < numLines; i++){
			System.out.println("Enter non terminal");
			String nonTerminal = keyboard.nextLine();
			ArrayList<String> row = new ArrayList<String>();
			row.add(nonTerminal);
			String nextRule = "";
			while(!nextRule.equals("done"))
			{
				System.out.println("Enter next rule in row");
				nextRule = keyboard.nextLine();
				if(!nextRule.equals("done"))
				{
					row.add(nextRule);
				}
				
			}
			cflInput.add(row);
		}
        /*ArrayList<String> row1 = new ArrayList<String>();
        row1.add("C");
        row1.add("ACC");
        row1.add("aB");
        ArrayList<String> row2 = new ArrayList<String>();
        row2.add("A");
        row2.add("C");
        row2.add("B");
        ArrayList<String> row3 = new ArrayList<String>();
        row3.add("B");
        row3.add("b");
        row3.add("$");
        cflInput.add(row1);
        cflInput.add(row2);
        cflInput.add(row3);
        numLines = 3;
        ArrayList<String> row1 = new ArrayList<String>();
        row1.add("H");
        row1.add("aA");

        ArrayList<String> row2 = new ArrayList<String>();
        row2.add("A");
        row2.add("aB");
        row2.add("bB");
        row2.add("$");
        ArrayList<String> row3 = new ArrayList<String>();
        row3.add("B");
        row3.add("A");
        row3.add("c");
        cflInput.add(row1);
        cflInput.add(row2);
        cflInput.add(row3);
        numLines = 3;*/

        System.out.println("The CFL you have entered is:");
        printCurrentCFL();

        if(checkChomsky()){
            System.out.println("Your CFL is already in CNF:");
            printCurrentCFL();
            return;
        }


		//call new method
		newStart();
		System.out.println(cflInput.get(2).get(0));
		//printCurrentCFL();
		/*while(!checkChomsky())
		{
			//check all the things
			
		}
		System.out.println("All existences of B0: ");
		ArrayList<Integer[]> test = getPositions("B0");
		for (Integer[] pos : test){
			System.out.println(pos[0] + ", " + pos[1]);
		}*/
		// STEP 2: Remove epsilons not in 1st line
		moveE();

        // STEP 3: Remove single non-terminal -> single non-terminal
        removeRedundant();
        System.out.println("After remove redundant:");
        printCurrentCFL();

        System.out.println("After removing singles");
        removeSingles();

		printCurrentCFL();

        System.out.println("After removing triples: ");
        removeTriples();
        printCurrentCFL();

        System.out.println("After removing not-alone little letters");
        keepLoneTerminals();
        printCurrentCFL();

        removeDuplicateEntries();

        printCurrentCFL();
		System.out.println("In chomsky form: " + checkChomsky());
		//checkStart();
		
	}
	
	
	/*
	 * So, we have to eliminate A -> $ rules before adding the new start rule
	 * BECAUSE just copying over the first line to the top line won't be correct, see coursepack
	 */
	public static void checkStart()
	{
		
			//numLines++; //this is to account for the newline added
			
			ArrayList<String> row2 = new ArrayList<String>();
			for(int j=0; j < cflInput.get(1).size(); j++)
			{
				if(!(j == 0))
				{
					row2.add(cflInput.get(1).get(j));
				}
			}
			
			cflInput.add(0, row2);
			
	
			System.out.println("New CFL: ");
			//print out to check for correctness
			for (int i = 0; i < numLines; i++){
				for (int j = 0; j < cflInput.get(i).size(); j++){
					if (j == 1){
						System.out.print(" --> ");
					}
					if (j > 1){
						System.out.print(" | ");
					}
					System.out.print(cflInput.get(i).get(j));
					
				}
				System.out.print("\n");
			}
			
	}
	
	//check if we did the chomsky-ing
	private static boolean checkChomsky()
	{
		
		for (int i = 0; i < numLines; i++){
			//System.out.println("Made it again: " + cflInput.get(0).size());
			for (int j = 1; j < cflInput.get(i).size(); j++){//j==1
				int upperCount = 0;
				int lowerCount = 0;
                int epsilonCount = 0;
				
				String contents = cflInput.get(i).get(j);
				
				for (int x = 0; x < contents.length(); x++){
					char curLetter = contents.charAt(x);
					if (curLetter > 96 && curLetter < 123){
						//lowercase
						lowerCount++;
					}
					else if (curLetter > 64 && curLetter < 91){
						upperCount++;
					}
                    else if (curLetter == '$'){
                        epsilonCount++;
                    }
					if ((curLetter == '$') && (i > 0)){
						return false;
					}
					
				}
				if (!((upperCount == 2 && lowerCount == 0) || (lowerCount == 1 && upperCount == 0) || (epsilonCount == 1 && upperCount == 0 && lowerCount == 0 && i == 0))){
					return false;
				}
				
				
			}
			
		}
		
		
		return true;
	}
	
	public static void printCurrentCFL(){
		for (int i = 0; i < numLines; i++){
			for (int j = 0; j < cflInput.get(i).size(); j++){
				if (j == 1){
					System.out.print(" --> ");
				}
				if (j > 1){
					System.out.print(" | ");
				}
				System.out.print(cflInput.get(i).get(j));
				
			}
			System.out.print("\n");
		}
	}
	
	private static void newStart()
	{
		numLines++;
		
		ArrayList<String> row2 = new ArrayList<String>();
		String newRule = cflInput.get(0).get(0);
		String newStart = newRule + '0';
		
		row2.add(newStart);
		row2.add(newRule);
		
		cflInput.add(0, row2);
		
	}
	
	public static boolean properEFormat()
	{
		/*for (int i = 0; i < numLines; i++){
			//System.out.println("Made it again: " + cflInput.get(0).size());
			for (int j = 1; j < cflInput.get(i).size(); j++){//j==1
				
				
				String contents = cflInput.get(i).get(j);
				
				for (int x = 0; x < contents.length(); x++){
					char curLetter = contents.charAt(x);
					
					if ((curLetter == '$') && (i > 0)){
						return false;
					}
					
				}
				
			}
			
		}*/
		
		for (int i = 0; i < numLines; i++)
		{
			for (int j = 1; j < cflInput.get(i).size(); j++)
			{
				String contents = cflInput.get(i).get(j);
				if((contents.equals("$")) && (i > 0)){
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public static int[] ePosition(int startLine){
		startLine = numLines - 1;
        int[] tuple = new int[2];
		
		for (int i = startLine; i >= 0; i--){
			
			for (int j = 1; j < cflInput.get(i).size(); j++){//j==1
				String contents = cflInput.get(i).get(j);
				
				for (int x = 0; x < contents.length(); x++){
					char curLetter = contents.charAt(x);
					
					if ((curLetter == '$')){
						tuple[0] = i;
						tuple[1] = j;
						return tuple;
					}
					
				}
				
			}
			
		}
		
		return tuple;
	}
	
	public static void moveE()
	{
        int startLine = numLines-1;
		while (!properEFormat()) {
			int[] ePos = ePosition(startLine);
			printCurrentCFL();
			System.out.println("E found at position: " + ePos[0] +", " + ePos[1]);
			if (ePos.length == 0){
				return;
			}
			// get the start variable of the line with the epsilon
			String startVar = cflInput.get(ePos[0]).get(0);
			// get all positions in the CFG with that variable on the right hand side
			ArrayList<Integer[]> positions = getPositions(startVar, startLine);
			
			for (Integer[] tuple : positions){
				// Inspect how to get the end of the string, getting an out of bounds exception because it's going too far... :( 
				int startOfNonTerm = tuple[2];
				int endOfNonTerm = tuple[2] + tuple[3];
				
				/*
				 * Printing out tuple contents
				 */
				System.out.println("Number of positions found: " + positions.size());
				for(int a : tuple){
					System.out.println("Tuple: " + a);
				}
                //if (endOfNonTerm >= cflInput.get(tuple[0]).get(tuple[1]).length()){

                //}
				
				// if the non terminal length is 2 and it is at the end of the string
				if (tuple[3] == 2 && (tuple[2]+tuple[3] >= tuple[4])){
					endOfNonTerm = tuple[4]-1;
				}
				System.out.println("Start of non terminal: " + startOfNonTerm + " EndOfNonTerm: "+ endOfNonTerm);
				String newString = cflInput.get(tuple[0]).get(tuple[1]).replace(startVar, "$");
				System.out.println("newString is this: " + newString);
				// add new string with $
				cflInput.get(tuple[0]).add(newString);
				
				
				//cflInput.get(tuple[0]).set(tuple[1], newString); // = cflInput.get(tuple[0]).get(tuple[1]).replace(startVar, "$");
				System.out.println("replaced " + startVar + " with "+ cflInput.get(tuple[0]).get(tuple[1]).toString());
				
				//Set<String> hs = new HashSet<>();
				//hs.addAll(cflInput.get(tuple[0]));
				//cflInput.get(tuple[0]).clear();
				//cflInput.get(tuple[0]).addAll(hs);
			}
			System.out.println("Removing: " + ePos[0] + ", " +ePos[1]);
            cflInput.get(ePos[0]).remove(ePos[1]);

            System.out.println("CFL with removed: ");
            printCurrentCFL();
            removeDuplicates();
            System.out.println("CFL with removed duplicates: ");
            printCurrentCFL();
            System.out.println("CFL with removed extra epsilons: ");
            removeEpsilons();
            printCurrentCFL();
            // set search start for next time at the line above where the last one was removed
            startLine = ePos[0] - 1;
			
		}
		
		//tim's attempt woop!
		/*int[] ePos = ePosition();
		System.out.println("E found at position: " + ePos[0] +", " + ePos[1]);
		if (ePos.length == 0){
			return;
		}
		String startVar = cflInput.get(ePos[0]).get(0);
		ArrayList<Integer[]> positions = getPositions(startVar);// get all positions in the CFG with that variable on the right hand side
		
		System.out.println("POSITIONS: " + positions);
		for (Integer[] tuple : positions)
		{
			System.out.println("STUFF IS LOCATED: " + tuple[0] + ", " + tuple[1]);
		}*/
		
	}
	
	public static ArrayList<Integer[]> getPositions(String nonTerminal, int lastLine){
		lastLine = numLines-1;
        ArrayList<Integer[]> positions = new ArrayList<Integer[]>();
		char[] nonTermChars = new char[2];
		if (nonTerminal.length() == 2){
			nonTermChars[0] = nonTerminal.charAt(0);
			nonTermChars[1] = nonTerminal.charAt(1);
		}
		else if (nonTerminal.length() == 1){
			nonTermChars[0] = nonTerminal.charAt(0);
			// when looking for matches, if this position 1 is '*',
			// ignore and only compare position 0.
			nonTermChars[1] = '*';
		}
		
		
		for (int i = 0; i < lastLine+1; i++){
			
			for (int j = 1; j < cflInput.get(i).size(); j++){//j==1
				
				String contents = cflInput.get(i).get(j);
				
				for (int x = 0; x < contents.length(); x++){
					char curLetter = contents.charAt(x);
					
					// if the current letter is equal to the first character
					// in the non terminal, check to make sure it is actually
					// equal (ie B != B0 and B1 != B0
					if ((curLetter == nonTermChars[0])){
						// if the non terminal only has one character, check the next 
						// character in the string to make sure it is not a number
						// if it's not, they're equal, else they're not
						if ((nonTermChars[1] == '*')){
							// if next char is not a number
							if ((x+1) < contents.length()){
								if (!((contents.charAt(x+1) > 47) && (contents.charAt(x+1) < 58))){
									Integer[] foundPos = {i,j, x, 1, contents.length()};
									positions.add(foundPos);
								}
							}
							else {
								Integer[] foundPos = {i,j, x, 1, contents.length()};
								positions.add(foundPos);
							}
						}
						// non terminal we are looking for is 2 characters (a letter followed by a number)
						else {
							if ((x+1) < contents.length()){
								if (((contents.charAt(x+1) > 47) && (contents.charAt(x+1) < 58) && (contents.charAt(x+1) == nonTermChars[1]))){
									
									Integer[] foundPos = {i,j, x, 2, contents.length()};
									positions.add(foundPos);
								}
							}
							
						}

					}
				}
			}
			
		}
		
		return positions;
		/*ArrayList<Integer[]> positions = new ArrayList<Integer[]>();
		for (int i = 0; i < numLines; i++)
		{
			for (int j = 1; j < cflInput.get(i).size(); j++)
			{
				String contents = cflInput.get(i).get(j);
				if(contents == nonTerminal)
				{
					Integer[] foundPos = {i,j};
					positions.add(foundPos);
				}
			}
		}
		return positions;*/
	}
	
	public static void removeDuplicates()
	{


        for (int i = 0; i < numLines; i++) {

            int epsilonCount = 0;
            //ArrayList<Integer> epsilonPositions = new ArrayList<Integer>();

            for (int j = 1; j < cflInput.get(i).size(); j++) {
                if (cflInput.get(i).get(j).equals("$")){
                    epsilonCount++;
                    //epsilonPositions.add(j);
                }
            }

            if (epsilonCount > 1){
                while(epsilonCount > 1) {
                    for (int a = 1; a < cflInput.get(i).size(); a++) {
                        if (cflInput.get(i).get(a).equals("$") && epsilonCount > 1) {
                            cflInput.get(i).remove(a);
                            epsilonCount--;
                        }
                    }
                }
            }
        }
	}
    // removes epsilons that are part of a string
    public static void removeEpsilons(){
        for (int i = 0; i < numLines; i++){
            for (int j = 1; j < cflInput.get(i).size(); j++){
                if(cflInput.get(i).get(j).length() > 1){
                    System.out.println("Old line: " + cflInput.get(i).get(j).toString());
                    cflInput.get(i).set(j, cflInput.get(i).get(j).replace("$", ""));
                    System.out.println("New line: "+ cflInput.get(i).get(j).toString());
                }
            }
        }
    }

    public static void removeRedundant(){

        for (int i = 0; i < numLines; i++) {

            ArrayList<String> temp = new ArrayList<String>();
            int duplicates = 0;
            String nonTerminal = cflInput.get(i).get(0).toString();
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                if (cflInput.get(i).get(j).toString().equals(nonTerminal)){
                    duplicates++;
                }
                temp.add(cflInput.get(i).get(j));
            }
            System.out.println("REMOVE DUPLICATES: " + i);

            for (int x = 0; x < duplicates; x++){

                temp.remove(nonTerminal);
            }
            cflInput.get(i).clear();
            cflInput.get(i).add(nonTerminal);

            for (String a : temp){
                cflInput.get(i).add(a);
            }
        }
    }

    public static void removeSingles(){

        //for (int z = numLines -1; z >=0; z--) {
            //String nonTerm = cflInput.get(z).get(0).toString();
            for (int i = numLines - 1; i >= 0; i--) {
                String nonTerm;

                for (int j = 1; j < cflInput.get(i).size(); j++) {
                    nonTerm= cflInput.get(i).get(j);
                    if (nonTerm.length() == 1) {
                        char letter = nonTerm.charAt(0);
                        // if the letter is single and uppercase
                        if (letter > 64 && letter < 91) {
                            for (int x = 0; x < numLines; x++) {

                                if (cflInput.get(x).get(0).equals(letter + "")) {
                                    copyContents(i, x);
                                }
                            }
                        }
                    }
                }
            }
        ArrayList<int[]> removeCoordinates = new ArrayList<int[]>();
        for(int i = 0; i < numLines; i++){
            for (int j = 1; j < cflInput.get(i).size(); j++){
                if (cflInput.get(i).get(j).length() == 1){
                    char letter = cflInput.get(i).get(j).charAt(0);
                    if (letter > 64 && letter < 91){
                        int[] coordinates = new int[2];
                        coordinates[0] = i;
                        coordinates[1] = j;
                        removeCoordinates.add(coordinates);
                    }
                }
            }
        }

        for(int i = removeCoordinates.size()-1; i >= 0; i--){
            cflInput.get(removeCoordinates.get(i)[0]).remove(removeCoordinates.get(i)[1]);
        }
       // }
    }

    public static void copyContents(int copyTo, int copyFrom){
        for (int j = 1; j < cflInput.get(copyFrom).size(); j++){
            String copying = cflInput.get(copyFrom).get(j);
            cflInput.get(copyTo).add(copying);
        }
        System.out.println("After copying contents: ");
        printCurrentCFL();
    }

    public static void removeTriples(){
        while(!checkTriples()) {
            Set<String> stringEnds = new HashSet<String>();
            for (int i = 0; i < numLines; i++) {
                for (int j = 1; j < cflInput.get(i).size(); j++) {
                    if (cflInput.get(i).get(j).length() > 2) {
                        String temp = cflInput.get(i).get(j).substring(1);
                        stringEnds.add(temp);
                    }
                }
            }

            ArrayList<String> strings = new ArrayList<String>();
            strings.addAll(stringEnds);
            for (int x = 0; x < strings.size(); x++) {
                for (int i = 0; i < numLines; i++) {
                    for (int j = 1; j < cflInput.get(i).size(); j++) {
                        if (cflInput.get(i).get(j).length() > 2) {
                            cflInput.get(i).set(j, cflInput.get(i).get(j).replace(strings.get(x), extraNonTerminals.get(x)));
                        }
                    }
                }
                numLines++;
                ArrayList<String> newLine = new ArrayList<String>();
                newLine.add(extraNonTerminals.get(x));
                newLine.add(strings.get(x));
                cflInput.add(newLine);
            }
            for (int i = strings.size()-1; i >= 0; i--){
                extraNonTerminals.remove(i);
            }
        }


    }

    public static void keepLoneTerminals(){
        Set<String> replaceChars = new HashSet<String>();

        for (int i = 0; i < numLines; i++) {
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                if (cflInput.get(i).get(j).length() > 1){
                    for (int x = 0; x < cflInput.get(i).get(j).length(); x++){
                        char letter = cflInput.get(i).get(j).charAt(x);
                        if (letter > 96 && letter < 123){
                            int stringStart = x;
                            int stringEnd = x+1;
                            String replaceChar;
                            if (stringEnd < cflInput.get(i).get(j).length()) {
                                replaceChar = cflInput.get(i).get(j).substring(stringStart, stringEnd);
                            }
                            else {
                                replaceChar = cflInput.get(i).get(j).substring(stringStart);
                            }
                            replaceChars.add(replaceChar);
                        }
                    }
                }
            }
        }

        ArrayList<String> chars = new ArrayList<String>();
        chars.addAll(replaceChars);

        for (int x = 0; x < chars.size(); x++){
            for (int i = 0; i < numLines; i++){
                for (int j = 1; j < cflInput.get(i).size(); j++){
                    if (cflInput.get(i).get(j).length() > 1) {
                        cflInput.get(i).set(j, cflInput.get(i).get(j).replace(chars.get(x), extraNonTerminals.get(x)));
                    }
                }
            }
            numLines++;
            ArrayList<String> newLine = new ArrayList<String>();
            newLine.add(extraNonTerminals.get(x));
            newLine.add(chars.get(x));
            cflInput.add(newLine);
        }

    }

    public static boolean checkTriples(){
        for (int i = 0; i < numLines; i++) {
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                if (cflInput.get(i).get(j).length() > 2){
                    return false;
                }
            }
        }
        return true;
    }

    public static void removeDuplicateEntries(){
        for (int i = 0; i < numLines; i++){
            String tempStartVar = cflInput.get(i).get(0);
            Set<String> tempLine = new HashSet<String>();
            for (int j = 1; j < cflInput.get(i).size(); j++){
                tempLine.add(cflInput.get(i).get(j));
            }
            cflInput.get(i).clear();
            cflInput.get(i).add(tempStartVar);
            cflInput.get(i).addAll(tempLine);
        }
    }

}
		