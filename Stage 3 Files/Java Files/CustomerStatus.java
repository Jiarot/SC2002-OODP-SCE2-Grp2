enum CustomerStatus {
    WAITING_IN_QUEUE("Waiting in Queue"),
    SEATED("Seated (Waiting to Order)"),
    ORDERED("Seated (Waiting for Food)"),
    SERVED_EATING("Eating"),
    READY_TO_PAY("Ready to Pay"),
    PAID("Paid & Left"),
    ABANDONED("Abandoned (Unhappy)");

    private final String label;

    CustomerStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}