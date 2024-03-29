import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean flag = true;
        while (flag) {
            System.out.println("Выберете действие:\n1. Записать новые данные\n2. Завершить работу приложения\n");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.nextLine()) {
                case "1":
                    System.out.println("""
                            Введите следующие данные в произвольном порядке через пробел:
                            1. ФИО (русские или латинские буквы);
                            2. дата рождения - строка формата dd.mm.yyyy;
                            3. номер телефона - целое беззнаковое число без форматирования (указывается код города для стационарного телефона и не указывается код страны);
                            4. пол - символ латиницей f или m.
                            """);
                    try {
                        writeToFile(checkData(inputData()));
                    } catch (IOException e) {
                        System.out.println("Ошибка записи данных");
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    System.out.println("Приложение завершило работу.");
                    flag = false;
                    break;
                default:
                    System.out.println("Такого пункта меню не существует, выберете из предложенных\n");
                    break;
            }
        }
    }

    public static String inputData() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static String checkData(String data) {

        String[] newData = data.split(" ");

        ArrayList<String> fio = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<String> phoneList = new ArrayList<>();
        ArrayList<String> genderList = new ArrayList<>();
        ArrayList<String> errorData = new ArrayList<>();
        String date = "";
        String phone = "";
        String gender = "";

        for (String newDatum : newData) {
            if (newDatum.equals("m") || newDatum.equals("f")) {
                genderList.add(newDatum);
                gender = newDatum;
            } else if (newDatum.contains(".")) {
                dateList.add(newDatum);
                date = newDatum;
            } else if (newDatum.matches("^[0-9]+$")) {
                phoneList.add(newDatum);
                phone = newDatum;
            } else if (newDatum.matches("^[A-Za-zА-Яа-яЁё]+$")) {
                fio.add(newDatum);
            } else {
                errorData.add(newDatum);
            }
        }

        if (!Validation.checkDataError(errorData)) throw new ErrorDataException();
        if (!Validation.checkDataCompleteness(fio, date, phone, gender)) throw new DataCompletenessException();
        if (!Validation.checkDataExcess(genderList, dateList, phoneList)) throw new DataExcessException();
        if (!Validation.checkPhone(phone)) throw new InvalidPhoneException();
        if (!Validation.checkDate(date)) throw new InvalidDateException();

        StringBuilder makeString = new StringBuilder();
        for (String s : fio) {
            makeString.append(s);
            makeString.append(" ");
        }
        return makeString + date + " " + phone + " " + gender + "\n";
    }

    public static void writeToFile(String data) throws IOException {
        String fileName = data.split(" ")[0];
        File newFile = new File(fileName);
        try (FileWriter writer = new FileWriter(newFile, true)) {
            writer.write(data);
            writer.flush();
        }
    }
}

class Validation {
    public static boolean checkDataCompleteness(ArrayList<String> fio, String date, String phone, String gender) {
        return !fio.isEmpty() && !date.isEmpty() && !phone.isEmpty() && !gender.isEmpty();
    }

    public static boolean checkDataExcess(ArrayList<String> gender, ArrayList<String> date, ArrayList<String> phone) {
        return gender.size() == 1 && date.size() == 1 && phone.size() == 1;
    }

    public static boolean checkDataError(ArrayList<String> errorData) {
        return errorData.isEmpty();
    }

    public static boolean checkPhone(String phone) {
        return phone.length() == 10;
    }

    public static boolean checkDate(String date) {
        if (date.matches("(0[1-9]|[12][0-9]|3[01])[.](0[1-9]|1[012])[.]((19|20)\\d\\d)")) {
            String day = date.substring(0, 2);
            String month = date.substring(3, 5);
            String year = date.substring(6, 10);
            if (day.equals("31") && (month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11"))) {
                return false;
            } else if (month.equals("02")) {
                if ((Integer.parseInt(year)) % 4 == 0) {
                    return !day.equals("30") && !day.equals("31");
                } else {
                    return !day.equals("29") && !day.equals("30") && !day.equals("31");
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}

class DataCompletenessException extends IllegalArgumentException {
    public DataCompletenessException() {
        super("Вы ввели недостаточно данных. Повторите ввод данных.");
    }
}

class DataExcessException extends IllegalArgumentException {
    public DataExcessException() {
        super("Вы ввели лишние данные. Вы можете ввести только один номер телефона, дату рождения и пол. Повторите ввод данных.");
    }
}

class ErrorDataException extends IllegalArgumentException {
    public ErrorDataException() {
        super("Некоторые данные не распознаны. Вы допустили ошибку в вводе данных. Проверьте правильность ввода данных.");
    }
}

class InvalidPhoneException extends IllegalArgumentException {
    public InvalidPhoneException() {
        super("Вы ввели некорректный номер телефона. Номер должен содержать 10 цифр и не содержать пробелов и других символов");
    }
}

class InvalidDateException extends IllegalArgumentException {
    public InvalidDateException() {
        super("Вы ввели некорректную дату. Введите дату в формате dd.mm.yyyy. Убедитесь, что дата не содержить недопустимых значений дня, месяца, года");
    }
}
