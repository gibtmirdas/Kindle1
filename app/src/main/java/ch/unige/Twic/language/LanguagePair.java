package ch.unige.Twic.language;

/**
 * This object store the information of a "pair" node contained in the XML file "languagelist" available at the address: "http://latlapps.unige.ch/Twicff?act=ll".
 * Information about language are is ISO 639-1
 *
 * Fields available:
 * <ul>
 *     <li>src: code for the source language</li>
 *     <li>tgt: code for the target language</li>
 *     <li>trad: Determine if the translation is available from the source language to the destination one</li>
 * </ul>
 */
public class LanguagePair {

    private String src;
    private String tgt;

    public LanguagePair(String src, String tgt) {
        this.src = src;
        this.tgt = tgt;
    }

    public String getSrc() {
        return src;
    }

    public String getTgt() {
        return tgt;
    }

    /**
     * Compare if two languagePair are equivalent (same source and destination)
     * @param o language to compare this the current one
     * @return if they are equivalent
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof LanguagePair))
            return false;
        LanguagePair remote = (LanguagePair) o;

        return remote.getSrc().equals(this.src) && remote.getTgt().equals(this.tgt);
    }
}
