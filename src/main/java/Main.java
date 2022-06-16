import data.*;
import parser.Parser;
import tree.TreeNode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Main {

    private static final DecimalFormat df = new DecimalFormat("0.00%");

    public static void main(String... args) throws IOException {
        Parser parser = new Parser();
        parser.parseFile("src/main/java");
        TreeNode<String> root = parser.root;
        System.out.println("Total depth: " + TreeNode.maxDepth(root));
        String className = "Test";
        TreeNode<String> node = root.findTreeNode(data -> data != null && data.equals(className) ? 0 : 1);
        System.out.println("DIT: " + node.getLevel());

        System.out.println("NOC: " + node.children.size());

        double mhf = calculateMHF(parser);
        System.out.println("MHF total: " + df.format(mhf));

        double mif = calculateMIF(parser, className);
        System.out.println("[" + className + "]" + "MIF: " + df.format(mif));

        double ahf = calculateAHF(parser, className);
        System.out.println("[" + className + "]" + "AHF: " + df.format(ahf));

        double aif = calculateAIF(parser, className);
        System.out.println("[" + className + "]" + "AIF: " + df.format(aif));

        double pof = calculatePOF(parser, className);
        System.out.println("[" + className + "]" + "POF: " + df.format(pof));
    }

    private static double calculatePOF(Parser parser, String className) {
        List<POF> pofList = parser.pofList;
        int inheritedMethods = 0;
        int newMethods = 0;
        for (POF pof : pofList) {
            inheritedMethods += pof.inheritedMethods;
            newMethods += pof.newMethods;
        }
        return (inheritedMethods * 1.0 / newMethods);
    }

    private static double calculateAIF(Parser parser, String className) {
        AIF aif = parser.aifList.stream().filter(a -> a.className.equals(className)).findFirst().get();
        return (aif.unmod * 1.0 / aif.totalAttrs);
    }

    private static double calculateAHF(Parser parser, String className) {
        AHF ahf = parser.ahfList.stream().filter(a -> a.className.equals(className)).findFirst().get();
        return (ahf.hiddenAttrs * 1.0 / (ahf.hiddenAttrs + ahf.publicAttrs));
    }

    private static double calculateMHF(Parser parser) {
        List<MHF> mhfList = parser.mhfList;
        int pub = 0;
        int hid = 0;
        for (MHF mhf : mhfList) {
            hid += mhf.hiddenMethods;
            pub += mhf.publicMethods;
        }
        return (pub * 1.0 / (pub + hid));
    }

    private static double calculateMIF(Parser parser, String className) {
        MIF mif = parser.mifList.stream().filter(m -> m.className.equals(className)).findFirst().get();
        return (mif.unmodified * 1.0 / mif.total);
    }
}
