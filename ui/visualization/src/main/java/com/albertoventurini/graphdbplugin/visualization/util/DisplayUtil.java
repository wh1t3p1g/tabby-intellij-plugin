/**
 * Copied and adapted from plugin
 * <a href="https://github.com/neueda/jetbrains-plugin-graph-database-support">Graph Database Support</a>
 * by Neueda Technologies, Ltd.
 * Modified by Alberto Venturini, 2022
 */
package com.albertoventurini.graphdbplugin.visualization.util;

import com.albertoventurini.graphdbplugin.database.api.data.GraphEntity;
import com.albertoventurini.graphdbplugin.database.api.data.GraphNode;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

public class DisplayUtil {

    private static final int MAX_TOOLTIP_PROPERTIES = 3;
    private static final int LABEL_TEXT_WIDTH = 300;
    private static final int MAX_TITLE_LENGTH = 40;
    private static final int MAX_TEXT_LENGTH = 100;

    private static final List<String> TITLE_INDICATORS = unmodifiableList(newArrayList("name", "title", "NAME"));
    private static final Predicate<Map.Entry<String, Object>> IS_STRING_VALUE = o -> String.class.isAssignableFrom(o.getValue().getClass());

    public static String getProperty(GraphNode node) {
        Optional<String> backup = Optional.empty();
        Optional<String> fuzzyMatch = Optional.empty();
        for (Map.Entry<String, Object> entry : node.getPropertyContainer().getProperties().entrySet()) {
            Object valueObj = entry.getValue();
            if (valueObj instanceof String) {
                String key = entry.getKey();
                String value = (String) valueObj;

                for (String titleIndicator : TITLE_INDICATORS) {
                    if (titleIndicator.equals(key) && filterLength(value)) {
                        return value;
                    }

                    if (key.contains(titleIndicator) && !fuzzyMatch.isPresent()) {
                        fuzzyMatch = Optional.of(value)
                                .filter(DisplayUtil::filterLength);
                    }
                }

                if (!backup.isPresent()) {
                    backup = Optional.of(value)
                            .filter(DisplayUtil::filterLength);
                }
            }
        }

        return fuzzyMatch.orElse(backup.orElse(node.getId()));
    }

    public static String getType(GraphNode node) {
        return node.getTypes().size() > 0 ? node.getTypes().get(0) : "";
    }

    private static boolean filterLength(String title) {
        return title.length() < MAX_TITLE_LENGTH;
    }

    public static String getTooltipTitle(GraphEntity entity) {
        List<String> types = entity.getTypes();
        if(types != null){
            if(types.contains("Class")){
                return "[Class] " + entity.getPropertyContainer().getProperties().getOrDefault("NAME", "Null");
            }else if(types.contains("Method")){
                return "[Method] " + entity.getPropertyContainer().getProperties().getOrDefault("NAME", "Null");
            }else if(types.contains("HAS")){
                return "[Has]";
            }else if(types.contains("INTERFACES")){
                return "[INTERFACES]";
            }else if(types.contains("EXTENDS")){
                return "[EXTENDS]";
            }else if(types.contains("CALL")){
                return "[CALL]";
            }else if(types.contains("ALIAS")){
                return "[ALIAS]";
            }
        }
        return entity.getId() + ": " + entity.getTypes();
    }

    public static String getTooltipText(GraphEntity entity) {
        Map<String, Object> properties = entity.getPropertyContainer().getProperties();
        String format = "<tr><th>%s</th><td>%s</td></tr>";

        StringBuilder sb = new StringBuilder();

        List<String> types = entity.getTypes();
        if(types != null){
            List<String> displayFields = new ArrayList<>();
            if(types.contains("Class")){
                displayFields = Arrays.asList("NAME", "IS_SERIALIZABLE");
            }else if(types.contains("Method")){
                displayFields = Arrays.asList("CLASSNAME", "NAME", "SIGNATURE");
            }else if(types.contains("CALL")){
                displayFields = Arrays.asList("POLLUTED_POSITION","LINE_NUM");
            }

            for(String field:displayFields){
                if(properties.containsKey(field)){
                    String data = String.valueOf(properties.getOrDefault(field, "null"));
                    sb.append(String.format(format, field, StringEscapeUtils.escapeHtml4(truncate(data, MAX_TEXT_LENGTH))));
                }
            }
        }

        return "<html><head><style>\n" +
                "th, td {\n" +
                "  border-style: solid;\n" +
                "}\n" +
                "</style>\n" +
                "</head><body><table>" + sb + "</table></body></html>";
    }

    private static String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 1) + "..." : text;
    }
}
