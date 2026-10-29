class Chef {
    private boolean taskCompletedThisTurn = false;

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