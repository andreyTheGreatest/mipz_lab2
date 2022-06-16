package data;

public class AHF {
    public final int hiddenAttrs;
    public final int publicAttrs;
    public final String className;

    public AHF(String className, int publicAttrs, int hiddenAttrs) {
        this.className = className;
        this.publicAttrs = publicAttrs;
        this.hiddenAttrs = hiddenAttrs;
    }
}
