import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ConsoleUI {
    // ANSI Reset
    private static final String RESET = "\u001B[0m";

    // Regular & Bold Foreground Colors (Unused RED and BLUE removed)
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private static final String BOLD_RED = "\u001B[1;31m";
    private static final String BOLD_GREEN = "\u001B[1;32m";
    private static final String BOLD_YELLOW = "\u001B[1;33m";
    private static final String BOLD_BLUE = "\u001B[1;34m";
    private static final String BOLD_PURPLE = "\u001B[1;35m";
    private static final String BOLD_CYAN = "\u001B[1;36m";
    private static final String BOLD_WHITE = "\u001B[1;37m";

    // High-Visibility Banner Backgrounds
    private static final String TURN_BADGE = "\u001B[44;1;37m";    // Blue background, bold white text
    private static final String VICTORY_BADGE = "\u001B[42;1;30m"; // Green background, bold dark text
    private static final String DEFEAT_BADGE = "\u001B[41;1;37m";  // Red background, bold white text

    private final RestaurantController controller;
    private final Scanner scanner;

    public ConsoleUI(RestaurantController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void runGame() {
        printWelcomeBanner();

        while (controller.getCurrentTurn() <= RestaurantController.MAX_TURNS) {
            Customer arrived = controller.startTurnAndProcessArrivals();

            displayStartOfTurn(arrived);

            promptManagerForWaiterPriority();
            promptManagerForChefPriority();

            List<String> step4Events = controller.processEatingAndPayment();
            List<String> step5Events = controller.processWaitingAndAbandonment();

            displayEndOfTurn(step4Events, step5Events);

            controller.advanceTurnCounter();
        }

        displayFinalResult();
    }

    private void printWelcomeBanner() {
        System.out.println(BOLD_CYAN + "======================================================================" + RESET);
        System.out.println(BOLD_WHITE + "         STAGE 2: CUSTOMER BEHAVIOUR EXTENSION (MANAGER MODE)         " + RESET);
        System.out.println(BOLD_CYAN + "======================================================================" + RESET);
        System.out.println(BOLD + "Role      : " + RESET + "You are the Restaurant Manager.");
        System.out.println(BOLD + "Challenge : " + RESET + "Staff time is limited! You decide which customers and");
        System.out.println("            orders come first each turn.");
        System.out.println(BOLD + "Staff Cap : " + RESET + CYAN + "Waiter x1" + RESET + " (1 task/turn) | "
                + YELLOW + "Chef x1" + RESET + " (1 prep unit/turn)");
        System.out.println("            " + GREEN + "Cashier x1" + RESET + " (1 payment/turn, automatic oldest first)");
        System.out.println(BOLD + "Food Rule : " + RESET + controller.getFoodRuleDescription());
        System.out.println(BOLD + "Customers : " + RESET + "Customer types differ in patience, billing and behaviour:");
        for (String line : controller.getCustomerTypeLegend()) {
            System.out.println("            - " + line);
        }
        System.out.println(BOLD + "Objective : " + RESET + "End of Turn 18 -> "
                + BOLD_GREEN + ">= 4 Paid Customers" + RESET + ", "
                + BOLD_GREEN + ">= $45.00 Revenue" + RESET + ",");
        System.out.println("            and " + BOLD_GREEN + ">= 60.00 Average Satisfaction" + RESET + " of Paid Customers.");
        System.out.println(BOLD_CYAN + "======================================================================\n" + RESET);
    }

    private void displayStartOfTurn(Customer arrived) {
        System.out.println(BOLD_BLUE + "======================================================================" + RESET);
        System.out.printf("%s  TURN %2d OF %2d  %s  %s[START-OF-TURN MANAGER BRIEFING]%s%n",
                TURN_BADGE,
                controller.getCurrentTurn(),
                RestaurantController.MAX_TURNS,
                RESET,
                BOLD_CYAN,
                RESET);
        System.out.println(BOLD_BLUE + "======================================================================" + RESET);

        if (arrived != null) {
            System.out.println(BOLD_PURPLE + "[Step 1: Arrivals] " + RESET + "Customer #" + arrived.getId()
                    + " (" + arrived.getCustomerType() + ") joined the waiting queue (Satisfaction: "
                    + colorizeSatisfaction(arrived.getSatisfaction()) + ").");
            System.out.println("                     " + arrived.getBehaviourNote());
        } else {
            System.out.println(PURPLE + "[Step 1: Arrivals] " + RESET + "No scheduled arrival this turn.");
        }

        System.out.printf("%sRevenue:%s %s%s%s | %sPaid Customers:%s %s%d%s | %sAvg Paid Satisfaction:%s %s%n",
                BOLD, RESET,
                BOLD_GREEN, MenuItem.formatCents(controller.getRevenueCents()), RESET,
                BOLD, RESET,
                BOLD_CYAN, controller.getPaidCustomersCount(), RESET,
                BOLD, RESET,
                colorizeAvgSatisfaction(controller.getFormattedAveragePaidSatisfaction()));

        // Waiting Queue
        System.out.print(BOLD + "\nWaiting Queue: " + RESET);
        if (controller.getWaitingQueue().isEmpty()) {
            System.out.println(GREEN + "(Empty)" + RESET);
        } else {
            List<String> qDesc = new ArrayList<>();
            for (Customer c : controller.getWaitingQueue()) {
                qDesc.add(String.format("%sCustomer #%d%s (%s) [Sat: %s, loses %d/turn]",
                        BOLD_YELLOW, c.getId(), RESET, c.getCustomerType(),
                        colorizeSatisfaction(c.getSatisfaction()), c.getSatisfactionLossPerTurn()));
            }
            System.out.println(String.join(", ", qDesc));
        }

        // Occupied Tables & Customer Satisfaction
        System.out.println(BOLD + "\nTables:" + RESET);
        for (Table t : controller.getTables()) {
            if (t.isFree()) {
                System.out.printf("  - Table %d: %s[FREE]%s%n", t.getTableNumber(), BOLD_GREEN, RESET);
            } else {
                Customer c = t.getOccupant();
                Order o = t.getCurrentOrder();
                String orderInfo = (o == null)
                        ? YELLOW + "No order placed yet" + RESET
                        : String.format("Order #%d (%s, %s, Status: %s, Prep: %d/%d)",
                                o.getId(),
                                BOLD + o.getItem().getDisplayName() + RESET,
                                formatBill(o),
                                colorizeOrderStatus(o.getStatus()),
                                o.getCompletedPrepUnits(),
                                o.getItem().getPrepUnitsRequired());

                System.out.printf("  - Table %d: %sCustomer #%d%s (%s) | Sat: %s | Status: %s | %s%n",
                        t.getTableNumber(),
                        BOLD_WHITE, c.getId(), RESET, c.getCustomerType(),
                        colorizeSatisfaction(c.getSatisfaction()),
                        colorizeCustomerStatus(c.getStatus()),
                        orderInfo);
            }
        }

        // Active Orders
        System.out.println(BOLD + "\nActive Orders:" + RESET);
        List<Order> activeOrders = controller.getActiveOrders();
        if (activeOrders.isEmpty()) {
            System.out.println("  (None)");
        } else {
            for (Order o : activeOrders) {
                System.out.printf("  - %sOrder #%d%s | Table %d (Customer #%d, %s) | %s (%s) | Status: %s | Prep: %d/%d%n",
                        BOLD_WHITE, o.getId(), RESET,
                        o.getTable().getTableNumber(),
                        o.getCustomer().getId(),
                        o.getCustomer().getCustomerType(),
                        BOLD + o.getItem().getDisplayName() + RESET,
                        formatBill(o),
                        colorizeOrderStatus(o.getStatus()),
                        o.getCompletedPrepUnits(),
                        o.getItem().getPrepUnitsRequired());
            }
        }

        // Staff Tasks Available
        System.out.println(BOLD + "\nStaff Tasks Available This Turn (Limited Capacity):" + RESET);
        System.out.println("  " + BOLD_CYAN + "* Waiter (Choose at most 1 task):" + RESET);
        for (String task : buildWaiterTasksSummary()) {
            System.out.println("      - " + task);
        }
        System.out.println("  " + BOLD_YELLOW + "* Chef (Choose at most 1 prep unit; may cook an order taken this turn):" + RESET);
        for (String task : buildChefTasksSummary()) {
            System.out.println("      - " + task);
        }
    }

    private void promptManagerForWaiterPriority() {
        while (true) {
            System.out.println("\n" + BOLD_CYAN + "[Manager Decision 1/2 -> Set Waiter Priority (1 task max)]" + RESET);
            System.out.println("  " + BOLD + "1)" + RESET + " Seat a queued customer at a free table");
            System.out.println("  " + BOLD + "2)" + RESET + " Take a seated customer's order");
            System.out.println("  " + BOLD + "3)" + RESET + " Serve a READY order");
            System.out.println("  " + BOLD + "4)" + RESET + " Wait (do nothing)");
            System.out.print(BOLD_CYAN + "Select Waiter priority (1-4): " + RESET);

            int option = readInt();
            ActionResult result = switch (option) {
                case 1 -> {
                    Customer firstInQueue = controller.getWaitingQueue().peek();
                    if (firstInQueue == null) {
                        yield ActionResult.fail("No customers are currently in the waiting queue.");
                    }
                    int suggestedId = firstInQueue.getId();
                    System.out.print("  Which Customer ID should be seated first? (e.g., " + suggestedId + "): ");
                    int custId = readInt();
                    System.out.print("  Assign to which Table Number (1-" + RestaurantController.NUM_TABLES + ")?: ");
                    int tableNum = readInt();
                    yield controller.waiterSeatCustomer(custId, tableNum);
                }
                case 2 -> {
                    System.out.print("  Which Table Number's order comes first (1-" + RestaurantController.NUM_TABLES + ")?: ");
                    int tableNum = readInt();
                    yield controller.waiterTakeOrder(tableNum);
                }
                case 3 -> {
                    System.out.print("  Which READY Order ID should be served first?: ");
                    int orderId = readInt();
                    yield controller.waiterServeOrder(orderId);
                }
                case 4 -> controller.waiterWait();
                default -> ActionResult.fail("Please enter a valid option between 1 and 4.");
            };

            if (result.isSuccess()) {
                System.out.println("  " + BOLD_GREEN + "-> " + result.getMessage() + RESET);
                printCustomerReaction(result.getReaction());
                break;
            } else {
                System.out.println("  " + BOLD_RED + "[Invalid Priority Choice] " + result.getMessage()
                        + " (Waiter task not used; please choose again.)" + RESET);
            }
        }
    }

    private void promptManagerForChefPriority() {
        System.out.println("\n" + BOLD_YELLOW + "[Manager Decision 2/2 -> Set Chef Kitchen Priority (1 prep unit max)]" + RESET);
        System.out.println(BOLD + "  Current Kitchen Queue:" + RESET);
        for (String task : buildChefTasksSummary()) {
            System.out.println("    * " + task);
        }

        while (true) {
            System.out.println("  " + BOLD + "1)" + RESET + " Advance an IN_KITCHEN order by 1 preparation unit");
            System.out.println("  " + BOLD + "2)" + RESET + " Wait (do nothing)");
            System.out.print(BOLD_YELLOW + "Select Chef priority (1-2): " + RESET);

            int option = readInt();
            ActionResult result = switch (option) {
                case 1 -> {
                    System.out.print("  Which Order ID gets the Chef's 1 preparation unit this turn?: ");
                    int orderId = readInt();
                    yield controller.chefPrepareOrder(orderId);
                }
                case 2 -> controller.chefWait();
                default -> ActionResult.fail("Please enter 1 or 2.");
            };

            if (result.isSuccess()) {
                System.out.println("  " + BOLD_GREEN + "-> " + result.getMessage() + RESET);
                break;
            } else {
                System.out.println("  " + BOLD_RED + "[Invalid Priority Choice] " + result.getMessage()
                        + " (Chef task not used; please choose again.)" + RESET);
            }
        }
    }

    private List<String> buildWaiterTasksSummary() {
        List<String> tasks = new ArrayList<>();

        List<Integer> freeTableNums = new ArrayList<>();
        for (Table t : controller.getTables()) {
            if (t.isFree()) {
                freeTableNums.add(t.getTableNumber());
            }
        }
        if (!controller.getWaitingQueue().isEmpty() && !freeTableNums.isEmpty()) {
            for (Customer c : controller.getWaitingQueue()) {
                tasks.add(String.format("Seat Customer #%d (%s, Sat: %d) at free Table %s",
                        c.getId(), c.getCustomerType(), c.getSatisfaction(), freeTableNums));
            }
        }

        for (Table t : controller.getTables()) {
            if (!t.isFree() && t.getOccupant().getStatus() == CustomerStatus.SEATED) {
                Customer c = t.getOccupant();
                tasks.add(String.format("Take order from Customer #%d (%s, Sat: %d) at Table %d",
                        c.getId(), c.getCustomerType(), c.getSatisfaction(), t.getTableNumber()));
            }
        }

        for (Order o : controller.getActiveOrders()) {
            if (o.getStatus() == OrderStatus.READY && o.isReadyToServe(controller.getCurrentTurn())) {
                tasks.add(String.format("Serve READY Order #%d (%s) to Customer #%d (%s, Sat: %d) at Table %d",
                        o.getId(), o.getItem().getDisplayName(), o.getCustomer().getId(),
                        o.getCustomer().getCustomerType(),
                        o.getCustomer().getSatisfaction(), o.getTable().getTableNumber()));
            }
        }

        tasks.add("Wait (conserve turn / do nothing)");
        return tasks;
    }

    private List<String> buildChefTasksSummary() {
        List<String> tasks = new ArrayList<>();
        for (Order o : controller.getActiveOrders()) {
            if (o.getStatus() == OrderStatus.IN_KITCHEN) {
                tasks.add(String.format(
                        "Cook Order #%d (%s, Prep: %d/%d done, %d unit(s) left) for Customer #%d (%s, Sat: %d) at Table %d",
                        o.getId(), o.getItem().getDisplayName(),
                        o.getCompletedPrepUnits(), o.getItem().getPrepUnitsRequired(),
                        o.getRemainingPrepUnits(), o.getCustomer().getId(),
                        o.getCustomer().getCustomerType(),
                        o.getCustomer().getSatisfaction(), o.getTable().getTableNumber()));
            }
        }
        tasks.add("Wait (no cooking this turn)");
        return tasks;
    }

    private void displayEndOfTurn(List<String> step4Events, List<String> step5Events) {
        System.out.println("\n" + BOLD_PURPLE + "--- End of Turn " + controller.getCurrentTurn() + " Updates ---" + RESET);
        for (String e : step4Events) {
            System.out.println("  " + BOLD_GREEN + "[Step 4: Eating/Payment] " + RESET + e);
        }
        for (String e : step5Events) {
            System.out.println("  " + BOLD_RED + "[Step 5: Abandonment] " + RESET + e);
        }

        String unhappyColor = controller.getUnhappyDepartures() > 0 ? BOLD_RED : BOLD_GREEN;
        System.out.printf(
                "%sEnd-of-Turn Results ->%s Served Count: %s%d%s (Paid: %s%d%s) | Unhappy Departures: %s%d%s | Revenue: %s%s%s | Avg Paid Satisfaction: %s | Turns Used: %s%d/%d%s%n%n",
                BOLD_CYAN, RESET,
                BOLD_WHITE, controller.getServedCount(), RESET,
                BOLD_CYAN, controller.getPaidCustomersCount(), RESET,
                unhappyColor, controller.getUnhappyDepartures(), RESET,
                BOLD_GREEN, MenuItem.formatCents(controller.getRevenueCents()), RESET,
                colorizeAvgSatisfaction(controller.getFormattedAveragePaidSatisfaction()),
                BOLD_YELLOW, controller.getCurrentTurn(), RestaurantController.MAX_TURNS, RESET);
    }

    private void displayFinalResult() {
        System.out.println(BOLD_CYAN + "======================================================================" + RESET);
        System.out.println(BOLD_WHITE + "                        FINAL GAME RESULT                             " + RESET);
        System.out.println(BOLD_CYAN + "======================================================================" + RESET);
        System.out.println("Turns Used                : " + BOLD_YELLOW + RestaurantController.MAX_TURNS + RESET);
        System.out.println("Dishes Served             : " + BOLD_WHITE + controller.getServedCount() + RESET);

        String paidColor = controller.getPaidCustomersCount() >= RestaurantController.TARGET_PAID_CUSTOMERS
                ? BOLD_GREEN : BOLD_RED;
        System.out.println("Paid Customers            : " + paidColor + controller.getPaidCustomersCount() + RESET
                + " (Target: >= " + RestaurantController.TARGET_PAID_CUSTOMERS + ")");

        String unhappyColor = controller.getUnhappyDepartures() == 0 ? BOLD_GREEN : BOLD_RED;
        System.out.println("Unhappy Departures        : " + unhappyColor + controller.getUnhappyDepartures() + RESET);

        String revColor = controller.getRevenueCents() >= RestaurantController.TARGET_REVENUE_CENTS
                ? BOLD_GREEN : BOLD_RED;
        System.out.println("Total Revenue             : " + revColor
                + MenuItem.formatCents(controller.getRevenueCents()) + RESET
                + " (Target: >= " + MenuItem.formatCents(RestaurantController.TARGET_REVENUE_CENTS) + ")");

        System.out.println("Average Paid Satisfaction : "
                + colorizeAvgSatisfaction(controller.getFormattedAveragePaidSatisfaction())
                + " (Target: >= " + String.format("%.2f", RestaurantController.TARGET_AVG_SATISFACTION) + ")");
        System.out.println(BOLD_CYAN + "----------------------------------------------------------------------" + RESET);

        if (controller.isVictory()) {
            System.out.println(VICTORY_BADGE + " VICTORY! " + RESET + " "
                    + BOLD_GREEN + "Your management priorities met all three targets in Section 2.1." + RESET);
        } else {
            System.out.println(DEFEAT_BADGE + " DEFEAT! " + RESET + " "
                    + BOLD_RED + "One or more targets in Section 2.1 were not met." + RESET);
        }
        System.out.println(BOLD_CYAN + "======================================================================" + RESET);
    }

    // --- Color Formatting Helper Methods ---

    /** Shows the bill locked on the order; if it differs from the menu price, the list price is shown too. */
    private String formatBill(Order o) {
        String bill = GREEN + MenuItem.formatCents(o.getBillCents()) + RESET;
        if (o.getBillCents() != o.getItem().getPriceCents()) {
            bill += " (list " + MenuItem.formatCents(o.getItem().getPriceCents()) + ")";
        }
        return bill;
    }

    /** Prints a customer's spoken reaction in colour. The rules for the reaction live in the Customer classes. */
    private void printCustomerReaction(CustomerReaction reaction) {
        String color = switch (reaction) {
            case NONE -> null;
            case DELIGHTED -> BOLD_GREEN;
            case FURIOUS -> BOLD_RED;
            case UNIMPRESSED -> BOLD_YELLOW;
        };
        if (color != null) {
            System.out.println("     " + color + "\"" + reaction.getMessage() + "\"" + RESET);
        }
    }

    private String colorizeSatisfaction(int sat) {
        if (sat >= 60) {
            return BOLD_GREEN + sat + RESET;
        } else if (sat >= 33) {
            return BOLD_YELLOW + sat + RESET;
        } else {
            return BOLD_RED + sat + RESET;
        }
    }

    private String colorizeAvgSatisfaction(String formattedAvg) {
        if ("N/A".equals(formattedAvg)) {
            return YELLOW + "N/A" + RESET;
        }
        double val = controller.getAveragePaidSatisfaction();
        if (val >= RestaurantController.TARGET_AVG_SATISFACTION) {
            return BOLD_GREEN + formattedAvg + RESET;
        } else {
            return BOLD_RED + formattedAvg + RESET;
        }
    }

    private String colorizeOrderStatus(OrderStatus status) {
        return switch (status) {
            case IN_KITCHEN -> BOLD_YELLOW + status.name() + RESET;
            case READY -> BOLD_GREEN + status.name() + RESET;
            case SERVED -> CYAN + status.name() + RESET;
            case PAID -> GREEN + status.name() + RESET;
            case CANCELLED -> BOLD_RED + status.name() + RESET;
        };
    }

    private String colorizeCustomerStatus(CustomerStatus status) {
        return switch (status) {
            case WAITING_IN_QUEUE, SEATED, ORDERED -> YELLOW + status.getLabel() + RESET;
            case SERVED_EATING -> CYAN + status.getLabel() + RESET;
            case READY_TO_PAY -> BOLD_GREEN + status.getLabel() + RESET;
            case PAID -> GREEN + status.getLabel() + RESET;
            case ABANDONED -> BOLD_RED + status.getLabel() + RESET;
        };
    }

    private int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("  " + BOLD_RED + "Invalid input. Please enter an integer: " + RESET);
            }
        }
    }
}