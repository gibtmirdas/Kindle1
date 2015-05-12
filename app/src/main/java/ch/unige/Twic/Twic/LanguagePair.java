package ch.unige.Twic.Twic;

/**
 * Created by thomas on 3/1/15.
 */
public class LanguagePair {

    private String src;
    private String tgt;
    private boolean trad;

    public LanguagePair(String src, String tgt, boolean trad) {
        this.src = src;
        this.tgt = tgt;
        this.trad = trad;
    }

    public String getSrc() {
        return src;
    }

    public String getTgt() {
        return tgt;
    }

    public boolean isTrad() {
        return trad;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof LanguagePair))
            return false;
        LanguagePair remote = (LanguagePair) o;

        return remote.getSrc().equals(this.src) && remote.getTgt().equals(this.tgt);
    }
}
