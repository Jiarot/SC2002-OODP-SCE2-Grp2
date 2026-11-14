class VIPCustomer extends Customer {
    private static final int LOSS_PER_TURN = 5;
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }

    @Override
    public String getCustomerType() {
        return "VIP";
    }


    // VIPs get 10% off the menu price (rounded half-up to the nearest cent).
    @Override
    public int calculateBillCents(int basePriceCents) {
        return (basePriceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    @Override
    public String getBehaviourNote() {
        return "Pays " + DISCOUNT_PERCENT + "% less than the menu price.";
    }
}
