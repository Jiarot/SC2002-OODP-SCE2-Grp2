class RegularCustomer extends Customer {
    private static final int LOSS_PER_TURN = 8;

    public RegularCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return LOSS_PER_TURN;
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}