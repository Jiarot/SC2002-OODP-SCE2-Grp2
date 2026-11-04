/**
 * Stage 3: a combo made of two existing menu items.
 * Preparation units = sum of both items.
 * Price = 90% of the combined price, rounded to the nearest cent
 * (e.g. Salad + Pasta = ($9 + $18) x 0.9 = $24.30, 3 units).
 */
class ComboMeal implements Dish {
    private static final int COMBO_PRICE_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String getDisplayName() {
        return first.getDisplayName() + " + " + second.getDisplayName() + " Combo";
    }

    @Override
    public int getPriceCents() {
        int combined = first.getPriceCents() + second.getPriceCents();
        return (combined * COMBO_PRICE_PERCENT + 50) / 100; // round half-up to the nearest cent
    }

    @Override
    public int getPrepUnitsRequired() {
        return first.getPrepUnitsRequired() + second.getPrepUnitsRequired();
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }
}
