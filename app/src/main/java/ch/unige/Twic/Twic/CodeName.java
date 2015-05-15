package ch.unige.Twic.Twic;

/**
 * This object store the information of a "language" node contained in the XML file "languagelist" available at the address: "http://latlapps.unige.ch/Twicff?act=ll"
 *
 * Fields available:
 * <ul>
 *     <li>code: ISO 639-1 code name for the current language</li>
 *     <li>name: Name of the language to be displayed</li>
 *     <li>synt: determine of a voice synthesizer is available for the current language</li>
 * </ul>
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
