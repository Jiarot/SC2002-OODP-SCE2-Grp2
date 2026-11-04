class Table {
    private final int tableNumber;
    private Customer occupant;
    private Order currentOrder;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void seatCustomer(Customer customer) {
        if (!isFree()) {
            throw new IllegalStateException("Table " + tableNumber + " is already occupied.");
        }
        this.occupant = customer;
        this.currentOrder = null;
        customer.setStatus(CustomerStatus.SEATED);
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
        if (this.occupant != null) {
            this.occupant.setStatus(CustomerStatus.ORDERED);
        }
    }

    public void freeTable() {
        this.occupant = null;
        this.currentOrder = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getOccupant() {
        return occupant;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}