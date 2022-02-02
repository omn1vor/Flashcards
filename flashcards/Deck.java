package flashcards;

import java.io.*;
import java.util.*;

public class Deck {

    private final static Map<String, Card> termIndex = new HashMap<>();
    private final static Map<String, Card> definitionIndex = new HashMap<>();
    static final String delimiter = "<br/>";

    public boolean hasTerm(String term) {
        return termIndex.containsKey(term);
    }

    public boolean hasDefinition(String definition) {
        return definitionIndex.containsKey(definition);
    }

    public Card getCardByTerm(String term) {
        return termIndex.get(term);
    }

    public Card getCardByDefinition(String definition) {
        return definitionIndex.get(definition);
    }

    public Card[] getCards(int num) {
        String[] terms = termIndex.keySet().toArray(new String[0]);
        Card[] cards = new Card[num];
        Random rnd = new Random();

        for (int i = 0; i < num; i++) {
            cards[i] = getCardByTerm(terms[rnd.nextInt(termIndex.size())]);
        }
        return cards;
    }

    public Card addCard(String term, String definition) {
        Card card = new Card(term, definition);
        termIndex.put(term, card);
        definitionIndex.put(definition, card);
        return card;
    }

    public void addUpdateCard(String term, String definition, long errors) {
        Card card = getCardByTerm(term);
        if (card == null) {
            card = addCard(term, definition);
        }
        card.setDefinition(definition);
        card.setErrors(errors);
    }

    public boolean removeCard(String term) {
        Card card = termIndex.get(term);
        if (card == null) {
            return false;
        }
        termIndex.remove(term);
        definitionIndex.remove(card.getDefinition());
        return true;
    }

    public String showHardestCard() {
        ArrayList<Card> cards = new ArrayList<>();
        long maxErrors = 0;

        for (Card card : termIndex.values()) {
            long errNum = card.getErrors();
            if (errNum > maxErrors) {
                maxErrors = errNum;
                cards.clear();
            }
            if (errNum == maxErrors) {
                cards.add(card);
            }
        }

        StringBuilder result = new StringBuilder();
        if (maxErrors == 0 || cards.size() == 0) {
            result.append("There are no cards with errors.");
        } else if (cards.size() == 1) {
            result.append(String.format("The hardest card is \"%s\". " +
                    "You have %d errors answering it", cards.get(0).getTerm(), maxErrors));
        } else {
            result.append("The hardest cards are ");
            for (Card card : cards) {
                result.append(String.format("\"%s\", ", card.getTerm()));
            }
            result.delete(result.length() - 2, result.length());
            result.append(". You have n errors answering them.");
        }
        return result.toString();
    }

    public void resetErrors() {
        for (Card card : termIndex.values()) {
            card.setErrors(0);
        }
    }

    public long importCards(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String cardData = br.readLine();
            int counter = 0;
            while(cardData != null) {
                String[] arr = cardData.split(delimiter);
                if (arr.length != 3) {
                    throw new IllegalArgumentException("Wrong file structure, can't read the line: " + cardData);
                }
                addUpdateCard(arr[0], arr[1], Long.parseLong(arr[2]));
                counter++;
                cardData = br.readLine();
            }
            return counter;
        } catch (IOException e) {
            return -1;
        }
    }

    public long exportCards(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Card card : termIndex.values()) {
                String cardData = String.format("%s%s%s%s%s%n",
                        card.getTerm(), delimiter,
                        card.getDefinition(), delimiter,
                        card.getErrors());
                bw.write(cardData);
            }
            return termIndex.size();
        } catch (IOException e) {
            return -1;
        }
    }


}
