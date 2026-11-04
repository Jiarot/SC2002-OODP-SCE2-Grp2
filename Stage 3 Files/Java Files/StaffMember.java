/**
 * Stage 3: common turn-capacity rule for staff the manager directs (Waiter, Chef, Host).
 * Each completes at most one task per turn.
 */
abstract class StaffMember {
    private boolean taskCompletedThisTurn = false;

    public abstract String getRoleName();

    public void resetTurn() {
        this.taskCompletedThisTurn = false;
    }

    public boolean isAvailable() {
        return !taskCompletedThisTurn;
    }

    public void markTaskCompleted() {
        this.taskCompletedThisTurn = true;
    }
}
