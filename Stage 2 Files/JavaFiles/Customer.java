abstract class Customer {
    protected final int id;
    protected final int arrivalTurn;
    protected int satisfaction;
    protected CustomerStatus status;
    protected int servedTurn = -1;
    protected int readyToPayTurn = -1;

    public Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = 100;
        this.status = CustomerStatus.WAITING_IN_QUEUE;
    }

    public abstract int getSatisfactionLossPerTurn();

    public abstract String getCustomerType();

    /**
     * One-line description of how this customer type differs from the others (shown in the UI).
     */
    public abstract String getBehaviourNote();

    /**
     * The bill for a dish at the given menu price. Full price by default;
     * customer types with a discount override this.
     */
    public int calculateBillCents(int basePriceCents) {
        return basePriceCents;
    }

    /**
     * Called when this customer is served their food. Most customers have no reaction;
     * types that do (e.g. Critic) override this and may change their own satisfaction.
     * Any randomness is owned by the customer type itself, not supplied by the controller.
     */
    public CustomerReaction reactToServedFood() {
        return CustomerReaction.NONE;
    }

    public boolean losesSatisfactionAtTurnEnd() {
        return status == CustomerStatus.WAITING_IN_QUEUE
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED;
    }

    public void applyWaitingPenalty() {
        if (losesSatisfactionAtTurnEnd()) {
            this.satisfaction = Math.max(0, this.satisfaction - getSatisfactionLossPerTurn());
        }
    }

    public boolean hasAbandoned() {
        return this.satisfaction == 0 && losesSatisfactionAtTurnEnd();
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public void markServed(int turn) {
        this.status = CustomerStatus.SERVED_EATING;
        this.servedTurn = turn;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }

    public void markReadyToPay(int turn) {
        this.status = CustomerStatus.READY_TO_PAY;
        this.readyToPayTurn = turn;
    }
}
