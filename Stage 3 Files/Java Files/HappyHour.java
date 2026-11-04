/**
 * Stage 3: Happy Hour pricing policy.
 * - Can be activated once per game, at the start of a turn.
 * - Lasts for that turn and the next (2 turns).
 * - Orders taken while active get 20% off (applied after any customer discount, e.g. VIP).
 * - During each active turn, customers still waiting for a table or food recover 15 satisfaction
 *   at turn start (maximum 100).
 */
class HappyHour {
    public static final int DURATION_TURNS = 2;
    public static final int DISCOUNT_PERCENT = 20;
    public static final int RECOVERY_POINTS = 15;

    private boolean used = false;
    private int remainingTurns = 0;

    public boolean canActivate() {
        return !used;
    }

    public void activate() {
        if (used) {
            throw new IllegalStateException("Happy Hour can only be activated once per game.");
        }
        used = true;
        remainingTurns = DURATION_TURNS;
    }

    public boolean isActive() {
        return remainingTurns > 0;
    }

    public boolean isUsed() {
        return used;
    }

    /** Active turns left, counting the current turn. */
    public int getRemainingTurns() {
        return remainingTurns;
    }

    /** Applies the Happy Hour discount if active; rounded half-up to the nearest cent. */
    public int applyDiscount(int priceCents) {
        if (!isActive()) {
            return priceCents;
        }
        return (priceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    /** Called once at the end of every turn. */
    public void endTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }
}
