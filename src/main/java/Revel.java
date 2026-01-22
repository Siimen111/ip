import java.util.Scanner;

public class Revel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
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

            System.out.println(indent + "\n" + input + "\n" + indent);
        }
    }
}
