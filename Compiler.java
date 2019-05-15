// 2019-03-29

import Absyn.Program;
import BE.*;
import FE.*;
import IR.IRRoot;
import Parser.MxStarLexer;
import Parser.MxStarParser;
import Scope.TopScope;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.InputStream;
import java.io.OutputStream;

class Compiler {
    private InputStream is;
    private Program ast;
    private IRRoot ir;
    private TopScope global;

    Compiler(InputStream i){
        is = i;
    }

    void buildAST() throws Exception {
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
        global = s2.topScope;
    }

    void buildIR() {
        IRBuilder irb = new IRBuilder(global);
        irb.visit(ast);
        ir = irb.getRoot();
    }

    void printIR(OutputStream os) {
        IRPrinter irp = new IRPrinter(os);
        irp.visit(ir);
    }

    void testIR(InputStream is, Boolean SSA) throws Exception {
        IRInterpreter iri = new IRInterpreter(is, SSA);
        iri.run();
        System.out.print(String.format("ExitCode: %d.", iri.getExitCode()));
    }

    void preTransform() {
        PreTransformer pt = new PreTransformer(ir);
        pt.run();
    }

    void constPropagate() {
        ConstProper sp = new ConstProper();
        sp.visit(ir);
    }

    void eliminate(int x) {
        Eliminator e = new Eliminator(ir);
        if (x == 0) {
            e.run();
        }
        else {
            e.run2();
        }
    }

    void allocate(int x) {
        if (x == 0) {
            NaiveAllocator na = new NaiveAllocator(ir);
            na.run();
            ir.pr1 = NASMRegSet.R10;
            ir.pr2 = NASMRegSet.R11;
        }
        else {
            GraphAllocator ga = new GraphAllocator(ir);
            ga.run();
        }
    }

    void transform() {
        TargetTransformer tt = new TargetTransformer(ir);
        tt.run();
    }

    void generate(OutputStream os) {
        CodePrinter cp = new CodePrinter(os);
        cp.visit(ir);
    }
}
