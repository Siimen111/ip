import java.util.Scanner;

public class Revel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] storedItems = new String[100];
        int itemCount = 0;
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
            if (input.equals("bye")) {
                System.out.println(indent + "\n" + " Bye. Hope to see you again soon!\n" + indent);
                break;
            }
            if (input.equals("list")) {
                System.out.println(indent);
                for (int i = 0; i < itemCount; i++) {
                    System.out.println(storedItems[i]);
                }
                System.out.println(indent);
                continue;
            }

            System.out.println(indent + "\n" + "added: " + input + "\n" + indent);
            storedItems[itemCount] = ++itemCount + ": " + input;
        }
    }
}
