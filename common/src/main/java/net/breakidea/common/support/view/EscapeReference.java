package net.breakidea.common.support.view;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

/**
 * @author apple
 *
 * HTML转义输出
 */
public class EscapeReference implements ReferenceInsertionEventHandler {

    private static String PAGE_CONTROL = Viewport.CONTEXT_NAME + ".";

    private static String BLANK = new String();

    private static String[] escapeVers = new String[] { "util.", PAGE_CONTROL, "lang.", "raw_", "_content" };

    /**
     * @param ref
     * @return
     */
    private static String getVariable( String ref ) {
        ref = ref.replace("$", BLANK);
        ref = ref.replace("!", BLANK);
        ref = ref.replace("{", BLANK);
        ref = ref.replace("}", BLANK);
        return ref;
    }

    @Override
    public Object referenceInsert( String reference, Object value ) {
        reference = getVariable(reference);
        for (int i = 0; i < escapeVers.length; i++) {
            String item = escapeVers[i];
            if (reference.startsWith(item) || reference.endsWith(item)) {
                return (value == null ? BLANK : value);
            }
        }
        if (null == value) {
            return null;
        } else {
            if (value instanceof String) {
                String source = value.toString();
                source = source.replaceAll("\\&", "&amp;");
                source = source.replaceAll("\\\"", "&quot;");
                source = source.replaceAll("\\<", "&lt;");
                return source.replaceAll("\\>", "&gt;");
            }
            return value;
        }
    }
}
