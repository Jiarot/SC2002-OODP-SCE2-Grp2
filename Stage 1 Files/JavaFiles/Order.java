class Order {
    private final int id;
    private final Customer customer;
    private final Table table;
    private final MenuItem item;
    private int remainingPrepUnits;
    private OrderStatus status;
    private final int createdTurn;
    private int readyTurn = -1;

    public Order(int id, Customer customer, Table table, MenuItem item, int createdTurn) {
        this.id = id;
        this.customer = customer;
        this.table = table;
        this.item = item;
        this.remainingPrepUnits = item.getPrepUnitsRequired();
        this.status = OrderStatus.IN_KITCHEN;
        this.createdTurn = createdTurn;
    }

    public void advancePreparation(int units, int currentTurn) {
        if (status != OrderStatus.IN_KITCHEN) {
            throw new IllegalStateException("Order #" + id + " is not in the kitchen.");
        }
        remainingPrepUnits = Math.max(0, remainingPrepUnits - units);
        if (remainingPrepUnits == 0) {
            status = OrderStatus.READY;
            readyTurn = currentTurn;
        }
    }

    /**
     * A dish can be served only if it was already READY when the Waiter acts.
     */
    public boolean isReadyToServe(int currentTurn) {
        return status == OrderStatus.READY && readyTurn <= currentTurn;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getRemainingPrepUnits() {
        return remainingPrepUnits;
    }

    public int getCompletedPrepUnits() {
        return item.getPrepUnitsRequired() - remainingPrepUnits;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getCreatedTurn() {
        return createdTurn;
    }

    public int getReadyTurn() {
        return readyTurn;
    }
}