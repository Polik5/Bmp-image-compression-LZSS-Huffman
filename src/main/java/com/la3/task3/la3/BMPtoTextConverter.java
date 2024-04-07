package com.la3.task3.la3;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

class Node
{
    Character ch; // узел
    Integer freq; // частота
    Node left = null, right = null;
    Node(Character ch, Integer freq)
    {
        this.ch = ch;
        this.freq = freq;
    }
    public Node(Character ch, Integer freq, Node left, Node right)
    {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
}
class LZSS {
    public static String encodeText(String text) {
        StringBuilder encodedText = new StringBuilder();
        int windowSize = 5;
        int lookAheadBufferSize = 3;
        int i = 0;
        while (i < text.length()) {
            int j = Math.max(i - windowSize, 0);
            int len = 0;
            int pos = -1;
            while (j < i) {
                int k = 0;
                while (i + k < text.length() && text.charAt(j + k) == text.charAt(i + k)) {
                    k++;
                    if (k == lookAheadBufferSize) {
                        break;
                    }
                }
                if (k > len) {
                    len = k;
                    pos = j;
                }
                j++;
            }
            if (len > 0) {
                encodedText.append("<").append(i - pos - 1).append(",").append(len).append(">");
                i += len;
            } else {
                encodedText.append(text.charAt(i));
                i++;
            }
        }
        return encodedText.toString();
    }
    public static String decodeText(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        int i = 0;
        while (i < encodedText.length()) {
            if (encodedText.charAt(i) == '<') {
                int commaIndex = encodedText.indexOf(",", i);
                int offset = Integer.parseInt(encodedText.substring(i + 1, commaIndex));
                int endIndex = encodedText.indexOf(">", commaIndex);
                int length = Integer.parseInt(encodedText.substring(commaIndex + 1, endIndex));
                int startPos = decodedText.length() - offset - 1;
                for (int j = 0; j < length; j++) {
                    decodedText.append(decodedText.charAt(startPos + j));
                }
                i = endIndex + 1;
            } else {
                decodedText.append(encodedText.charAt(i));
                i++;
            }
        }
        return decodedText.toString();
    }
}
class Main {
    public static void encode(Node root, String str, Map<Character, String> huffmanCode) {
        if (root == null) {
            return;
        }
        if (isLeaf(root)) {
            huffmanCode.put(root.ch, str.length() > 0 ? str : "1");
        }
        encode(root.left, str + '0', huffmanCode);
        encode(root.right, str + '1', huffmanCode);
    }

    public static boolean isLeaf(Node root) {
        return root.left == null && root.right == null;
    }

