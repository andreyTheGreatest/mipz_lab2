package parser;

import data.*;
import tree.TreeNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Parser {
    public TreeNode<String> root = new TreeNode<>("_");
    public List<MHF> mhfList = new ArrayList<>();
    public List<MIF> mifList = new ArrayList<>();
    public List<AHF> ahfList = new ArrayList<>();
    public List<AIF> aifList = new ArrayList<>();
    public List<POF> pofList = new ArrayList<>();

    public Parser() {}

    public void parseFile(String fileName) throws IOException {
        Objects.requireNonNull(fileName);
        final Path filePath = Paths.get(fileName);
        if (Files.isDirectory(filePath)) {
            File[] dirFiles = new File(fileName).listFiles();
            if (dirFiles != null && dirFiles.length > 0) {
                Stream.of(dirFiles).forEach(file -> {
                    try {
                        parseFile(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        if (!Files.isDirectory(filePath)) {
            List<String> words = Arrays.stream(String.join(" ", Files.lines(Paths.get(fileName)).toList()).split("[,\s]"))
                    .map(String::trim).toList();
            for (Iterator<String> it = words.iterator(); it.hasNext(); ) {
                String next = it.next();
                if (next.contains("class") || next.contains("interface")) {
                    String className = parseClassScope(it);
                    parseClassBody(it, className);
                }
            }
        }
    }

    private void parseClassBody(Iterator<String> it, String className) {
        int openBarsCount = 1;
        int closedBarsCount = 0;

        int publicMethods = 0;
        int hiddenMethods = 0;

        int publicAttrs = 0;
        int hiddenAttrs = 0;
        int unmodMethods = 0;
        int unmodAttrs = 0;
        int inherited = 0;
        String currentModifier = "";
        while ((openBarsCount - closedBarsCount) != 0 && it.hasNext()) {
            String finalNext = it.next();
            if (finalNext.equals("public")) currentModifier = "public";
            if (finalNext.equals("private") || finalNext.equals("protected")) currentModifier = "hidden";

            if (finalNext.contains("super.") && (openBarsCount - closedBarsCount) > 1) // unmodified inherited method found
                unmodMethods++;

            if (finalNext.contains("super.") && (openBarsCount - closedBarsCount) == 1)
                unmodAttrs++;

            if (finalNext.contains("=") && (openBarsCount - closedBarsCount) == 1) {
                // property found
                if (currentModifier.equals("public")) publicAttrs++;
                else if (currentModifier.equals("hidden")) hiddenAttrs++;
            }
            if (finalNext.contains("@Override")) {
                inherited++;
            }
            if (finalNext.equals("{")) {
                // method found
                openBarsCount++;
                if (currentModifier.equals("public")) publicMethods++;
                if (currentModifier.equals("hidden")) hiddenMethods++;
            } else if (finalNext.equals("}")) closedBarsCount++;
        }
        mhfList.add(new MHF(publicMethods, hiddenMethods));
        mifList.add(new MIF(className, unmodMethods, publicMethods + hiddenMethods));

        ahfList.add(new AHF(className, publicAttrs, hiddenAttrs));
        aifList.add(new AIF(className, unmodAttrs, publicAttrs + hiddenAttrs));
        pofList.add(new POF(className, inherited - unmodMethods, publicMethods + hiddenMethods - inherited));
    }

    private String parseClassScope(Iterator<String> it) {
        String className, baseClass = null;
        List<String> interfaceClasses = new ArrayList<>();
        className = it.next();
        String word;
        if ((word = it.next()).equals("extends")) {
            baseClass = it.next();
        }
        if (word.equals("implements") || it.next().equals("implements")) {
            String interfaceClass;
            while (!(interfaceClass = it.next()).equals("{")) {
                interfaceClasses.add(interfaceClass);
            }
        }
        pushToTree(className, baseClass, interfaceClasses);
        return className;
    }

    private void pushToTree(String className, String baseClass, List<String> interfaceClasses) {

        // if simple class declaration mentioned
        if (baseClass == null && interfaceClasses.isEmpty())
            root.addChild(className);

        // if base class is mentioned and found/not found
        TreeNode<String> node = root.findTreeNode(((treeData) -> treeData == null ? 1 : treeData.equals(baseClass) ? 0 : 1));

        if (node != null) {
            node.addChild(className);
        } else root.addChild(baseClass);

        // if interface mentioned and found/not found
        interfaceClasses.forEach(i -> {
            TreeNode<String> n = root.findTreeNode(((treeData) -> treeData == null ? 1 : treeData.equals(i) ? 0 : 1));
            if (n != null) n.addChild(i);
            else root.addChild(i);
        });
    }
}
