package flashcards;

public class Card {

    private final String term;
    private String definition;
    private long errors;

    Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
        this.errors = 0;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public long getErrors() {
        return errors;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setErrors(long errors) {
        this.errors = errors;
    }

    public void addError() {
        this.errors++;
    }
}
