import java.util.stream.Collectors;
import java.util.Arrays;

public enum Command {
    BYE("bye", "exit"),
    LIST("list"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    HELP("help");

    private final String[] aliases;

    Command(String... aliases) {
        this.aliases = aliases;
    }

    static Command parse(String token) throws RevelException {
        String t = token.toLowerCase();
        for (Command c : values()) {
            for (String a : c.aliases) {
                if (a.equals(t)) {
                    return c;
                }
            }
        }

        throw new RevelException("Sorry! I am unable to assist you with that.\n" +
                "Type 'help' for a list of commands available to you.");
    }

    static String helpText() {
        return Arrays.stream(values())
                .map(c -> c.aliases[0])
                .collect(Collectors.joining(", "));
    }
}
