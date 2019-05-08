// 2019-03-29

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception{
        boolean inFile = false;
        String fileName = null;

        for (int i = 0; i < args.length; ++i){
            switch (args[i]){
                case "-f": case "--file":
                    if (i < args.length - 1){
                        inFile = true;
                        fileName = args[i + 1];
                    }
                    break;
            }
        }

        InputStream is;
        if (inFile)
            is = new FileInputStream(fileName);
        else
            is = System.in;

        Compiler compiler = new Compiler(is);
        compiler.buildAST();
        compiler.buildIR();
        compiler.eliminate(0);
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //compiler.printIR(baos);
        //System.out.println(baos);
        //ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        //compiler.testIR(bais, false);
        compiler.allocate();
        compiler.transform();
        compiler.eliminate(1);
        //FileOutputStream fos = new FileOutputStream("src/out.asm");
        //compiler.generate(fos);
        //fos.close();
        compiler.generate(System.out);
        /*try {
            compiler.compile();
        }
        catch (Error e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }*/
    }
}
