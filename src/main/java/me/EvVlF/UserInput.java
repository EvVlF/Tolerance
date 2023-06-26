package me.EvVlF;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInput {
    private static Scanner scanner = new Scanner(System.in);

    private String userInput;

    private final Pattern pattern = Pattern.compile("^(?<size>(500|[1-9]|[1-9][0-9]|[1-4][0-9]{2})((\\.|,)\\d{0,7})?)(?<quality>(?i)[bcdefghkmnprsu]|(js)|(JS)|(jt)|(JT))(?<deviation>[5-9]|1[0-7])$");

    private static final class UserInputSingletonHolder {
        private static final UserInput USER_INPUT_INSTANCE = new UserInput();
    }

    private UserInput() {
    }

    static UserInput getInstance() {
        return UserInputSingletonHolder.USER_INPUT_INSTANCE;
    }

    private Pattern getPattern() {
        return pattern;
    }

    Matcher matcherText_ESDiP_Range() {
        return getPattern().matcher(getUserInput());
    }

    private static void printMainMessage() {
        System.out.println("Введите данные в формате виде 12h7 и нажмите Enter.\nВ программе представлены не все поля допусков.\nДля выхода введите q и нажмите Enter");
    }

    private void setUserInput() {
        this.userInput = scanner.nextLine();
    }

    String getUserInput() {
        return this.userInput;
    }

    void interactWithUser() {
        do {
            printMainMessage();
            setUserInput();
            exitOrContinueProgram();
            checkUserInput();
        } while (!(checkUserInput()));
    }

    private boolean checkUserInput() {
        if (!matcherText_ESDiP_Range().matches()) {
            System.out.println("Данные допуска не представлены в программе или некорректный ввод");
        }
        return matcherText_ESDiP_Range().matches();
    }

    boolean isExitProgram() {
        return "q".equals(userInput);
    }

    void exitOrContinueProgram() {
        if (isExitProgram()) {
            System.exit(0);
        }
    }

}
