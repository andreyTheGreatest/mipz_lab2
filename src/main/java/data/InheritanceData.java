package data;

import java.util.List;

public class InheritanceData {

    public String className;
    public String baseClass;
    public List<String> interfaceClasses;

    public InheritanceData(String className, String baseClass, List<String> interfaceClasses) {
        this.className = className;
        this.baseClass = baseClass;
        this.interfaceClasses = interfaceClasses;
    }
}
