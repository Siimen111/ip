import java.util.Scanner;

public class Revel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] storedItems = new String[100];
        int itemCount = 0;
        boolean exitLoop = false;
        String intro = """
                ____________________________________________________________
                 Hello! I'm Revel
                 What can I do for you?
                ____________________________________________________________
                """;
        String indent = "____________________________________________________________";
        System.out.println(intro);

        while (true) {
            String input = sc.nextLine();

            switch (input.split(" ")[0]) {
                case "bye":
                    System.out.println(indent + "\n" + " Bye. Hope to see you again soon!\n" + indent);
                    exitLoop = true;
                    break;
                case "list":
                    System.out.println(indent);
                    for (int i = 0; i < itemCount; i++) {
                        System.out.println(storedItems[i]);
                    }
                    System.out.println(indent);
                    continue;
                case "mark":

                default:
                    System.out.println(indent + "\n" + "added: " + input + "\n" + indent);
                    storedItems[itemCount] = ++itemCount + ": " + input;
            }

            if (exitLoop) {
                break;
            }

        }
    }
}
