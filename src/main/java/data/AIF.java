package data;

public class AIF {
    public final int totalAttrs;
    public final int unmod;
    public final String className;

    public AIF(String className, int unmod, int totalAttrs) {
        this.className = className;
        this.unmod = unmod;
        this.totalAttrs = totalAttrs;
    }
}
