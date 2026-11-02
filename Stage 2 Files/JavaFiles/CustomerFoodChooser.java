import java.util.Random;

class CustomerFoodChooser {
    private final Random seededRandom;
    private final boolean useDeterministicCycle;
    private int cycleIndex = 0;

    public CustomerFoodChooser(long seed, boolean useDeterministicCycle) {
        this.seededRandom = new Random(seed);
        this.useDeterministicCycle = useDeterministicCycle;
    }

    public MenuItem chooseFoodFor(Customer customer) {
        MenuItem[] items = MenuItem.values();
        if (useDeterministicCycle) {
            MenuItem chosen = items[cycleIndex % items.length];
            cycleIndex++;
            return chosen;
        }
        return items[seededRandom.nextInt(items.length)];
    }

    public String getRuleDescription() {
        return useDeterministicCycle
                ? "Deterministic rotation (Salad -> Burger -> Pasta)"
                : "Seeded randomness (seed = 42)";
    }
}