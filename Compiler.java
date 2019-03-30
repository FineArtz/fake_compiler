// 2019-03-29

import Absyn.Program;
import FE.ASTBuilder;
import FE.Scanner1;
import FE.Scanner2;
import FE.SomeErrorListener;
import Parser.MxStarLexer;
import Parser.MxStarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.InputStream;

public class Compiler {
    private InputStream is;
    private Program ast;

    public Compiler(InputStream i){
        is = i;
    }

    public void compile() throws Exception{
        CharStream input = CharStreams.fromStream(is);
        MxStarLexer lexer = new MxStarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MxStarParser parser = new MxStarParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new SomeErrorListener());
        ParseTree tree = parser.program();
        ASTBuilder astb = new ASTBuilder();
        ast = (Program)astb.visit(tree);

        Scanner1 s1 = new Scanner1();
        Scanner2 s2 = new Scanner2();
        s1.visit(ast);
        s2.topScope = s1.topScope;
        s2.visit(ast);
    }
}
