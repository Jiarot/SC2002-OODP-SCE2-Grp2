import java.util.Random;

class CustomerFoodChooser {
    /**
     * Stage 3 combo rule: a customer whose satisfaction is at least this high when the
     * order is taken is in a great mood and upgrades to a combo.
     */
    public static final int COMBO_SATISFACTION_THRESHOLD = 95;

    private final Random seededRandom;
    private final boolean useDeterministicCycle;
    private int cycleIndex = 0;

    public CustomerFoodChooser(long seed, boolean useDeterministicCycle) {
        this.seededRandom = new Random(seed);
        this.useDeterministicCycle = useDeterministicCycle;
    }

    /**
     * 1. The customer's main dish is picked exactly as in Stages 1-2 (seed 42, or the cycle).
     * 2. Combo rule: if the customer's satisfaction is >= 95 when ordering, they add a side and
     *    order a combo: main + Salad (or Burger + Salad if the main is Salad).
     */
    public Dish chooseFoodFor(Customer customer) {
        MenuItem main = chooseMainDish();
        if (customer.getSatisfaction() >= COMBO_SATISFACTION_THRESHOLD) {
            if (main == MenuItem.SALAD) {
                return new ComboMeal(MenuItem.BURGER, MenuItem.SALAD);
            }
            return new ComboMeal(main, MenuItem.SALAD);
        }
        return main;
    }

    private MenuItem chooseMainDish() {
        MenuItem[] items = MenuItem.values();
        if (useDeterministicCycle) {
            MenuItem chosen = items[cycleIndex % items.length];
            cycleIndex++;
            return chosen;
        }
        return items[seededRandom.nextInt(items.length)];
    }

    public String getRuleDescription() {
        String mainRule = useDeterministicCycle
                ? "Deterministic rotation (Salad -> Burger -> Pasta)"
                : "Seeded randomness (seed = 42)";
        return mainRule + ". Combo rule: satisfaction >= " + COMBO_SATISFACTION_THRESHOLD
                + " when ordering -> main + Salad combo (Salad main -> Burger + Salad).";
    }
}
