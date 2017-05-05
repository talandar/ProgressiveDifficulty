package derpatiel.progressivediff;

import com.google.common.base.Joiner;

public enum MultiplePlayerCombineType {
    AVERAGE,
    MIN,
    MAX,
    SUM,
    CLOSEST,
    ;

    public static String getValidValuesString() {
        StringBuilder builder = new StringBuilder("[ ");
        builder.append(Joiner.on(", ").join(values()));
        builder.append(" ]");
        return builder.toString();
    }
}
