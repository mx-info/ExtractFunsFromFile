package src.cpp.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.cpp.CPP14Parser;
import src.cpp.CPP14ParserBaseListener;


import javax.swing.*;
import java.util.*;

public class CppAllWalker extends CPP14ParserBaseListener {


    StringBuilder tokens;

    public CppAllWalker(StringBuilder tokens) {
        this.tokens = tokens;
    }

    //	public void enterSourceUnit(CPP14Parser.CompilationUnitContext ctx) {
    public void enterSourceUnit(CPP14Parser.TranslationUnitContext ctx) {
        List<Token> sourceUnitTokens = getTokenList(ctx);
        List<Integer> lineSpans = getLineSpan(ctx);
        tokens.append(Arrays.toString(lineSpans.toArray()) + "\t");

        tokens.append(Arrays.toString(sourceUnitTokens.toArray()) + "\n");
    }

    public static List<Integer> getLineSpan(ParseTree tree) {

        List<Integer> lineSpan = new ArrayList<Integer>();
        lineSpan.add(((ParserRuleContext) tree).getStart().getLine());
        lineSpan.add(((ParserRuleContext) tree).getStop().getLine());
        return lineSpan;
    }

    public static List<Token> getTokenList(ParseTree tree) {
        List<Token> tokens = new ArrayList<Token>();
//        inOrderTraversal(tokens, tree);
        extractFunctionByInorderTraversal(tokens, tree, new ArrayList<>());
        return tokens;
    }

    public static void inOrderTraversal(List<Token> tokens, ParseTree parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            ParseTree child = parent.getChild(i);
            if (child instanceof TerminalNode) {
                TerminalNode node = (TerminalNode) child;
                tokens.add(node.getSymbol());
                //System.out.println(node.getSymbol());
                System.out.print(node.getText() + " ");
            } else {
                inOrderTraversal(tokens, child);
            }
        }
    }

    public static String temp = "";
    public static String struct = "";


    public static void inOrderTraversal(List<Token> tokens, ParseTree parent, String funStr) {
        Map<String, String> replaceAllDefineMap = CppTokenize.replaceAllDefineMap;
        Set<String> keys = replaceAllDefineMap.keySet();

        for (int i = 0; i < parent.getChildCount(); i++) {
            ParseTree child = parent.getChild(i);
            String label = "";
            if (child instanceof TerminalNode) {
                TerminalNode node = (TerminalNode) child;
                tokens.add(node.getSymbol());
                if (keys.contains(node.getText())){
                    label = replaceAllDefineMap.get(node.getText());
                }else {
                    label = node.getText();
                }

//                temp += node.getText() + " ";
                temp += label + " ";
                funStr += node.getText() + " ";
            } else {
                inOrderTraversal(tokens, child, funStr);
            }
        }
    }

    //抽出结构体和共用体
    public static void extractStructByInorderTraversal(List<Token> tokens, ParseTree tree, List<String> structs) {
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            String structString = new String();
            //抽出结构体和共用体
            if (child instanceof CPP14Parser.BlockDeclarationContext) {
                inOrderTraversal(tokens, child, structString);
//                System.out.println(temp);
                if (temp.matches("^(typedef struct).*") || temp.matches("^(typedef union).*")){
                    structs.add(temp);
//                    System.out.println(temp);
                }
                temp = "";
            }

            extractStructByInorderTraversal(tokens, child, structs);
        }
    }

    public static void extractFunctionByInorderTraversal(List<Token> tokens, ParseTree tree, List<String> funs) {
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            String funString = new String();

            if (child instanceof CPP14Parser.FunctionDefinitionContext) {
                inOrderTraversal(tokens, child, funString);
                funs.add(temp);
//                if (temp.matches(".*int & data.*")){
//                    System.out.println(temp);
//                }else {
//                    funs.add(temp);
//                }
//                System.out.println(temp);
                temp = "";
            }
            extractFunctionByInorderTraversal(tokens, child, funs);
        }
    }

    public static void extractStatementByInorderTraversal(List<Token> tokens, ParseTree tree, List<String> stats) {
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            String statStr = new String();

            if (child instanceof CPP14Parser.JumpStatementContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }
            if (child instanceof CPP14Parser.LabeledStatementContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }

            if (child instanceof CPP14Parser.SelectionStatementContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }

            if (child instanceof CPP14Parser.SimpleDeclarationContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }
            if (child instanceof CPP14Parser.IterationStatementContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }
            extractStatementByInorderTraversal(tokens, child, stats);
        }
    }

    public static void standardFunction(List<Token> tokens, ParseTree tree, List<String> stats) {
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            String statStr = new String();
            if (child instanceof CPP14Parser.SimpleDeclarationContext) {
                inOrderTraversal(tokens, child, statStr);
                stats.add(temp);
                temp = "";
            }
            extractStatementByInorderTraversal(tokens, child, stats);
        }
    }

}

