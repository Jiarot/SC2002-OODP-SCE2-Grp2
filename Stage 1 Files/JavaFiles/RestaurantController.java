import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

class RestaurantController {
    public static final int MAX_TURNS = 18;
    public static final int NUM_TABLES = 2;
    public static final int TARGET_PAID_CUSTOMERS = 4;
    public static final int TARGET_REVENUE_CENTS = 4500; // $45.00
    public static final double TARGET_AVG_SATISFACTION = 60.0;
    private static final Set<Integer> ARRIVAL_TURNS = new HashSet<>(Arrays.asList(1, 4, 7, 10, 13, 16));

    private final CustomerFoodChooser foodChooser;
    private final Queue<Customer> waitingQueue = new LinkedList<>();
    private final List<Table> tables = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Integer> paidCustomerSatisfactions = new ArrayList<>();

    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();

    private int currentTurn = 1;
    private int nextCustomerId = 1;
    private int nextOrderId = 1;
    private int revenueCents = 0;
    private int servedCount = 0;
    private int unhappyDepartures = 0;

    public RestaurantController(CustomerFoodChooser foodChooser) {
        this.foodChooser = foodChooser;
        for (int i = 1; i <= NUM_TABLES; i++) {
            tables.add(new Table(i));
        }
    }

    /**
     * Step 1: Arrivals.
     * Runs at the start of the turn so the newly arrived customer is visible in the queue.
     */
    public Customer startTurnAndProcessArrivals() {
        waiter.resetTurn();
        chef.resetTurn();

        if (ARRIVAL_TURNS.contains(currentTurn)) {
            Customer arrived = new RegularCustomer(nextCustomerId++, currentTurn);
            waitingQueue.add(arrived);
            return arrived;
        }
        return null;
    }

    // --- Waiter Actions (Step 2 & Step 3 part 1: Waiter acts before Chef) ---

    public ActionResult waiterSeatCustomer(int customerId, int tableNumber) {
        if (!waiter.isAvailable()) {
            return ActionResult.fail("Waiter has already completed a task this turn.");
        }
        if (waitingQueue.isEmpty()) {
            return ActionResult.fail("Cannot seat a customer because the waiting queue is empty.");
        }
        Table table = findTable(tableNumber);
        if (table == null) {
            return ActionResult.fail("Invalid table number: Table " + tableNumber + " does not exist.");
        }
        if (!table.isFree()) {
            return ActionResult.fail("Table " + tableNumber + " is already occupied by Customer #"
                    + table.getOccupant().getId() + ".");
        }

        Customer target = null;
        for (Customer c : waitingQueue) {
            if (c.getId() == customerId) {
                target = c;
                break;
            }
        }
        if (target == null) {
            return ActionResult.fail("Customer #" + customerId + " is not in the waiting queue.");
        }

        waitingQueue.remove(target);
        table.seatCustomer(target);
        waiter.markTaskCompleted();
        return ActionResult.ok("Waiter seated Customer #" + target.getId() + " at Table " + table.getTableNumber() + ".");
    }

    public ActionResult waiterTakeOrder(int tableNumber) {
        if (!waiter.isAvailable()) {
            return ActionResult.fail("Waiter has already completed a task this turn.");
        }
        Table table = findTable(tableNumber);
        if (table == null) {
            return ActionResult.fail("Invalid table number: Table " + tableNumber + " does not exist.");
        }
        if (table.isFree()) {
            return ActionResult.fail("Cannot take an order from Table " + tableNumber + " because it is empty.");
        }
        Customer occupant = table.getOccupant();
        if (occupant.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.fail("Customer #" + occupant.getId() + " at Table " + tableNumber
                    + " is not waiting to place an order (current status: " + occupant.getStatus().getLabel() + ").");
        }

        MenuItem chosenItem = foodChooser.chooseFoodFor(occupant);
        Order newOrder = new Order(nextOrderId++, occupant, table, chosenItem, currentTurn);
        orders.add(newOrder);
        table.assignOrder(newOrder);
        waiter.markTaskCompleted();

        return ActionResult.ok("Waiter took Order #" + newOrder.getId() + " from Customer #" + occupant.getId()
                + " at Table " + tableNumber + ": " + chosenItem.getDisplayName() + " ("
                + MenuItem.formatCents(chosenItem.getPriceCents()) + ", "
                + chosenItem.getPrepUnitsRequired() + " prep unit(s)).");
    }

