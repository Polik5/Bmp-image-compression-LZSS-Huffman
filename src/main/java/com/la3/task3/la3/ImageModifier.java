package com.la3.task3.la3;
// D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//01.bmp
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.util.Random;
public class ImageModifier {

//        for (int i = 0; i < graySecretImage.getHeight(); i++) {
//            for (int j = 0; j < graySecretImage.getWidth(); j++) {
//                // Получение значения пикселя скрытого изображения
//                int pixel = graySecretImage.getRGB(j, i)  &   0xFF;
//                // Замена выбранного количества наименее значащих бит пикселя контейнера, используя значение пикселя скрытого изображения
//                int containerPixel = containerImage.getRGB(j, i);
//                for (int k = 0; k < numBitsToReplace; k++) {
//                    int randomBit = new Random().nextInt(8); // Генерация случайного числа от 0 до 7 для выбора бита
//                    containerPixel = (containerPixel    & (1 << randomBit)) | ((pixel  &   (1 << k)) << randomBit);
//                }
//                containerImage.setRGB(j, i, containerPixel);
//            }
//        }

    public static void main(String[] args) {
        File containerFile = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//01.bmp");
        BufferedImage containerImage = null;
        try {
            containerImage = ImageIO.read(containerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File secretFile = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//03.bmp");
        BufferedImage secretImage = null;
        try {
            secretImage = ImageIO.read(secretFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (containerImage == null || secretImage == null) {
            System.out.println("Не удалось загрузить изображения.");
            return;
        }
        if (containerImage.getWidth() < secretImage.getWidth() ||
                containerImage.getHeight() < secretImage.getHeight()) {
            System.out.println("Размер скрытого изображения превышает размер контейнера.");
            return;
        }
        // преобразование скрытого изображения к оттенкам серого
        BufferedImage graySecretImage = new BufferedImage(secretImage.getWidth(), secretImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = graySecretImage.getGraphics();
        g.drawImage(secretImage, 0, 0, null);
        g.dispose();
        File outputfile = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//graySecretImage.bmp");
        try {
            ImageIO.write(graySecretImage, "bmp", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numBitsToReplace = 2; //  количество заменяемых бит
        if (numBitsToReplace < 1 || numBitsToReplace > 8) {
            System.out.println("Недопустимое количество заменяемых бит");
            return;
        }
        for (int i = 0; i < graySecretImage.getHeight(); i++) {
            for (int j = 0; j < graySecretImage.getWidth(); j++) {
                int pixel = graySecretImage.getRGB(j, i) & 0xFF;
                // замена наименее значащих бит пикселя контейнера
                int containerPixel = containerImage.getRGB(j, i);
                for (int k = 0; k < numBitsToReplace; k++) {
                    int randomBit = new Random().nextInt(8);
                    int containerBit = (containerPixel >> randomBit) & 1; // получение значения бита контейнера
                    int newContainerBit = (pixel >> k) & 1; // получение значения бита скрытого изображения
                    containerPixel = (containerPixel & ~(1 << randomBit)) | (newContainerBit << randomBit);
                }
                containerImage.setRGB(j, i, containerPixel);
            }
        }
        File outputContainerFile = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//container_with_secret.bmp");
        try {
            ImageIO.write(containerImage, "bmp", outputContainerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Скрытое изображение встроено в контейнер");
        try {
            File input = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//container_with_secret.bmp");
            BufferedImage image = ImageIO.read(input);
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage[] bitPlanes = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                bitPlanes[i] = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int rgb = image.getRGB(x, y);
                        int bit = (rgb >> i) & 1;
                        int newRGB = (bit * 255 << 16) | (bit * 255 << 8) | (bit * 255);
                        bitPlanes[i].setRGB(x, y, newRGB);
                    }
                }
                File output = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//bitplane//bit_plane_" + i + ".bmp");
                ImageIO.write(bitPlanes[i], "bmp", output);
            }
            System.out.println("Разложение на битовые срезы завершено");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
