/**
 * Stage 3: anything a customer can order and the kitchen can prepare.
 * Implemented by single MenuItems and by ComboMeals, so Order, Chef and Cashier
 * work with both without knowing which one they have.
 */
interface Dish {
    String getDisplayName();

    int getPriceCents();

    int getPrepUnitsRequired();
}
