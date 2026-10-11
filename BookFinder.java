import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookFinder extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JButton clearButton;
    private JButton openButton;

    private DefaultListModel<String> listModel;
    private JList<String> bookList;
    private JTextArea detailsArea;
    private JLabel statusLabel;

    private final ArrayList<Book> books = new ArrayList<>();
    private String selectedBookUrl = "";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    static class Book {
        String title;
        String authors;
        String publisher;
        String publishedDate;
        String description;
        String pageCount;
        String categories;
        String url;

        Book(String title, String authors, String publisher,
             String publishedDate, String description,
             String pageCount, String categories, String url) {

            this.title = title;
            this.authors = authors;
            this.publisher = publisher;
            this.publishedDate = publishedDate;
            this.description = description;
            this.pageCount = pageCount;
            this.categories = categories;
            this.url = url;
        }
    }

    public BookFinder() {
        setTitle("Book Finder");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel heading = new JLabel("BOOK FINDER", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(new Color(35, 75, 120));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 15, 10, 15)
        );

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setToolTipText("Enter a book title or author name");

        searchButton = new JButton("Search Books");
        clearButton = new JButton("Clear");

        searchButton.setBackground(new Color(35, 100, 170));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        searchPanel.add(new JLabel("Enter book title or author:"),
                        BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Book list
        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setFont(new Font("Arial", Font.PLAIN, 14));
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(bookList);
        listScrollPane.setBorder(
            BorderFactory.createTitledBorder("Search Results")
        );
        listScrollPane.setPreferredSize(new Dimension(330, 400));

        // Details area
        detailsArea = new JTextArea();
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setEditable(false);
        detailsArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setBorder(
            BorderFactory.createTitledBorder("Book Details")
        );

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 15, 5, 15)
        );
        contentPanel.add(listScrollPane, BorderLayout.WEST);
        contentPanel.add(detailsScrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout(10, 5));
        footerPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 15, 12, 15)
        );

        statusLabel = new JLabel("Search for your favourite books.");
        openButton = new JButton("Open Google Books Page");
        openButton.setEnabled(false);

        footerPanel.add(statusLabel, BorderLayout.CENTER);
        footerPanel.add(openButton, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);

        // Search button action
        searchButton.addActionListener(e -> searchBooks());

        // Press Enter to search
        searchField.addActionListener(e -> searchBooks());

        // Clear button action
        clearButton.addActionListener(e -> {
            searchField.setText("");
            listModel.clear();
            books.clear();
            detailsArea.setText("");
            selectedBookUrl = "";
            openButton.setEnabled(false);
            statusLabel.setText("Search cleared. Enter a new book title.");
        });

        // Select a book to view its details
        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = bookList.getSelectedIndex();

                if (index >= 0 && index < books.size()) {
                    showBookDetails(books.get(index));
                }
            }
        });

        // Open selected book page
        openButton.addActionListener(e -> {
            if (!selectedBookUrl.isEmpty()) {
                try {
                    Desktop.getDesktop().browse(
                        URI.create(selectedBookUrl)
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Unable to open the book page.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void searchBooks() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a book title or author name.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        listModel.clear();
        books.clear();
        detailsArea.setText("");
        selectedBookUrl = "";
        openButton.setEnabled(false);

        searchButton.setEnabled(false);
        statusLabel.setText("Searching for books...");

        // Run the API request in the background
        SwingWorker<ArrayList<Book>, Void> worker =
            new SwingWorker<ArrayList<Book>, Void>() {

            @Override
            protected ArrayList<Book> doInBackground() throws Exception {
                String encodedQuery = URLEncoder.encode(
                    query, StandardCharsets.UTF_8
                );

                String apiUrl =
                    "https://www.googleapis.com/books/v1/volumes?q="
                    + encodedQuery + "&maxResults=20";

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

                HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() != 200) {
                    throw new Exception(
                        "Google Books API returned HTTP "
                        + response.statusCode()
                    );
                }

                return parseBooks(response.body());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);

                try {
                    ArrayList<Book> results = get();

                    books.addAll(results);

                    for (Book book : books) {
                        listModel.addElement(book.title);
                    }

                    if (books.isEmpty()) {
                        statusLabel.setText("No books found.");
                        detailsArea.setText(
                            "No books were found for: " + query
                            + "\n\nTry another title or author name."
                        );
                    } else {
                        statusLabel.setText(
                            books.size() + " book(s) found."
                        );
                        bookList.setSelectedIndex(0);
                    }

                } catch (Exception ex) {
                    statusLabel.setText("Search failed.");

                    detailsArea.setText(
                        "Unable to retrieve books.\n\n"
                        + "Please check your internet connection "
                        + "and try again.\n\n"
                        + "Error: " + ex.getMessage()
                    );
                }
            }
        };

        worker.execute();
    }

    private ArrayList<Book> parseBooks(String json) {
        ArrayList<Book> result = new ArrayList<>();

        Matcher itemsMatcher = Pattern.compile(
            "\"items\"\\s*:\\s*\\["
        ).matcher(json);

        if (!itemsMatcher.find()) {
            return result;
        }

        int arrayStart = itemsMatcher.end();
        int arrayEnd = findMatchingBracket(json, arrayStart - 1);

        if (arrayEnd == -1) {
            return result;
        }

        String itemsText = json.substring(arrayStart, arrayEnd);

        int position = 0;

        while (position < itemsText.length()) {
            int objectStart = itemsText.indexOf("{", position);

            if (objectStart == -1) {
                break;
            }

            int objectEnd = findMatchingBracket(itemsText, objectStart);

            if (objectEnd == -1) {
                break;
            }

            String item = itemsText.substring(
                objectStart, objectEnd + 1
            );

            String title = extractString(
                item, "\"title\"\\s*:\\s*"
            );

            if (title.isEmpty()) {
                title = "Untitled Book";
            }

            String authors = extractArrayStrings(
                item, "\"authors\"\\s*:\\s*\\["
            );

            String publisher = extractString(
                item, "\"publisher\"\\s*:\\s*"
            );

            String publishedDate = extractString(
                item, "\"publishedDate\"\\s*:\\s*"
            );

            String description = extractString(
                item, "\"description\"\\s*:\\s*"
            );

            String pageCount = extractString(
                item, "\"pageCount\"\\s*:\\s*"
            );

            String categories = extractArrayStrings(
                item, "\"categories\"\\s*:\\s*\\["
            );

            String url = extractString(
                item, "\"previewLink\"\\s*:\\s*"
            );

            if (url.isEmpty()) {
                url = extractString(
                    item, "\"infoLink\"\\s*:\\s*"
                );
            }

            result.add(new Book(
                title,
                authors,
                publisher,
                publishedDate,
                description,
                pageCount,
                categories,
                url
            ));

            position = objectEnd + 1;
        }

        return result;
    }

    private int findMatchingBracket(String text, int start) {
        if (start < 0 || start >= text.length()) {
            return -1;
        }

        char opening = text.charAt(start);
        char closing;

        if (opening == '{') {
            closing = '}';
        } else if (opening == '[') {
            closing = ']';
        } else {
            return -1;
        }

        int depth = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);

            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (c == '"') {
                inString = true;
            } else if (c == opening) {
                depth++;
            } else if (c == closing) {
                depth--;

                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private String extractString(String text, String keyPattern) {
        Pattern pattern = Pattern.compile(
            keyPattern + "\"((?:\\\\.|[^\"\\\\])*)\"",
            Pattern.DOTALL
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return unescapeJson(matcher.group(1));
        }

        return "";
    }

    private String extractArrayStrings(String text, String keyPattern) {
        Matcher matcher = Pattern.compile(keyPattern).matcher(text);

        if (!matcher.find()) {
            return "";
        }

        int start = matcher.end() - 1;
        int end = findMatchingBracket(text, start);

        if (end == -1) {
            return "";
        }

        String arrayText = text.substring(start + 1, end);
        Matcher values = Pattern.compile(
            "\"((?:\\\\.|[^\"\\\\])*)\""
        ).matcher(arrayText);

        ArrayList<String> items = new ArrayList<>();

        while (values.find()) {
            items.add(unescapeJson(values.group(1)));
        }

        return String.join(", ", items);
    }

    private String unescapeJson(String value) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c != '\\' || i + 1 >= value.length()) {
                output.append(c);
                continue;
            }

            char next = value.charAt(++i);

            switch (next) {
                case '"':
                    output.append('"');
                    break;
                case '\\':
                    output.append('\\');
                    break;
                case '/':
                    output.append('/');
                    break;
                case 'b':
                    output.append('\b');
                    break;
                case 'f':
                    output.append('\f');
                    break;
                case 'n':
                    output.append('\n');
                    break;
                case 'r':
                    output.append('\r');
                    break;
                case 't':
                    output.append('\t');
                    break;
                case 'u':
                    if (i + 4 < value.length()) {
                        try {
                            int code = Integer.parseInt(
                                value.substring(i + 1, i + 5), 16
                            );
                            output.append((char) code);
                            i += 4;
                        } catch (NumberFormatException ex) {
                            output.append('u');
                        }
                    } else {
                        output.append('u');
                    }
                    break;
                default:
                    output.append(next);
                    break;
            }
        }

        return output.toString();
    }

    private void showBookDetails(Book book) {
        StringBuilder details = new StringBuilder();

        details.append("TITLE\n");
        details.append(book.title).append("\n\n");

        details.append("AUTHOR(S)\n");
        details.append(
            book.authors.isEmpty() ? "Not available" : book.authors
        ).append("\n\n");

        details.append("PUBLISHER\n");
        details.append(
            book.publisher.isEmpty() ? "Not available" : book.publisher
        ).append("\n\n");

        details.append("PUBLISHED DATE\n");
        details.append(
            book.publishedDate.isEmpty()
                ? "Not available" : book.publishedDate
        ).append("\n\n");

        details.append("PAGE COUNT\n");
        details.append(
            book.pageCount.isEmpty() ? "Not available" : book.pageCount
        ).append("\n\n");

        details.append("CATEGORIES\n");
        details.append(
            book.categories.isEmpty() ? "Not available" : book.categories
        ).append("\n\n");

        details.append("DESCRIPTION\n");
        details.append(
            book.description.isEmpty()
                ? "No description available."
                : book.description
        ).append("\n");

        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);

        selectedBookUrl = book.url;
        openButton.setEnabled(!selectedBookUrl.isEmpty());

        statusLabel.setText("Selected: " + book.title);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookFinder app = new BookFinder();
            app.setVisible(true);
        });
    }
}