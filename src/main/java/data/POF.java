package data;

public class POF {
    public final int inheritedMethods;
    public final int newMethods;
    public final String className;


    public POF(String className, int inheritedMethods, int newMethods) {
        this.className = className;
        this.inheritedMethods = inheritedMethods;
        this.newMethods = newMethods;
    }
}
