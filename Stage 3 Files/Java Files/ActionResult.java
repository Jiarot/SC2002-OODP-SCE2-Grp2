class ActionResult {
    private final boolean success;
    private final String message;
    private final CustomerReaction reaction;

    private ActionResult(boolean success, String message, CustomerReaction reaction) {
        this.success = success;
        this.message = message;
        this.reaction = reaction;
    }

    public static ActionResult ok(String message) {
        return new ActionResult(true, message, CustomerReaction.NONE);
    }

    public static ActionResult ok(String message, CustomerReaction reaction) {
        return new ActionResult(true, message, reaction);
    }

    public static ActionResult fail(String reason) {
        return new ActionResult(false, reason, CustomerReaction.NONE);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public CustomerReaction getReaction() {
        return reaction;
    }
}