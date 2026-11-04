enum MenuItem implements Dish {
    SALAD("Salad", 900, 1),
    BURGER("Burger", 1500, 1),
    PASTA("Pasta", 1800, 2);

    private final String displayName;
    private final int priceCents;
    private final int prepUnitsRequired;

    MenuItem(String displayName, int priceCents, int prepUnitsRequired) {
        this.displayName = displayName;
        this.priceCents = priceCents;
        this.prepUnitsRequired = prepUnitsRequired;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPrepUnitsRequired() {
        return prepUnitsRequired;
    }

    public static String formatCents(int cents) {
        int dollars = cents / 100;
        int remCents = Math.abs(cents % 100);
        return String.format("$%d.%02d", dollars, remCents);
    }
}
