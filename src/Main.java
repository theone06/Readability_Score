package readability;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    /*checks a regular expression againts character
     and returns a boolean if it is a vowel*/
    private boolean isVowel(char c) {
        String temp = "";
        temp += c;
        return (Pattern.compile("[aeiouyAEIOUY]").matcher(temp).matches());
    }

    /*Counts the syllables with the assistance
     of the isVowel() method*/
    private int countSyllables(String word) {
        int sCount = 0;
        int i = 0;
        boolean vowelFlag = false;
        char[] array = word.toCharArray();

        while(i < array.length) {
            if(isVowel(array[i]) && !vowelFlag) {
                sCount++;
                vowelFlag = true;
                i++;
            }
            else {
                vowelFlag = false;
                i++;
            }
        }
        //when word ends with a 'e' don't increment
        if(array[array.length-1] == 'e') {
            sCount--;
        }
        /*when word has no vowels it and
         is considered a one syllable word*/
        if(sCount == 0) {
            sCount = 1;
        }
        return sCount;
    }

    //returns the number of sentences in input
    private int countTotalSentences(String input) {
        String[] sentences = input.split("[\\.|\\?|!]");
        return sentences.length;
    }

    //returns the number of words input
    private int countTotalWords(String input) {
        String[] words = input.split("[\\!\\.\\,\\?]*\\s");
        return words.length;
    }

    //returns number of syllables in input
    private int countTotalSyllables(String input) {
        int count = 0;
        String[] words = input.split("[\\!\\.]*\\s");
        for(String s: words) {
           count += countSyllables(s);
        }
        return count;
    }

    //returns number of polysyllables in input
    private int countTotalPolysyllables(String input) {
        int count = 0;
        String[] polys = input.split("[\\!\\.\\,\\?]*\\s");
        for(String s: polys) {
            if(countSyllables(s) > 2) {
                count++;
            }
        }
        return count;
    }

    //returns number of characters in input
    private int countTotalCharacters(String input) {
        int charCount = 0;
        String[] words = input.split("\\s");
        for(String s: words) {
            charCount += s.length();
        }
        return charCount;
    }

    /*Flesch–Kincaid readability test*/
    private double fleschKincaidTest(String input) {
        int totalSyllables = countTotalSyllables(input);
        int totalWords = countTotalWords(input);
        int totalSentences = countTotalSentences(input);

        return (0.39 * totalWords/totalSentences + 11.8 * totalSyllables/totalWords - 15.59);
    }

    /*SMOG index: Simple Measure of Gobbledygook*/
    private double smogIndexTest(String input) {
        int polys = countTotalPolysyllables(input);
        int sentences = countTotalSentences(input);
        return (1.043 * Math.sqrt(polys *30/sentences) + 3.1291);
    }

    /*Coleman-Liau index test*/
    private double colemanLiauIndexTest(String input) {
        double L = (double) countTotalCharacters(input)/countTotalWords(input) * 100;   //L = Letters ÷ Words × 100
        double S = (double) countTotalSentences(input)/countTotalWords(input) * 100;   //S = Sentences ÷ Words × 100
        return (0.0588 * L - 0.296 * S -15.8);
    }

    /*Automated Readability Index*/
    private double autoReadIndex(String input) {
       int characters = countTotalCharacters(input);
       int words = countTotalWords(input);
       int sentences = countTotalSentences(input);
       return (4.71 * (double)characters/words + 0.5 * (double)words/sentences - 21.43);
    }

    /*determine approximate grade level*/
    private int getAgeGroup (int score) {
        HashMap<Integer, Integer> ages = new HashMap();

        ages.put(1,6);
        ages.put(2,7);
        ages.put(3,9);
        ages.put(4,10);
        ages.put(5,11);
        ages.put(6,12);
        ages.put(7,13);
        ages.put(8,14);
        ages.put(9,15);
        ages.put(10,16);
        ages.put(11,17);
        ages.put(12,18);
        ages.put(13,24);
        ages.put(14,25);
        return ages.get(score);
    }

    /*prints text analysis*/
    public void textAnalysis(String input) {
        System.out.println("Words: " + countTotalWords(input));
        System.out.println("Sentences: " + countTotalSentences(input));
        System.out.println("Characters: " + countTotalCharacters(input));
        System.out.println("Syllables: " + countTotalSyllables(input));
        System.out.println("Polysyllables: " + countTotalPolysyllables(input));
    }

    //prints readability analysis
    public void readabilityAnalysis(String choice, String input) {
        double ari = autoReadIndex(input);
        double fk = fleschKincaidTest(input);
        double smog = smogIndexTest(input);
        double cl = colemanLiauIndexTest(input);
        if (choice.equalsIgnoreCase("ARI") || choice.equalsIgnoreCase("all")) {
            System.out.printf("Automated Readability Index: %5.2f", ari);
            System.out.println(" (about " + getAgeGroup((int) Math.round(ari)) + " year olds).");
        }
        if (choice.equalsIgnoreCase("FK") ||choice.equalsIgnoreCase("all")) {
            System.out.printf("Flesch–Kincaid readability tests: %5.2f", fk);
            System.out.println(" (about " + getAgeGroup((int) Math.round(fk)) + " year olds).");
        }
        if(choice.equalsIgnoreCase("SMOG") ||choice.equalsIgnoreCase("all")) {
            System.out.printf("Simple Measure of Gobbledygook: %5.2f", smog);
            System.out.println(" (about " + getAgeGroup((int) Math.round(smog)) + " year olds).");
        }
        if(choice.equalsIgnoreCase("CL") ||choice.equalsIgnoreCase("all")) {
            System.out.printf("Coleman–Liau index: %5.2f", cl);
            System.out.println(" (about " + getAgeGroup((int) Math.round(cl)) + " year olds).");
        }
        double avg = (getAgeGroup((int)ari) + getAgeGroup((int)fk) + getAgeGroup((int)smog) + getAgeGroup((int)cl))/4;
        System.out.println("This text should be understood in average by " +  avg + " year olds.");

    }

    public static void main(String[] args) throws IOException {
        String path = args[0];
        File readFile = new File(path);
        String input;
        try (Scanner scan = new Scanner(readFile)) {
            input = "";
            while(scan.hasNextLine()) {
                input = scan.nextLine();
            }
        }
        Main main = new Main();
        main.textAnalysis(input);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        try (Scanner ask = new Scanner(System.in)) {
            String choice = ask.nextLine();
            main.readabilityAnalysis(choice, input);
        }
    }

}
