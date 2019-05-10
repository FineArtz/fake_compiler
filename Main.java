// 2019-03-29

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception{
        boolean inFile = false;
        String fileName = null;
        boolean printIR = false;
        boolean nasmFile = false;
        String nasmName = null;

        for (int i = 0; i < args.length; ++i) {
            switch (args[i]){
                case "-f": case "--file":
                    if (i < args.length - 1) {
                        inFile = true;
                        fileName = args[i + 1];
                    }
                    break;
                case "--print-nasm":
                    nasmFile = true;
                    nasmName = "src/out.asm";
                    break;
                case "--print-ir":
                    printIR = true;
                    break;
            }
        }

        InputStream is;
        if (inFile) {
            is = new FileInputStream(fileName);
        }
        else {
            is = System.in;
        }

        OutputStream os;
        //nasmFile = true;
        //nasmName = "src/out.asm";
        if (nasmFile) {
            os = new FileOutputStream(nasmName);
        }
        else {
            os = System.out;
        }

        Compiler compiler = new Compiler(is);
        if (is instanceof FileInputStream) {
            is.close();
        }
        compiler.buildAST();
        compiler.buildIR();
        compiler.preTransform();
        compiler.eliminate(0);
        if (printIR) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compiler.printIR(baos);
            System.out.println(baos);
        }
        //ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        //compiler.testIR(bais, false);
        compiler.allocate(1);
        compiler.transform();
        compiler.eliminate(1);
        compiler.generate(os);
        if (os instanceof FileOutputStream) {
            os.close();
        }
        /*try {
            compiler.compile();
        }
        catch (Error e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }*/
    }
}