    public static void buildHuffmanTree(String text, String encodedFile) throws IOException {
        if (text == null || text.length() == 0) {
            return;
        }
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        PriorityQueue<Node> pq;
        pq = new PriorityQueue<>(Comparator.comparingInt(l -> l.freq));
        for (var entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (pq.size() != 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.freq + right.freq;
            pq.add(new Node(null, sum, left, right));
        }
        Node root = pq.peek();
        Map<Character, String> huffmanCode = new HashMap<>();
        encode(root, "", huffmanCode);
        System.out.println("Коды Хаффмана: " + huffmanCode);
        File encodedFileObj = new File(encodedFile);
        FileWriter encodedFileWriter = new FileWriter(encodedFileObj);
        for (Map.Entry<Character, String> entry : huffmanCode.entrySet()) {
            encodedFileWriter.write(entry.getKey() + "&" + entry.getValue() + System.lineSeparator());
        }
        encodedFileWriter.write("EOF" + System.lineSeparator());
        encodedFileWriter.close();
        FileWriter encodedTextWriter = new FileWriter(encodedFileObj, true);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(huffmanCode.get(c));
        }
        encodedTextWriter.write(sb.toString());
        encodedTextWriter.close();
    }
    public static void decodeFromFile(String encodedFilePath, String decodedFilePath, String dictionaryFilePath) {
        Map<Character, String> huffmanCode = new HashMap<>();
        //StringBuilder encodedString = new StringBuilder();
        boolean isCodeSection = true;
        // Считываем словарь из другого файла
        try (BufferedReader dictionaryReader = new BufferedReader(new FileReader(dictionaryFilePath))) {
            String dictionaryLine;
            while ((dictionaryLine = dictionaryReader.readLine()) != null) {
                if (dictionaryLine.equals("EOF")) {
                    isCodeSection = false;
                    continue;
                }
                if (isCodeSection) {
                    String[] dictionaryParts = dictionaryLine.split("&");
                    huffmanCode.put(dictionaryParts[0].charAt(0), dictionaryParts[1]);
                } else {
                    //encodedString.append(dictionaryLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder encodedString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(encodedFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                encodedString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String decodedString = decode(encodedString.toString(), huffmanCode);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(decodedFilePath))) {
            writer.write(decodedString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String decode(String encodedString, Map<Character, String> huffmanCode) {
        StringBuilder decodedString = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        for (char c : encodedString.toCharArray()) {
            currentCode.append(c);
            for (Map.Entry<Character, String> entry : huffmanCode.entrySet()) {
                if (entry.getValue().equals(currentCode.toString())) {
                    decodedString.append(entry.getKey());
                    currentCode.setLength(0);
                    break;
                }
            }
        }
        return decodedString.toString();
    }


}
public class BMPtoTextConverter {

    public static void main(String[] args) {
        try {

//            File inputFile = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//1.bmp");
//            BufferedImage image = ImageIO.read(inputFile);
//            FileWriter outputFile = new FileWriter("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//2.txt");
//
//            for (int y = 0; y < image.getHeight(); y++) {
//                for (int x = 0; x < image.getWidth(); x++) {
//                    int rgb = image.getRGB(x, y);
//                    int red = (rgb >> 16) & 0xFF;
//                    int green = (rgb >> 8) & 0xFF;
//                    int blue = (rgb & 0xFF);
//
//                    String pixelInfo = String.format("(%d, %d, %d) ", red, green, blue);
//                    outputFile.write(pixelInfo);
//                }
//                //outputFile.write("\n");
//            }
//            outputFile.close();

            // Запись изображения в текстовый файл
            BufferedImage image = ImageIO.read(new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//1.bmp"));
            File file = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//2.txt");
            PrintWriter writer0 = new PrintWriter(file);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = (rgb & 0xFF);
                    String pixelInfo = String.format("%d, %d, %d", red, green, blue);
                    writer0.print(pixelInfo);
                }
            }
            writer0.close();
            //System.out.println("BMP изображение успешно преобразовано в текстовый файл.");
            System.out.print("Нажмите 1, чтобы выполнить кодирование по алгоритму Хаффмена: ");
            Scanner write = new Scanner(System.in);
            int a = write.nextInt();
            System.out.println();
            if(a == 1){
                File inputFile2 = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//2.txt");
                Scanner scanner = new Scanner(inputFile2);
                StringBuilder text = new StringBuilder();
                while (scanner.hasNextLine()) {
                    text.append(scanner.nextLine());
                }
                Map<Character, Integer> freq = new HashMap<>();
                int totalChars = 0;
                for (char c : text.toString().toCharArray()) {
                    freq.put(c, freq.getOrDefault(c, 0) + 1);
                    totalChars++;
                }
                System.out.println("Вероятности символов:");
                for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
                    char c = entry.getKey();
                    int count = entry.getValue();
                    double probability = (double) count / totalChars;
                    System.out.println(c + ": " + probability);
                }
                scanner.close();
                Main.buildHuffmanTree(text.toString(), "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//encoded.txt");
                System.out.println("Файл успешно закодирован по алгоритму Хаффмана!");
            }
            System.out.print("Нажмите 2, чтобы выполнить кодирование по алгоритму LZSS: ");
            a = write.nextInt();
            System.out.println();
            if(a == 2){
                String inputFile2 = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//encoded.txt";
                String outputFile2 = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//lzss_encoded.txt";
                BufferedReader reader = new BufferedReader(new FileReader(inputFile2));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile2));
                StringBuilder text2 = new StringBuilder();
                String line;
                boolean startEncoding = false;
                while ((line = reader.readLine()) != null) {
                    if (startEncoding) {
                        text2.append(line);
                    } else if (line.equals("EOF")) {
                        startEncoding = true;
                    }
                }
                String encodedText = LZSS.encodeText(text2.toString());
                writer.write(encodedText);
                reader.close();
                writer.close();
                System.out.println("Файл успешно закодирован по алгоритму LZSS!");
            }
            System.out.print("Нажмите 3, чтобы выполнить декодирование по алгоритму LZSS: ");
            a = write.nextInt();
            System.out.println();
            if(a == 3){
                String inputFile3 = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//lzss_encoded.txt";
                String outputFile3 = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//lzss_decoded.txt";
                BufferedReader reader = new BufferedReader(new FileReader(inputFile3));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile3));
                StringBuilder text3 = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text3.append(line);
                }
                String encodedText = LZSS.decodeText(text3.toString());
                writer.write(encodedText);
                reader.close();
                writer.close();
                System.out.println("Файл успешно декодирован по алгоритму LZSS!");
            }
            System.out.print("Нажмите 4, чтобы выполнить декодирование по алгоритму Хаффмана: ");
            a = write.nextInt();
            System.out.println();
            if(a == 4){
                String encodedFilePath = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//lzss_decoded.txt";
                String decodedFilePath = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//decoded.txt";
                String inputFile5 = "D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//encoded.txt";
                Main.decodeFromFile(encodedFilePath, decodedFilePath, inputFile5);
                System.out.println("Файл успешно декодирован по алгоритму Хаффмана!");
            }
            // Чтение изображения из текстового файла
            BufferedReader reader = new BufferedReader(new FileReader("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//decoded.txt"));
            String line;
            int x = 0;
            int y = 0;
            while ((line = reader.readLine()) != null) {
                String[] colors = line.split(", ");
                int red = Integer.parseInt(colors[0].substring(1).trim());
                int green = Integer.parseInt(colors[1].trim());
                int blue = Integer.parseInt(colors[2].substring(0, colors[2].length() - 2).trim());
                int rgb = (red << 16) | (green << 8) | blue;

                image.setRGB(x, y, rgb);
                x++;
                if (x == image.getWidth()) {
                    x = 0;
                    y++;
                }
            }
            reader.close();

            File outputImage = new File("D://JAVAPROG//Kurs3//encodingOfInformation//LA3//src//main//resources//com//la3//task3//la3//text//3.bmp");
            ImageIO.write(image, "bmp", outputImage);

        } catch (IOException e) {
            System.out.println("Ошибка при чтении/записи файлов: " + e.getMessage());
        }
    }
}


