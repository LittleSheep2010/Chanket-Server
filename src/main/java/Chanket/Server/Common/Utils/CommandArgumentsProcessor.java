package Chanket.Server.Common.Utils;

import java.util.regex.Pattern;

public class CommandArgumentsProcessor {

    public static final Pattern STRING_PROCESS_PATTERN = Pattern.compile("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

    public static String[] QuotedDoubleSpaces(String[] arguments) {
        return STRING_PROCESS_PATTERN.split(String.join(" ", arguments).replaceAll("^\"", ""));
    }

    public static String[] QuotedSingleSpaces(String[] arguments) {
        return STRING_PROCESS_PATTERN.split(String.join("", arguments).replaceAll("^'", ""));
    }
}
