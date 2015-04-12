package ch.unige.Twic.Twic;

/**
 * Created by thomas on 3/1/15.
 */
public class CodeName {
    private String code;
    private String name;
    private boolean synt;

    public CodeName(String code, String name, boolean synt) {
        this.code = code;
        this.name = name;
        this.synt = synt;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isSynt() {
        return synt;
    }
}
