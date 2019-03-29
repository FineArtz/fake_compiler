// 2019-03-29

import java.io.FileInputStream;
import java.io.InputStream;

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
        try {
            compiler.compile();
        }
        catch (Error e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