    public ActionResult waiterServeOrder(int orderId) {
        if (!waiter.isAvailable()) {
            return ActionResult.fail("Waiter has already completed a task this turn.");
        }
        Order order = findOrder(orderId);
        if (order == null) {
            return ActionResult.fail("Order #" + orderId + " does not exist.");
        }
        if (order.getStatus() != OrderStatus.READY) {
            return ActionResult.fail("Order #" + orderId + " cannot be served because its status is "
                    + order.getStatus() + " (must be READY).");
        }
        if (!order.isReadyToServe(currentTurn)) {
            return ActionResult.fail("Order #" + orderId + " was not ready when the Waiter acted.");
        }

        order.setStatus(OrderStatus.SERVED);
        order.getCustomer().markServed(currentTurn);
        servedCount++;
        waiter.markTaskCompleted();

        return ActionResult.ok("Waiter served Order #" + order.getId() + " ("
                + order.getItem().getDisplayName() + ") to Customer #" + order.getCustomer().getId()
                + " at Table " + order.getTable().getTableNumber() + ". Status is now SERVED.");
    }

    public ActionResult waiterWait() {
        if (!waiter.isAvailable()) {
            return ActionResult.fail("Waiter has already completed a task this turn.");
        }
        waiter.markTaskCompleted();
        return ActionResult.ok("Waiter waits this turn.");
    }

    // --- Chef Actions (Step 2 & Step 3 part 2: Chef acts after Waiter) ---

    public ActionResult chefPrepareOrder(int orderId) {
        if (!chef.isAvailable()) {
            return ActionResult.fail("Chef has already completed a preparation unit this turn.");
        }
        Order order = findOrder(orderId);
        if (order == null) {
            return ActionResult.fail("Order #" + orderId + " does not exist.");
        }
        if (order.getStatus() != OrderStatus.IN_KITCHEN) {
            return ActionResult.fail("Chef cannot work on Order #" + orderId + " because its status is "
                    + order.getStatus() + " (must be IN_KITCHEN).");
        }

        order.advancePreparation(1, currentTurn);
        chef.markTaskCompleted();

        int done = order.getCompletedPrepUnits();
        int total = order.getItem().getPrepUnitsRequired();
        if (order.getStatus() == OrderStatus.READY) {
            return ActionResult.ok("Chef completed unit " + done + " of " + total + " for Order #"
                    + order.getId() + " (" + order.getItem().getDisplayName()
                    + "). Order is now READY (can be served next turn at the earliest).");
        } else {
            return ActionResult.ok("Chef completed unit " + done + " of " + total + " for Order #"
                    + order.getId() + " (" + order.getItem().getDisplayName() + ").");
        }
    }

    public ActionResult chefWait() {
        if (!chef.isAvailable()) {
            return ActionResult.fail("Chef has already completed a task this turn.");
        }
        chef.markTaskCompleted();
        return ActionResult.ok("Chef waits this turn.");
    }

    // --- Step 4: Eating and Payment ---

