package flashcards;

import java.io.*;
import java.util.*;

public class Main {

    static Deck deck = new Deck();
    static Scanner scanner = new Scanner(System.in);
    static final StringBuilder logString = new StringBuilder();
    static String autoSaveFileName;

    public static void main(String[] args) {

        for (int i = 1; i < args.length; i++) {
            if ("-import".equalsIgnoreCase(args[i - 1])) {
                importCards(args[i]);
            } else if ("-export".equalsIgnoreCase(args[i - 1])) {
                autoSaveFileName = args[i];
            }
        }

        boolean exitFlag = false;

        printLine("");
        while(!exitFlag) {
            printLine("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String input = logInputLine(scanner);

            switch (input.toLowerCase()) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importCards(null);
                    break;
                case "export":
                    exportCards(null);
                    break;
                case "ask":
                    ask();
                    break;
                case "exit":
                    printLine("Bye bye!");
                    if (autoSaveFileName != null) {
                        exportCards(autoSaveFileName);
                    }
                    exitFlag = true;
                    break;
                case "log":
                    writeLog();
                    break;
                case "hardest card":
                    printLine(deck.showHardestCard());
                    break;
                case "reset stats":
                    deck.resetErrors();
                    printLine("Card statistics have been reset.");
                    break;
                default:
                    printLine("Wrong input!");
                    break;
            }
        }
    }

    static void addCard() {
        printLine("The card:");
        String term = logInputLine(scanner);
        if (deck.hasTerm(term)) {
            printLine(String.format("The card \"%s\" already exists.", term));
            return;
        }
        printLine("The definition of the card:");
        String definition = logInputLine(scanner);
        if (deck.hasDefinition(definition)) {
            printLine(String.format("The definition \"%s\" already exists.", definition));
            return;
        }
        deck.addCard(term, definition);
        printLine(String.format("The pair (\"%s\":\"%s\") has been added", term, definition));
    }

    static void removeCard() {
        printLine("Which card?");
        String term = logInputLine(scanner);
        if (!deck.removeCard(term)) {
            printLine(String.format("Can't remove \"%s\": there is no such card.", term));
        } else {
            printLine("The card has been removed.");
        }
    }

    static void importCards(String fileName) {
        if (fileName == null) {
            printLine("File name:");
            fileName = logInputLine(scanner);
        }
        long cardsLoaded = deck.importCards(fileName);
        if (cardsLoaded >= 0) {
            printLine(String.format("%d cards have been loaded.", cardsLoaded));
        } else {
            printLine("File not found.");
        }
    }

    static void exportCards(String fileName) {
        if (fileName == null) {
            printLine("File name:");
            fileName = logInputLine(scanner);
        }
        long cardsExported = deck.exportCards(fileName);
        if (cardsExported >= 0) {
            printLine(String.format("%d cards have been saved.", cardsExported));
        } else {
            printLine("There was an error while writing cards.");
        }
    }

    static void ask() {
        printLine("How many times to ask?");
        int num = Integer.parseInt(logInputLine(scanner));
        Card[] cards = deck.getCards(num);

        for (Card card : cards) {
            printLine(String.format("Print the definition of \"%s\":", card.getTerm()));
            String answer = logInputLine(scanner);
            if (card.getDefinition().equals(answer)) {
                printLine("Correct!");
            } else {
                card.addError();
                if (deck.hasDefinition(answer)) {
                    Card anotherCard = deck.getCardByDefinition(answer);
                    printLine(String.format("Wrong. The right answer is \"%s\", " +
                                    "but your definition is correct for \"%s\".",
                            card.getDefinition(), anotherCard.getTerm()));
                } else {
                    printLine(String.format("Wrong. The right answer is \"%s\".", card.getDefinition()));
                }
            }
        }
    }

    static void printLine(String msg) {
        System.out.println(msg);
        log(msg);
    }

    static void log(String msg) {
        logString.append(String.format("%s%n", msg));
    }

    static void writeLog() {
        printLine("File name:");
        String fileName = logInputLine(scanner);
        printLine("The log has been saved.");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(logString.toString());
        } catch (IOException e) {
            printLine(e.getMessage());
        }
    }

    static String logInputLine(Scanner scanner) {
        String input = scanner.nextLine();
        log(input);
        return input;
    }

}

