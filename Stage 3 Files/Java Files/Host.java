/**
 * Stage 3: the Host seats at most one queued customer per turn,
 * in addition to the Waiter's task. The Host acts before the Waiter.
 */
class Host extends StaffMember {
    @Override
    public String getRoleName() {
        return "Host";
    }
}
