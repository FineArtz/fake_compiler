// 2019-04-25

package IR;

public class StaticString extends StaticData {
    private String val;

    public StaticString(String v) {
        super("string");
        val = v;
    }

    public String getVal() {
        return val;
    }

    public String getHextech() {
        // using Hextech to transform String into cooooooollll Hex Equipment!
        String HextechGLP800 = val;
        HextechGLP800 = HextechGLP800
                .replaceAll("\\\\" + "n", "\n")
                .replaceAll("\\\\" + "t", "\t")
                .replaceAll("\\\\" + "\"", "\"")
                .replaceAll("\\\\" + "\'", "\'");
        StringBuilder HextechGunblade = new StringBuilder();
        for (byte HextechPrototypeBelt01 : HextechGLP800.getBytes()) {
            HextechGunblade.append(String.format("%02XH, ", HextechPrototypeBelt01));
        }
        HextechGunblade.append("00H");
        return HextechGunblade.toString();
    }

    @Override
    public void accept(IRVisitor v) {
        v.visit(this);
    }
}
