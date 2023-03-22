package src.cpp.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.Tree;
import org.junit.Test;

import src.construct_dataset.TwoTimes;
import src.cpp.CPP14Lexer;
import src.cpp.CPP14Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CppTokenize {

    static Map<String, String> replaceAllDefineMap;

    public static String readWSADATA() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("D:\\two\\WSADATA.txt"));
        String line = "";
        String wadata = "";
        while ((line = br.readLine()) != null) {
            wadata += line;
        }
        return wadata;
    }

    public static String extractFun(String sourcePath) throws Exception {
//    public static String getParseStringByFunction(StringReader stringReader) throws Exception {

//        InputStream is = new FileInputStream(String.valueOf(stringReader));
        //替换C文件中的 define语句
        String replaceAllDefine = TwoTimes.replaceAllDefine(sourcePath);
        replaceAllDefineMap = TwoTimes.replaceAllDefineMap(sourcePath);

        StringReader stringReader = new StringReader(replaceAllDefine);

        InputStream is = new FileInputStream(sourcePath);

        ANTLRInputStream input = new ANTLRInputStream(stringReader);
//        ANTLRInputStream input = new ANTLRInputStream(stringReader);

        CPP14Lexer lexer = new CPP14Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        tokens.fill();
        CPP14Parser parser = new CPP14Parser(tokens);

        ParseTree tree = parser.translationUnit();

        /* Extract Function Tokens */
        ParseTreeWalker walker = new ParseTreeWalker();
        StringBuilder contractTokens = new StringBuilder();
        walker.walk(new CppAllWalker(contractTokens), tree);
        CppAllWalker cppWalker = new CppAllWalker(contractTokens);

        List<Token> tokens_funs = new ArrayList<>();
        List<Token> tokens_struct = new ArrayList<>();
        List<Token> tokens_statement = new ArrayList<>();
        List<String> funs = new ArrayList<>();
        List<String> structs = new ArrayList<>();
        List<String> statements = new ArrayList<>();

        cppWalker.extractFunctionByInorderTraversal(tokens_funs, tree, funs);

//        cppWalker.extractStructByInorderTraversal(tokens_struct, tree, structs);
        cppWalker.extractStatementByInorderTraversal(tokens_statement,tree,statements);

        /**
         * structs  C文件中的所有结构体和共用体
         * funs   C文件中的所有函数
         */
        String result = "";
        for (String fun : funs) {
            Set<String> keys = replaceAllDefineMap.keySet();
            String[] s = fun.split(" ");
            for (String s1 : s) {
                if (keys.contains(s1)) {
                    fun = fun.replaceAll(s1, replaceAllDefineMap.get(s1));
                }
            }
            System.out.println(fun);

            result += fun + "\n";
        }
        return result;

    }

//    public static String getParseStructByFunction(String sourcePath) throws Exception {
////    public static String getParseStringByFunction(StringReader stringReader) throws Exception {
//
////        InputStream is = new FileInputStream(String.valueOf(stringReader));
//        //替换C文件中的 define语句
//        String replaceAllDefine = TwoTimes.replaceAllDefine(sourcePath);
//
//        StringReader stringReader = new StringReader(replaceAllDefine);
//
////        InputStream is = new FileInputStream(sourcePath);
//
////        ANTLRInputStream input = new ANTLRInputStream(is);
//        ANTLRInputStream input = new ANTLRInputStream(stringReader);
//
//        CPP14Lexer lexer = new CPP14Lexer(input);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//        tokens.fill();
//        CPP14Parser parser = new CPP14Parser(tokens);
//        ParseTree tree = parser.translationUnit();
//
//        /* Extract Function Tokens */
//        ParseTreeWalker walker = new ParseTreeWalker();
//        StringBuilder contractTokens = new StringBuilder();
//        walker.walk(new CppAllWalker(contractTokens), tree);
//        CppAllWalker cppWalker = new CppAllWalker(contractTokens);
//        List<Token> tokens_funs = new ArrayList<>();
//        List<Token> tokens_struct = new ArrayList<>();
//        List<String> funs = new ArrayList<>();
//        List<String> structs = new ArrayList<>();
//        cppWalker.extractFunctionByInorderTraversal(tokens_funs, tree, funs);
//        cppWalker.extractStructByInorderTraversal(tokens_struct, tree, structs);
//
//        /**
//         * structs  C文件中的所有结构体和共用体
//         * funs   C文件中的所有函数
//         */
//        String structList = "";
//
//        for (String struct : structs) {
//            structList += struct + "\n";
////            System.out.println(struct);
//        }
//
//        return structList;
//
//    }

    public static String extractStatements(String sourcePath) throws Exception {

        //input  string
        /*StringReader stringReader = new StringReader(sourcePath);
        ANTLRInputStream input = new ANTLRInputStream(stringReader);*/

        //input  path
        InputStream is = new FileInputStream(sourcePath);
        ANTLRInputStream input = new ANTLRInputStream(is);


        CPP14Lexer lexer = new CPP14Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        tokens.fill();
        CPP14Parser parser = new CPP14Parser(tokens);
        ParseTree tree = parser.translationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        StringBuilder contractTokens = new StringBuilder();
        walker.walk(new CppAllWalker(contractTokens), tree);
        CppAllWalker cppWalker = new CppAllWalker(contractTokens);
        List<Token> tokens_statement = new ArrayList<>();
        List<String> statements = new ArrayList<>();

        cppWalker.extractStatementByInorderTraversal(tokens_statement,tree,statements);

        String stats = "";

        for (String statement : statements) {
//            stats += "#"+statement;
            stats += statement + "\n";
//            System.out.println(statement);
        }

        return stats;
    }


}
