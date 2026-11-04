import java.util.Comparator;
import java.util.List;

class Cashier {
    /**
     * Automatically handles at most one READY_TO_PAY customer per turn, oldest first.
     * A customer who became READY_TO_PAY this turn pays next turn at the earliest.
     */
    public Table findOldestPayableTable(List<Table> tables, int currentTurn) {
        return tables.stream()
                .filter(t -> !t.isFree())
                .filter(t -> t.getOccupant().getStatus() == CustomerStatus.READY_TO_PAY)
                .filter(t -> t.getOccupant().getReadyToPayTurn() < currentTurn)
                .min(Comparator
                        .comparingInt((Table t) -> t.getOccupant().getReadyToPayTurn())
                        .thenComparingInt(t -> t.getOccupant().getId()))
                .orElse(null);
    }
}