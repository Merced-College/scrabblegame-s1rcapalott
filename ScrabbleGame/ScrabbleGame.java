import java.io.*;
import java.util.*;

public class ScrabbleGame {
    private ArrayList<Word> wordList;

    public ScrabbleGame() {
        wordList = new ArrayList<>();
        loadWords("CollinsScarbbleWords_2019.txt");
    }

    private void loadWords(String filename) {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                wordList.add(new Word(fileScanner.nextLine()));
            }
            Collections.sort(wordList);  // Sort for binary search
        } catch (IOException e) {
            System.out.println("Error reading word list: " + e.getMessage());
        }
    }

    private char[] generatePlayableLetters() {
        Random rand = new Random();
        String chosen = "";

        while (true) {
            Word randomWord = wordList.get(rand.nextInt(wordList.size()));
            String text = randomWord.getText();
            if (text.length() >= 4) {
                Set<Character> unique = new HashSet<>();
                for (char c : text.toCharArray()) unique.add(c);
                if (unique.size() >= 4) {
                    chosen = text;
                    break;
                }
            }
        }

        List<Character> chars = new ArrayList<>();
        for (char c : chosen.toCharArray()) chars.add(c);
        Collections.shuffle(chars);
        char[] result = new char[4];
        for (int i = 0; i < 4; i++) result[i] = chars.get(i);
        return result;
    }

    private boolean isWordMadeFromLetters(String word, char[] letters) {
        Map<Character, Integer> available = new HashMap<>();
        for (char c : letters) {
            available.put(c, available.getOrDefault(c, 0) + 1);
        }
        for (char c : word.toCharArray()) {
            if (!available.containsKey(c) || available.get(c) == 0) return false;
            available.put(c, available.get(c) - 1);
        }
        return true;
    }

    private int binarySearch(String target) {
        int left = 0, right = wordList.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = wordList.get(mid).getText().compareTo(target);
            if (cmp == 0) return mid;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    private int calculateScore(String word) {
        if (word.length() == 3) return 3;
        if (word.length() == 4) return 5;
        return 1; // fallback
    }

    public void play() {
        Scanner input = new Scanner(System.in);
        char[] letters = generatePlayableLetters();

        while (true) {
            System.out.println("Your letters are: " + Arrays.toString(letters));
            System.out.print("Enter a word using these letters (or type 'exit' to quit): ");
            String userWord = input.nextLine().toLowerCase();

            if (userWord.equals("exit")) {
                System.out.println("Thanks for playing!");
                break;
            }

            if (!isWordMadeFromLetters(userWord, letters)) {
                System.out.println("Invalid: You used letters not in the set.\n");
                continue;
            }

            int index = binarySearch(userWord);
            if (index != -1) {
                int score = calculateScore(userWord);
                System.out.println("✅ Valid word! Score: " + score + "\n");
            } else {
                System.out.println("❌ Not a valid dictionary word.\n");
            }
        }
    }

        
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ScrabbleGame game = new ScrabbleGame();

        while (true) {
            game.play();  // plays one full round (with its own loop)
            System.out.print("Play again? (yes/no): ");
            String choice = scanner.nextLine().toLowerCase();
            if (!choice.equals("yes")) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

}
