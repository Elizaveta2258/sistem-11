import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите URL Музыки:");
        String urlMusic = scanner.nextLine();
        System.out.println("Введите URL Картинки:");
        String urlPicture = scanner.nextLine();

        Thread mp3Thread = new Thread(() -> downloadFile(urlMusic, "file.mp3"));
        Thread pictureThread = new Thread(() -> downloadFile(urlPicture, "picture.jpg"));

        mp3Thread.start();
        pictureThread.start();

        try {
            mp3Thread.join();
            pictureThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        openFiles();
    }
    public static void downloadFile(String url, String filePath) {
        try (InputStream is = new URL(url).openStream();
             OutputStream os = new FileOutputStream(filePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            System.out.println("Загрузка завершена: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла: " + filePath);
            e.printStackTrace();
        }
    }

    public static void openFiles() {
        try {
            Desktop.getDesktop().open(new File("file.mp3"));
            Desktop.getDesktop().open(new File("picture.jpg"));

            String pictureType = getFileType("picture.jpg");
            if (Objects.equals(pictureType, "JPEG")) {
                System.out.println("Тип файла совпадает: JPEG");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при открытии файлов.");
            e.printStackTrace();
        }
    }

    public static String getFileType(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] magicBytes = new byte[4];
            if (fis.read(magicBytes) != -1) {
                return determineFileType(magicBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Неопределенный тип";
    }

    public static String determineFileType(byte[] magicBytes) {
        if (magicBytes.length < 4) {
            return "Неопределенный тип";
        }

        if (magicBytes[0] == (byte) 0x49 && magicBytes[1] == (byte) 0x44 &&
                magicBytes[2] == (byte) 0x33) {
            return "MP3";
        } else if (magicBytes[0] == (byte) 0x89 && magicBytes[1] == (byte) 0x50 &&
                magicBytes[2] == (byte) 0x4E && magicBytes[3] == (byte) 0x47) {
            return "PNG";
        } else if (magicBytes[0] == (byte) 0xFF && magicBytes[1] == (byte) 0xD8) {
            return "JPEG";
        } else if (magicBytes[0] == (byte) 0x25 && magicBytes[1] == (byte) 0x50 &&
                magicBytes[2] == (byte) 0x44 && magicBytes[3] == (byte) 0x46) {
            return "PDF";
        }

        return "Неопределенный тип";
    }
}