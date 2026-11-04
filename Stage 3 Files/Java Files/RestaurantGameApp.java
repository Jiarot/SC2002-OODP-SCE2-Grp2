import java.util.Scanner;

public class RestaurantGameApp {
    public static void main(String[] args) {
        // Default fixed seed (42L) guarantees reproducible runs for TAs.
        // Pass "--cycle" as a command-line argument to use a deterministic menu cycle instead.
        boolean useCycleRule = args.length > 0 && args[0].equalsIgnoreCase("--cycle");
        CustomerFoodChooser foodChooser = new CustomerFoodChooser(42L, useCycleRule);
        RestaurantController controller = new RestaurantController(foodChooser);
        ConsoleUI ui = new ConsoleUI(controller, new Scanner(System.in));
        ui.runGame();
    }
}