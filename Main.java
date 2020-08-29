package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File file = new File("./" + args[0]);
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        StringBuilder content = new StringBuilder();
        while (sc.hasNext()) {
            content.append(sc.nextLine()).append("\n");
        }
        sc.close();
        ReadabilityScore rs = new ReadabilityScore(content.toString());
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("The text is:");
        System.out.println(rs.getContent() + "\n");
        System.out.println("Words: " + rs.getWordCount());
        System.out.println("Sentences: " + rs.getSentenceCount());
        System.out.println("Characters: " + rs.getCharacterCount());
        System.out.println("Syllables: " + rs.getSyllableCount());
        System.out.println("Polysyllables: " + rs.getPolysyllableCount());

        sc = new Scanner(System.in);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String scoreType = sc.next();
        if (scoreType.equals("ARI") || scoreType.equals("all")) {
            System.out.println("Automated Readability Index: " + df.format(rs.getARI()) + " (about "+ ReadabilityScore.getAgeForScore(rs.getARI()) +" year olds.)");

        }
        if (scoreType.equals("FK") || scoreType.equals("all")) {
            System.out.println("Flesch–Kincaid readability tests: " + df.format(rs.getFK()) + " (about "+ ReadabilityScore.getAgeForScore(rs.getFK()) +" year olds.)");

        }
        if (scoreType.equals("SMOG") || scoreType.equals("all")) {
            System.out.println("Simple Measure of Gobbledygook: " + df.format(rs.getSMOGIndex()) + " (about "+ ReadabilityScore.getAgeForScore(rs.getSMOGIndex()) +" year olds.)");

        }
        if (scoreType.equals("CL") || scoreType.equals("all")) {
            System.out.println("Coleman–Liau index: " + df.format(rs.getCLIndex()) + " (about "+ ReadabilityScore.getAgeForScore(rs.getCLIndex()) +" year olds.)");
        }
        if (scoreType.equals("all")) {
            float avg = (ReadabilityScore.getAgeForScore(rs.getARI()) + ReadabilityScore.getAgeForScore(rs.getFK()) + ReadabilityScore.getAgeForScore(rs.getSMOGIndex()) + ReadabilityScore.getAgeForScore(rs.getCLIndex()))/4f;
            System.out.println("\nThis text should be understood in average by "+ df.format(avg) + " year olds.");
        }


        sc.close();
    }
}

class ReadabilityScore {
    private final String content;
    private final int sentenceCount;
    private final int wordCount;
    private final int characterCount;
    private int syllableCount = 0;
    private int polysyllableCount = 0;
    private float automatedReadabilityIndexScore;
    private float fleschKincaidReadabilityTestScore;
    private float SMOGIndex;
    private String[] sentences;
    private final String[] words;
    private float CLIndex;

    ReadabilityScore(String content) {
        this.content = content.trim();
        this.sentences = this.content.split("[\\.!\\?]"); // get sentences as array
        this.sentenceCount = sentences.length;
        //
        this.words = this.content.split("[\\s]");
        this.wordCount = words.length;

        // Count characters
        this.characterCount = content.replaceAll("\\s", "").length();

        this.countSyllablesAndPolysyllablesInContent();

        this.calculateARI();
        this.calculateFK();
        this.calculateSMOG();
        this.calculateCLIndex();
    }
    private void countSyllablesAndPolysyllablesInContent() {
        for (String word: this.words) {

            int syllables = countSyllablesInWord(word);
            this.syllableCount += syllables;
            if (syllables > 2) {
                polysyllableCount++;
            }

        }
    }
    private static int countSyllablesInWord(String word) {
        int syllableCount = 0;
        word = word.toLowerCase().replaceAll("[^a-z]$", "").replaceAll("e*$", "").replaceAll("[aeiouy]{2,}", "a");
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == 'a' || c == 'e'|| c == 'i'|| c == 'o'|| c == 'u'|| c == 'y') {
                syllableCount++;
            }
        }

        return Math.max(1, syllableCount);
    }

    String getContent() {
        return content;
    }
    int getCharacterCount() {
        return this.characterCount;
    }
    int getWordCount() {
        return this.wordCount;
    }

    int getSentenceCount() {
        return this.sentenceCount;
    }
    int getPolysyllableCount() {
        return this.polysyllableCount;
    }
    int getSyllableCount() {
        return this.syllableCount;
    }
    private void calculateARI() {
        this.automatedReadabilityIndexScore = 4.71F * characterCount / wordCount + 0.5F * wordCount / sentenceCount - 21.43F;
    }
    public float getARI() {
        return this.automatedReadabilityIndexScore;
    }
    private void calculateFK() {
        this.fleschKincaidReadabilityTestScore = 0.39F * wordCount / sentenceCount + 11.8F * syllableCount / wordCount - 15.59F;
    }
    public float getFK() {
        return this.fleschKincaidReadabilityTestScore;
    }
    private void calculateSMOG() {
        this.SMOGIndex = (float) (1.043F * Math.sqrt(polysyllableCount * 30d/sentenceCount))  + 3.1291F;
    }
    public float getSMOGIndex() {
        return this.SMOGIndex;
    }
    private void calculateCLIndex() {
        float L = (characterCount*100f) / wordCount;
        float S = (sentenceCount*100f) / wordCount ;
        this.CLIndex = (0.0588F * L) - (0.296F * S) - 15.8F;
    }
    public float getCLIndex() {
        return this.CLIndex;
    }

    public static int getAgeForScore(float score) {
        int scoreInt = Math.round(score);
        switch (scoreInt) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 10;
            case 5:
                return 11;
            case 6:
                return 12;
            case 7:
                return 13;
            case 8:
                return 14;
            case 9:
                return 15;
            case 10:
                return 16;
            case 11:
                return 17;
            case 12:
                return 18;
            case 13:
                return 24;
            case 14:
                return 25;
            default:
                return 0;
        }
    }
}
