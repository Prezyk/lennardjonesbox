public class SimulationCalculationsFinishedEvent implements Event {
    private final Molecules molecules;

    public SimulationCalculationsFinishedEvent(Molecules molecules) {
        this.molecules = molecules;
    }

    public Molecules getMolecules() {
        return molecules;
    }
}
