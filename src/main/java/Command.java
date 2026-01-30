import java.util.stream.Collectors;
import java.util.Arrays;

public enum Command {
    HELLO("hello", "hi"),
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

    String[] getAliases() {
        return this.aliases;
    }

    static String helpText() {
        return Arrays.stream(values())
                .flatMap(c -> Arrays.stream(c.aliases))
                .distinct()
                .collect(Collectors.joining(", "));
    }
}