    public List<String> processEatingAndPayment() {
        List<String> events = new ArrayList<>();

        // 1. Cashier automatically handles at most one READY_TO_PAY customer who became ready on an earlier turn.
        Table payableTable = cashier.findOldestPayableTable(tables, currentTurn);
        if (payableTable != null) {
            Customer customer = payableTable.getOccupant();
            Order order = payableTable.getCurrentOrder();
            int billCents = order.getItem().getPriceCents();

            revenueCents += billCents;
            customer.setStatus(CustomerStatus.PAID);
            order.setStatus(OrderStatus.PAID);
            paidCustomerSatisfactions.add(customer.getSatisfaction());
            payableTable.freeTable();

            events.add("Cashier collected " + MenuItem.formatCents(billCents) + " from Customer #"
                    + customer.getId() + " (Order #" + order.getId() + "). Status: PAID, Table "
                    + payableTable.getTableNumber() + " freed, Satisfaction "
                    + customer.getSatisfaction() + " recorded.");
        }

        // 2. Any customer served on an earlier turn finishes one full turn of eating and becomes READY_TO_PAY.
        for (Table table : tables) {
            if (!table.isFree()) {
                Customer c = table.getOccupant();
                if (c.getStatus() == CustomerStatus.SERVED_EATING && c.getServedTurn() < currentTurn) {
                    c.markReadyToPay(currentTurn);
                    events.add("Customer #" + c.getId() + " at Table " + table.getTableNumber()
                            + " finished eating and is now READY_TO_PAY.");
                }
            }
        }

        return events;
    }

    // --- Step 5: Waiting and Abandonment ---

    public List<String> processWaitingAndAbandonment() {
        List<String> events = new ArrayList<>();

        // Check waiting queue customers
        Iterator<Customer> queueIt = waitingQueue.iterator();
        while (queueIt.hasNext()) {
            Customer c = queueIt.next();
            c.applyWaitingPenalty();
            if (c.hasAbandoned()) {
                c.setStatus(CustomerStatus.ABANDONED);
                unhappyDepartures++;
                queueIt.remove();
                events.add("Customer #" + c.getId()
                        + " reached 0 satisfaction in the waiting queue and left without paying!");
            }
        }

        // Check seated customers
        for (Table table : tables) {
            if (!table.isFree()) {
                Customer c = table.getOccupant();
                c.applyWaitingPenalty();
                if (c.hasAbandoned()) {
                    c.setStatus(CustomerStatus.ABANDONED);
                    Order order = table.getCurrentOrder();
                    if (order != null && order.getStatus() != OrderStatus.PAID) {
                        order.setStatus(OrderStatus.CANCELLED);
                    }
                    unhappyDepartures++;
                    table.freeTable();
                    events.add("Customer #" + c.getId() + " at Table " + table.getTableNumber()
                            + " reached 0 satisfaction and left without paying! Unfinished order cancelled and Table "
                            + table.getTableNumber() + " freed.");
                }
            }
        }

        return events;
    }

    public void advanceTurnCounter() {
        currentTurn++;
    }

    // --- Queries & Objective Evaluation (Step 6) ---

    public boolean isVictory() {
        return getPaidCustomersCount() >= TARGET_PAID_CUSTOMERS
                && revenueCents >= TARGET_REVENUE_CENTS
                && getAveragePaidSatisfaction() >= TARGET_AVG_SATISFACTION;
    }

    public int getPaidCustomersCount() {
        return paidCustomerSatisfactions.size();
    }

    public double getAveragePaidSatisfaction() {
        if (paidCustomerSatisfactions.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (int s : paidCustomerSatisfactions) {
            sum += s;
        }
        return (double) sum / paidCustomerSatisfactions.size();
    }

    public String getFormattedAveragePaidSatisfaction() {
        if (paidCustomerSatisfactions.isEmpty()) {
            return "N/A";
        }
        return String.format("%.2f", getAveragePaidSatisfaction());
    }

    private Table findTable(int tableNumber) {
        for (Table t : tables) {
            if (t.getTableNumber() == tableNumber) {
                return t;
            }
        }
        return null;
    }

    private Order findOrder(int orderId) {
        for (Order o : orders) {
            if (o.getId() == orderId) {
                return o;
            }
        }
        return null;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    public Queue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Order> getActiveOrders() {
        List<Order> active = new ArrayList<>();
        for (Order o : orders) {
            if (o.getStatus() != OrderStatus.PAID && o.getStatus() != OrderStatus.CANCELLED) {
                active.add(o);
            }
        }
        return active;
    }

    public String getFoodRuleDescription() {
        return foodChooser.getRuleDescription();
    }
}