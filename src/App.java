import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println(
                    "Usage: java -jar myaplication.jar -file=/path/to/content/file.txt -top=<top_value> -phraseSize=<phrase_size>");
            return;
        }

        String filePath = "";
        int top = 0;
        int phraseSize = 0;

        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length == 2) {
                String paramName = parts[0].trim();
                String paramValue = parts[1].trim();

                switch (paramName) {
                    case "-file":
                        filePath = paramValue;
                        break;
                    case "-top":
                        top = Integer.parseInt(paramValue);
                        break;
                    case "-phraseSize":
                        phraseSize = Integer.parseInt(paramValue);
                        break;
                    default:
                        System.out.println("Invalid parameter: " + paramName);
                        return;
                }
            } else {
                System.out.println("Invalid argument format: " + arg);
                return;
            }
        }

        try {
            String content = readFile(filePath);
            String[] words = content.split("\\s+");
            String[] sentences = content.split("[.!?]");

            int wordCount = words.length;
            int sentenceCount = sentences.length;

            Map<String, Integer> phraseMap = new HashMap<>();
            for (int i = 0; i < words.length - phraseSize + 1; i++) {
                StringBuilder phraseBuilder = new StringBuilder();
                for (int j = 0; j < phraseSize; j++) {
                    phraseBuilder.append(words[i + j]).append(" ");
                }
                String phrase = phraseBuilder.toString().trim();
                phraseMap.put(phrase, phraseMap.getOrDefault(phrase, 0) + 1);
            }

            TableGenerator tableGenerator1 = new TableGenerator();
            List<String> headersList1 = List.of("Number of words:", String.valueOf(wordCount));
            List<List<String>> rowsList1 = new ArrayList<>();
            rowsList1.add(List.of("Number of sentences:", String.valueOf(sentenceCount)));
            System.out.println(tableGenerator1.generateTable(headersList1, rowsList1));

            TableGenerator tableGenerator = new TableGenerator();
            List<String> headersList = List.of("Phrase", "Count");
            List<List<String>> rowsList = phraseMap.entrySet().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                    .limit(top)
                    .map(entry -> List.of(entry.getKey(), String.valueOf(entry.getValue())))
                    .collect(Collectors.toList());
            System.out.println(tableGenerator.generateTable(headersList, rowsList));

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
