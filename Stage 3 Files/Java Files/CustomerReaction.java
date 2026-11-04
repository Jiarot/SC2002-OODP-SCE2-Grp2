/**
 * How a customer reacts to the food they are served. Only some customer types react;
 * everyone else returns NONE.
 */
enum CustomerReaction {
    NONE(""),
    DELIGHTED("Compliments to the Chef!"),
    FURIOUS("I want to speak to the Chef!"),
    UNIMPRESSED("The food is just okay...");

    private final String message;

    CustomerReaction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
