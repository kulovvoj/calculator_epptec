package eu.epptec.calculator;

public class OperatorAttr {
    enum Assoc {
        LEFT,
        RIGHT
    }

    public OperatorAttr(int preced, Assoc assoc) {
        this.preced = preced;
        this.assoc = assoc;
    }

    public int getPreced() {return this.preced;}
    public Assoc getAssoc() {return this.assoc;}

    private int preced;
    private Assoc assoc;
}
