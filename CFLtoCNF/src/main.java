
import java.util.*;
import javax.swing.*;
/*
* This program takes in a Context Free Grammar line
* by line from the user through the command line. The program
* then tells the user the new version of that grammar in
* Chomsky Normal Form.
*
* Authors: Alexandra Warlen and Timothy MacNary
* Last Modified: April 17, 2016
*
 */
public class main extends JFrame{
	// main data structure for the CFG, holds the 2D arraylist with the elements of the grammar
    public static ArrayList<ArrayList<String>> cflInput = new ArrayList<ArrayList<String>>();
	// number of lines in the CFG
    public static int numLines;

    // lists to hold the characters that will be used to add new non-terminals when
    // the algorithm calls for new rules
    public static ArrayList<String> extraNonTerminals = new ArrayList<String>();
    public static String[] extras =     {"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    /*
    * main()
    *
    * This main program takes in the command line inputs and creates a CFG
    * from this information. The main program then ties together all portions
    * of the code to complete each step in the algorithm to convert a CFG
    * into Chomsky Normal Form.
    *
    */
	public static void main(String[] args) {
		extraNonTerminals.addAll(Arrays.asList(extras));
        Scanner keyboard = new Scanner(System.in);

        // get info from user about the CFG
		System.out.println("How many lines?");
		numLines = Integer.parseInt(keyboard.nextLine());
		
		for (int i = 0; i < numLines; i++){
			// Ask for first rule (non terminal) in line
            System.out.println("Enter non terminal");
			String nonTerminal = keyboard.nextLine();
			ArrayList<String> row = new ArrayList<String>();
			row.add(nonTerminal);
			String nextRule = "";
			// wait until user enters 'done'
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

        // print inital CFL in proper format
        System.out.println("The CFL you have entered is:");
        printCurrentCFL();
        System.out.println("\n");

        // if already in CNF format, tell user this
        if(checkChomsky()){
            System.out.println("Your CFL is already in CNF:");
            printCurrentCFL();
            return;
        }


		// STEP 1: Create new Start Rule
		newStart();

		// STEP 2: Remove epsilons not in 1st line
		moveE();

        // STEP 3: Remove single non-terminal to itself
        removeRedundant();
        // Remove single non-terminals
        removeSingles();

        // STEP 4: Remove any rules with more than 2 characters
        removeTriples();

        // remove any terminals that are not by themselves (ie aB removed, c stays)
        keepLoneTerminals();

        // remove any entries that are for some reason copied twice into a row
        removeDuplicateEntries();

        // print out new CFG in proper format
        System.out.println("Here is your CFG in proper CNF:");
        printCurrentCFL();

        // print out if it is now in proper CNF
		System.out.println("\nIn chomsky form: " + checkChomsky());

	}
	
	

    /*
	 * checkChomsky()
	 *
	 * Check to see if the CFG is now in proper CNF format
	 *
	 * It is as long as every rule either goes to:
	 *      1. A single terminal
	 *      2. 2 non-terminals
	 *      3. Epsilon, but only if it is in the first line of the CFG
	 *
	 */
	private static boolean checkChomsky()
	{
		//loop through both dimensions of the CFG
		for (int i = 0; i < numLines; i++){
			for (int j = 1; j < cflInput.get(i).size(); j++){//j==1
				// count how many uppercase letters, lowercase letters and
                // occurrences of $ (epsilon) exist.
                int upperCount = 0;
				int lowerCount = 0;
                int epsilonCount = 0;
				
				String contents = cflInput.get(i).get(j);

                // look through each letter in each set of terminals and non terminals
				for (int x = 0; x < contents.length(); x++){

                    char curLetter = contents.charAt(x);
					if (curLetter > 96 && curLetter < 123){
						//lowercase
						lowerCount++;
					}
					else if (curLetter > 64 && curLetter < 91){
						// uppercase
                        upperCount++;
					}
                    else if (curLetter == '$'){
                        // epsilon ('$')
                        epsilonCount++;
                    }
                    // if you find a $ and it's not on the first line
                    // it's not in chomsky form
					if ((curLetter == '$') && (i > 0)){
						return false;
					}
					
				}
                // if it's not a double non terminal, a single terminal or a lone epsilon in the first line
                // then it's not in CNF, return false.
				if (!((upperCount == 2 && lowerCount == 0) || (lowerCount == 1 && upperCount == 0) || (epsilonCount == 1 && upperCount == 0 && lowerCount == 0 && i == 0))) {
                    return false;
                }
			}
			
		}
		
		
		return true;
	}

    /*
     * printCurrentCFL()
     *
     * Takes the current CFL that is being stored in cflInput
     * and prints it out in readable format with the -> and |
     * symbols placed properly
     *
     */
	public static void printCurrentCFL(){
		// loop through both dimensions of the CFG
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

    /*
	 * newStart()
	 *
	 * Creates a new start variable for the CFG.
	 * Meaning that it also adds a line to the CFG.
	 *
	 */
	private static void newStart()
	{
        // make one size longer
        numLines++;

        // create the new row
		ArrayList<String> row2 = new ArrayList<String>();
		String newRule = cflInput.get(0).get(0);
		String newStart = newRule + '0';

        // add contents to row
		row2.add(newStart);
		row2.add(newRule);

        // ad row to the CFG
		cflInput.add(0, row2);
		
	}

    /*
     * properEFormat()
     *
     * Returns a true or false boolean that decides if the
     * epsilons are in the correct place or not.
     *
     */
	public static boolean properEFormat()
	{
		// loop through and make sure epsilons are in right postion
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

    /*
	 * ePosition()
	 *
	 * Finds and returns coordinates of next epsilon in
	 * CFG closest to bottom of CFG.
	 *
	 */
	public static int[] ePosition(int startLine){
		startLine = numLines - 1;
        int[] tuple = new int[2];

        // loop through whole CFG
		for (int i = startLine; i >= 0; i--) {
            for (int j = 1; j < cflInput.get(i).size(); j++) {//j==1
                // get current string
                String contents = cflInput.get(i).get(j);

                // look at each letter
                for (int x = 0; x < contents.length(); x++) {
                    char curLetter = contents.charAt(x);

                    if ((curLetter == '$')) {
                        tuple[0] = i;
                        tuple[1] = j;
                        return tuple;
                    }

                }

            }

        }
		return tuple;
	}
    /*
     * moveE()
     *
     * While its not in the correct epsilon format,
     * find the epsilons and keep moving them to the right
     * place.
     *
     */
	public static void moveE()
	{
        int startLine = numLines-1;
		while (!properEFormat()) {
			int[] ePos = ePosition(startLine);

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
				
				// if the non terminal length is 2 and it is at the end of the string
				if (tuple[3] == 2 && (tuple[2]+tuple[3] >= tuple[4])){
					endOfNonTerm = tuple[4]-1;
				}

				String newString = cflInput.get(tuple[0]).get(tuple[1]).replace(startVar, "$");

				// add new string with $
				cflInput.get(tuple[0]).add(newString);
				

			}

            cflInput.get(ePos[0]).remove(ePos[1]);


            removeDuplicates();


            removeEpsilons();
            // set search start for next time at the line above where the last one was removed
            startLine = ePos[0] - 1;
			
		}
		
	}

    /*
	 * getPositions()
	 *
	 * Takes a non terminal and a line from which to search the CFG.
	 *
	 * Finds all existences of the given nonTerminal in the CFG. Returns this list
	 * as an arraylist of coordinates (array of size 2, x and y axes).
	 *
	 *
	 */
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

	}

    /*
	 * removeDuplicates()
	 *
	 * Removes duplicates of epsilon on the same line.
	 *
	 */
	public static void removeDuplicates()
	{
        // iterate through entire CFG
        for (int i = 0; i < numLines; i++) {

            // count the epsilons
            int epsilonCount = 0;

            for (int j = 1; j < cflInput.get(i).size(); j++) {
                if (cflInput.get(i).get(j).equals("$")){
                    epsilonCount++;
                }
            }

            // if more than one, remove the rest
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

    /*
	 * removeEpsilons()
	 *
	 * Removes epsilons that are in the middle of a string
	 * after replacements have taken place. (i.e. $A$ -> A)
	 *
	 */
    public static void removeEpsilons(){
        for (int i = 0; i < numLines; i++){
            for (int j = 1; j < cflInput.get(i).size(); j++){
                if(cflInput.get(i).get(j).length() > 1){
                    cflInput.get(i).set(j, cflInput.get(i).get(j).replace("$", ""));
                }
            }
        }
    }

    /*
	 * removeRedundant()
	 *
	 * If a rule goes to itself, remove that part of the
	 * rule that goes back to itself.
	 *
	 */
    public static void removeRedundant(){

        // iterate through all lines
        for (int i = 0; i < numLines; i++) {

            // create a list of nonterminals
            ArrayList<String> temp = new ArrayList<String>();
            int duplicates = 0;
            String nonTerminal = cflInput.get(i).get(0).toString();
            // for each right hand side
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                // if the entry in the CFG is equal to it's own
                // left hand side, count it
                if (cflInput.get(i).get(j).toString().equals(nonTerminal)){
                    duplicates++;
                }
                temp.add(cflInput.get(i).get(j));
            }

            // for each duplicate
            for (int x = 0; x < duplicates; x++){
                // remove it
                temp.remove(nonTerminal);
            }
            cflInput.get(i).clear();
            cflInput.get(i).add(nonTerminal);

            for (String a : temp){
                cflInput.get(i).add(a);
            }
        }
    }

    /*
	 * removeSingles()
	 *
	 * Remove all single uppercase letters, replace with respective
	 * lowercase letters.
	 *
	 */
    public static void removeSingles(){

        // iterate through CFG
        for (int i = numLines - 1; i >= 0; i--) {
            String nonTerm;

            for (int j = 1; j < cflInput.get(i).size(); j++) {
                nonTerm = cflInput.get(i).get(j);
                if (nonTerm.length() == 1) {
                    char letter = nonTerm.charAt(0);
                    // if the letter is single and uppercase
                    if (letter > 64 && letter < 91) {
                        // copy all contents from that line
                        for (int x = 0; x < numLines; x++) {
                            // ony copy if the nonterminal is equal to the one we want
                            if (cflInput.get(x).get(0).equals(letter + "")) {
                                copyContents(i, x);
                            }
                        }
                    }
                }
            }

        }
        // find all coordinates that have been replaced so we can now remove those rules
        ArrayList<int[]> removeCoordinates = new ArrayList<int[]>();
        for(int i = 0; i < numLines; i++){
            for (int j = 1; j < cflInput.get(i).size(); j++){
                // if the letter is single and uppercase, remove
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

        // now loop through and actually remove them
        for(int i = removeCoordinates.size()-1; i >= 0; i--){
            cflInput.get(removeCoordinates.get(i)[0]).remove(removeCoordinates.get(i)[1]);
        }

    }

    /*
	 * copyContents()
	 *
	 * Takes two ints that are line numbers.
	 *
	 * Copies contents from one line and adds them to the end
	 * of the other line.
	 *
	 */
    public static void copyContents(int copyTo, int copyFrom){
        for (int j = 1; j < cflInput.get(copyFrom).size(); j++){
            String copying = cflInput.get(copyFrom).get(j);
            cflInput.get(copyTo).add(copying);
        }
    }

    /*
	 * removeTriples()
	 *
	 * Finds and removes any rules that are longer than 2
	 * characters in length. Replaces the end of the rule
	 * with a single character from list of usable extras.
	 *
	 */
    public static void removeTriples(){
        // while rules longer than 2 still remain
        while(!checkTriples()) {
            // create a hash set ( so no duplicates are allowed)
            Set<String> stringEnds = new HashSet<String>();
            // loop through
            for (int i = 0; i < numLines; i++) {
                for (int j = 1; j < cflInput.get(i).size(); j++) {
                    // if the length is greater than 2
                    if (cflInput.get(i).get(j).length() > 2) {
                        // make a temp string and get the end of the initial string
                        String temp = cflInput.get(i).get(j).substring(1);
                        stringEnds.add(temp);
                    }
                }
            }

            // create second temp list of these strings
            ArrayList<String> strings = new ArrayList<String>();
            // copy over all contents from hash set so we can iterate now
            strings.addAll(stringEnds);
            for (int x = 0; x < strings.size(); x++) {
                for (int i = 0; i < numLines; i++) {
                    for (int j = 1; j < cflInput.get(i).size(); j++) {
                        // replace the second part of the string with one of the extra non terminals
                        if (cflInput.get(i).get(j).length() > 2) {
                            cflInput.get(i).set(j, cflInput.get(i).get(j).replace(strings.get(x), extraNonTerminals.get(x)));
                        }
                    }
                }
                // increment the line number and create the new line
                numLines++;
                ArrayList<String> newLine = new ArrayList<String>();
                newLine.add(extraNonTerminals.get(x));
                newLine.add(strings.get(x));
                cflInput.add(newLine);
            }
            // remove any extra non terminals used so they are no longer available
            for (int i = strings.size()-1; i >= 0; i--){
                extraNonTerminals.remove(i);
            }
        }


    }

    /*
	 * keepLoneTerminals()
	 *
	 * Remove any lowercase letters that are not alone.
	 *
	 * Replace with extra non terminals that are still available.
	 *
	 */
    public static void keepLoneTerminals(){
        Set<String> replaceChars = new HashSet<String>();

        for (int i = 0; i < numLines; i++) {
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                // if the entry is longer than one
                if (cflInput.get(i).get(j).length() > 1){
                    // look through each letter and if it's lowercase, add to
                    // list of chars to be replaced
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

        // loop through all characters that need to be replaced, find them and
        // actually replace
        for (int x = 0; x < chars.size(); x++){
            for (int i = 0; i < numLines; i++){
                for (int j = 1; j < cflInput.get(i).size(); j++){
                    if (cflInput.get(i).get(j).length() > 1) {
                        // replace with chars from list
                        cflInput.get(i).set(j, cflInput.get(i).get(j).replace(chars.get(x), extraNonTerminals.get(x)));
                    }
                }
            }
            // increment number of lines and create and add the new line
            numLines++;
            ArrayList<String> newLine = new ArrayList<String>();
            newLine.add(extraNonTerminals.get(x));
            newLine.add(chars.get(x));
            cflInput.add(newLine);
        }

    }

    /*
	 * checkTriples()
	 *
	 * Helper method returns true if no rules exist with more than
	 * two characters.
	 *
	 */
    public static boolean checkTriples(){
        for (int i = 0; i < numLines; i++) {
            for (int j = 1; j < cflInput.get(i).size(); j++) {
                // if the string is longer than two, we're still not
                // in the right format.
                if (cflInput.get(i).get(j).length() > 2){
                    return false;
                }
            }
        }
        return true;
    }

    /*
	 * removeDuplicates()
	 *
	 * Removes any duplicate letters or rules in the CFG
	 * to make it more readable
	 *
	 */
    public static void removeDuplicateEntries(){
        // for each line, add the right hand side rules to a hash
        // set to remove the duplicates
        for (int i = 0; i < numLines; i++){
            String tempStartVar = cflInput.get(i).get(0);
            Set<String> tempLine = new HashSet<String>();

            for (int j = 1; j < cflInput.get(i).size(); j++){
                tempLine.add(cflInput.get(i).get(j));
            }
            // clear that line then replace it with the contents of the
            // hash set
            cflInput.get(i).clear();
            cflInput.get(i).add(tempStartVar);
            cflInput.get(i).addAll(tempLine);
        }
    }

}
		