// 2019-03-22

package Symbol;

public class Symbol{
    public String name;
    public static java.util.Dictionary dict = new java.util.Hashtable();

    public Symbol(String n){
        name = n;
    }

    @Override
    public String toString(){
        return name;
    }

    public static Symbol symbol(String n){
        String u = n.intern();
        Symbol s = (Symbol)dict.get(u);
        if (s == null){
            s = new Symbol(u);
            dict.put(u, s);
        }
        return s;
    }
}
