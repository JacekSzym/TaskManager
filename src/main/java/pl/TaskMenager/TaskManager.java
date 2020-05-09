package pl.TaskMenager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class TaskManager {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String line ="";
        String dbDirectory = "db.csv";


        try {
            String[][] dbTable = readFile(dbDirectory);
            System.out.println(ConsoleColors.BLUE + "Witaj w programie TaskMenager" + ConsoleColors.RESET);

            while (!line.equals("quit")  ) {

                System.out.println(ConsoleColors.BLUE + "Wybierz komendę:" +
                        "\n" + ConsoleColors.WHITE + "list" + ConsoleColors.BLUE + "  lista zadań" +
                        "\n" + ConsoleColors.WHITE + "add" + ConsoleColors.BLUE + "  dodaj nowe zadanie" +
                        "\n" + ConsoleColors.WHITE + "remove" + ConsoleColors.BLUE + "  usuń zadanie" +
                        "\n" + ConsoleColors.WHITE + "quit" + ConsoleColors.BLUE + "  zapis i wyjdź" +
                        ConsoleColors.RESET);

                line = scanner.nextLine();

                switch (line) {
                    case "list":
                        listTask(dbTable);
                        break;
                    case "add":
                        dbTable = addTask(dbTable);
                        break;
                    case "remove":
                        dbTable = removeTask(dbTable);
                        break;
                }
            }
                saveTasks(dbTable, dbDirectory);
                System.out.println(ConsoleColors.RED + "Do zobaczenia" + ConsoleColors.RESET);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(ConsoleColors.BLUE + "NIE UDAŁO SIE UTWORZYC PLIKU" + ConsoleColors.RESET);
        } catch (ParseException e) {
            System.out.println("Błędny fomrat daty");
        }
    }

    public static String[][] readFile(String fileDirectory) throws IOException {

        Path path = Paths.get(fileDirectory);
        String[][] dbTable = new String[0][3];

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        File file = new File(fileDirectory);

        try (Scanner scanner = new Scanner(file)) {

            int i = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tableRow = line.split(";");

                dbTable = Arrays.copyOf(dbTable, dbTable.length + 1);
                dbTable[i] = tableRow;

                i += 1;
            }
        } catch (FileNotFoundException e) {
            System.out.println(ConsoleColors.BLUE + "NIE UDAŁO SIE ZNALEŹĆ PLIKU" + ConsoleColors.RESET);
        }
        return dbTable;
    }

    public static String[][] addTask(String[][] dbTable) throws ParseException {

        Scanner scanner = new Scanner(System.in);
        String date = "";
        String discription;
        String importance = "";
        String[] tableRow = new String[3];

        // 1. Podajemy datę.
        System.out.println(ConsoleColors.BLUE +
                "Podaj datę zadania w formacie DD-MM-YYYY: "
                + ConsoleColors.RESET);

        while (checkDate(date) != true) {
            date = scanner.nextLine();

            if (checkDate(date) != true) {
                System.out.println(ConsoleColors.RED +
                        "Błędny format format daty. Wpisz datę w formacie DD-MM-YYYY"
                        + ConsoleColors.RESET);
            }
        }


        //2. Podajemy opis zadania
        System.out.println(ConsoleColors.BLUE +
                "Podaj opis zadania: "
                + ConsoleColors.RESET);

        discription = scanner.nextLine().replace(";", ",");

        //3. Podajemy priorytet zadania
        System.out.println(ConsoleColors.BLUE +
                "Podaj priorytet zadania wg skali: " + "\n" +
                ConsoleColors.RED + "1 - Zadanie HIPER ważne" + "\n" +
                ConsoleColors.YELLOW + "2 - Zadanie ważne" + "\n" +
                ConsoleColors.GREEN + "3 - Zadanie mało ważne"
                + ConsoleColors.RESET);

        while (!(importance.equals("1") || importance.equals("2") || importance.equals("3"))) {
            importance = scanner.nextLine();

            if (!(importance.equals("1") || importance.equals("2") || importance.equals("3")) == true) {
                System.out.println(ConsoleColors.BLUE + "Błędny priorytet ! " +
                        "Podaj priorytet zadania wg skali: " + "\n" +
                        ConsoleColors.RED + "1 - Zadanie HIPER ważne" + "\n" +
                        ConsoleColors.YELLOW + "2 - Zadanie ważne" + "\n" +
                        ConsoleColors.GREEN + "3 - Zadanie mało ważne"
                        + ConsoleColors.RESET);
            }
        }

        //4. Budowanie wiersza:

        tableRow[0] = discription;
        tableRow[1] = date;
        tableRow[2] = importance;

        //5. Dodawanie do bazy:

        dbTable = Arrays.copyOf(dbTable, dbTable.length + 1);
        dbTable[dbTable.length - 1] = tableRow;

        return dbTable;
    }

    public static String[][] removeTask(String[][] dbTable) {

        Scanner scanner = new Scanner(System.in);
        String[][] newDbTable = new String[dbTable.length - 1][dbTable[0].length];
        int taskNo;

        // 0. Drukowanie listy:

        System.out.println(ConsoleColors.BLUE + "Twoje zadania: ");
        listTask(dbTable);

        // 1. Podanie indkesu zadania

        System.out.println(ConsoleColors.BLUE + "Podaj numer zadania do usunięcia (od 0 do " +
                (dbTable.length - 1) + ")"
                + ConsoleColors.RESET);

        while (true) {
            if (scanner.hasNextInt()) {
                taskNo = scanner.nextInt();
                if (taskNo >= 0 && taskNo < dbTable.length) {
                    break;
                } else {
                    System.out.println(ConsoleColors.BLUE +
                            "Błędny numer zadania. Podaj numer zadania od 0 do " +
                            (dbTable.length - 1) +
                            ConsoleColors.RESET);
                }
            }
        }

        // 2. Budowanie nowej tablicy
        int j = 0;
        for (int i = 0; i < dbTable.length; i++) {

            if (i != taskNo) {
                newDbTable[j] = dbTable[i];
                j += 1;
            }
        }
        return newDbTable;
    }

    public static void listTask(String[][] dbTable) {

        StringBuilder listRow = new StringBuilder();

        for (int j = 0; j < dbTable.length; j++) {

            for (int i = 0; i < dbTable[j].length - 1; i++) {
                if (i == 0) {
                    listRow.append(ConsoleColors.BLUE + j + ": ");
                }
                if (dbTable[j][2].equals("1")) {
                    listRow.append(ConsoleColors.RED + dbTable[j][i] + " " + ConsoleColors.RESET);
                } else if (dbTable[j][2].equals("2")) {
                    listRow.append(ConsoleColors.YELLOW + dbTable[j][i] + " " + ConsoleColors.RESET);
                } else if (dbTable[j][2].equals("3")) {
                    listRow.append(ConsoleColors.GREEN + dbTable[j][i] + " " + ConsoleColors.RESET);
                }
            }
            listRow.append("\n");
        }
        System.out.println(listRow);
    }

    public static void saveTasks(String[][] dbTable, String fileDirectory) {

        Path path = Paths.get(fileDirectory);
        File file = new File(fileDirectory);

        try (FileWriter fileWriter = new FileWriter(file)) {

            for (int i = 0; i < dbTable.length; i++) {
                for (int j = 0; j < dbTable[i].length; j++) {
                    fileWriter.append(dbTable[i][j]);
                    if (j < dbTable[i].length - 1) {
                        fileWriter.append(";");
                    }
                }
                if (i < dbTable.length) {
                    fileWriter.append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED + "Nie udało się zapusac pliku" + ConsoleColors.RESET);
        }
    }

    public static boolean checkDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setLenient(false);

        try {
            format.parse(date.trim());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
