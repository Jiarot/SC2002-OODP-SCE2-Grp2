import java.util.Random;

class CriticCustomer extends Customer {
    private static final int LOSS_PER_TURN = 12;

    // Reaction probabilities when the food is served: 50% like, 25% okay, remaining 25% hate.
    private static final double LIKE_CHANCE = 0.50;
    private static final double OKAY_CHANCE = 0.25;

    // Deliberately UNSEEDED: the Critic's verdict differs on every run and every game.
    // It is independent of the game's seed-42 generator (CustomerFoodChooser), so menu choices
    // and all other customer types stay reproducible.
    private final Random reactionRandom = new Random();

    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }

    @Override
    public String getCustomerType() {
        return "Critic";
    }

    /**
     * The Critic judges the food the moment it is served:
     * - LIKE  (50%): satisfaction is restored to 100.
     * - OKAY  (25%): satisfaction is unchanged.
     * - HATE  (25%): satisfaction drops to 0.
     * A served customer no longer loses satisfaction, so the new value is kept until payment.
     */
    @Override
    public CustomerReaction reactToServedFood() {
        double roll = reactionRandom.nextDouble();
        if (roll < LIKE_CHANCE) {
            this.satisfaction = 100;
            return CustomerReaction.DELIGHTED;
        }
        if (roll < LIKE_CHANCE + OKAY_CHANCE) {
            return CustomerReaction.UNIMPRESSED;
        }
        this.satisfaction = 0;
        return CustomerReaction.FURIOUS;
    }

    @Override
    public String getBehaviourNote() {
        int like = (int) Math.round(LIKE_CHANCE * 100);
        int okay = (int) Math.round(OKAY_CHANCE * 100);
        int hate = 100 - like - okay;
        return "Pays full price. Judges the food when served: " + like + "% delighted (satisfaction -> 100), "
                + okay + "% unimpressed (unchanged), " + hate + "% furious (satisfaction -> 0).";
    }
}
