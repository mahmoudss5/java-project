package com.example.library;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

public class Librarysystemgui extends Application {
    enum BookStatus {
        AVAILABLE,
        CHECKED_OUT,
        RESERVED
    }

    static abstract class User {
        protected String username;
        protected String password;
        protected String name;
        protected String contactinfo;

        public User(String username, String password, String name, String contactinfo) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.contactinfo = contactinfo;
        }

        public String getUsername() {
            return username;
        }

        public boolean checkpass(String password) {
            return this.password.equals(password);
        }

        public String getName() {
            return name;
        }

        public String getContactinfo() {
            return contactinfo;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setinfo(String contactinfo) {
            this.contactinfo = contactinfo;
        }
    }

    static class Admin extends User {
        public Admin(String username, String password, String name, String contactInfo) {
            super(username, password, name, contactInfo);
        }
    }

    static class Librarian extends User {
        public Librarian(String username, String password, String name, String contactInfo) {
            super(username, password, name, contactInfo);
        }
    }

    static class Patron extends User {
        private List<Book> checkedbooks = new ArrayList<>();
        private List<Book> checkhistory = new ArrayList<>();

        public Patron(String username, String password, String name, String contactInfo) {
            super(username, password, name, contactInfo);
        }

        public List<Book> checkbook() {
            return checkedbooks;
        }

        public List<Book> getCheckhistory() {
            return checkhistory;
        }

        public void checkoutBook(Book book) {
            checkedbooks.add(book);
            checkhistory.add(book);
        }

        public void returnBook(Book book) {
            checkedbooks.remove(book);
        }
    }

    static class Book {
        private String id;
        private String title;
        private String author;
        private String genre;
        private int publyear;
        private BookStatus status;
        private String summary;
        private String dueDate = "";

        public Book(String title, String author, String genre, int publyear, String summary) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.publyear = publyear;
            this.summary = summary;
            this.status = BookStatus.AVAILABLE;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getGenre() {
            return genre;
        }

        public int getPublyear() {
            return publyear;
        }

        public BookStatus getStatus() {
            return status;
        }

        public String getSummary() {
            return summary;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setStatus(BookStatus status) {
            this.status = status;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }
    }

    static class Reservation { // Process to reserve a book not available in our stock
        private String reservationId;
        private String bookId;
        private String patname;
        private boolean notified = false; // Boolean value indicating if the user has been notified

        public Reservation(String bookId, String patronname) {
            this.reservationId = UUID.randomUUID().toString();
            this.bookId = bookId;
            this.patname = patronname;
        }

        public void setrid(String reservationId) {
            this.reservationId = reservationId;
        }

        public String getReservationId() {
            return reservationId;
        }

        public String getBookId() {
            return bookId;
        }

        public String getPatname() {
            return patname;
        }

        public boolean isNotified() {
            return notified;
        }

        public void setNotified(boolean notified) {
            this.notified = notified;
        }
    }
    static class user_repo {
        private Map<String, User> users = new HashMap<>();
        private final String FILE_NAME = "users.txt";

        public user_repo() {
            loadUsers();
        }

        public void addUser(User user) {
            users.put(user.getUsername(), user);
            saveUsers();
        }

        public User getUser(String username) {
            return users.get(username);
        }

        public void deleteUser(String username) {
            users.remove(username);
            saveUsers();
        }

        public Collection<User> getall() {
            return users.values();
        }

        public void saveUsers() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (User user : users.values()) {
                    String role = "Admin";
                    if (user instanceof Librarian) {
                        role = "Librarian";
                    } else if (user instanceof Patron) {
                        role = "Patron";
                    }
                    writer.write("USER | " + role + " | " + user.username + " | " + user.password + " | " + user.name + " | " + user.contactinfo);
                    writer.newLine();

                    if (user instanceof Patron) {
                        Patron p = (Patron) user;
                        for (Book b : p.checkbook()) {
                            writer.write("CHECKEDOUT|" + b.getId() + "|" + b.getTitle() + "|" + b.getAuthor() + "|" + b.getGenre() + "|" + b.getPublyear() + "|" + b.getStatus() + "|" + b.getSummary() + "|" + b.getDueDate());
                            writer.newLine();
                        }

                        for (Book b : p.getCheckhistory()) {
                            writer.write("HISTORY|" + b.getId() + "|" + b.getTitle() + "|" + b.getAuthor() + "|" + b.getGenre() + "|" + b.getPublyear() + "|" + b.getStatus() + "|" + b.getSummary() + "|" + b.getDueDate());
                            writer.newLine();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadUsers() {
            File f = new File(FILE_NAME);
            if (!f.exists()) {
                System.out.println(FILE_NAME + " not found, starting fresh.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                User currentUser = null;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    if (line.startsWith("USER|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length < 6) {
                            System.out.println("Invalid user line: " + line);
                            continue;
                        }
                        String role = parts[1];
                        String username = parts[2];
                        String password = parts[3];
                        String name = parts[4];
                        String contactInfo = parts[5];

                        switch (role) {
                            case "Admin":
                                currentUser = new Admin(username, password, name, contactInfo);
                                break;
                            case "Librarian":
                                currentUser = new Librarian(username, password, name, contactInfo);
                                break;
                            case "Patron":
                                currentUser = new Patron(username, password, name, contactInfo);
                                break;
                            default:
                                currentUser = null;
                                System.out.println("Invalid role: " + role);
                                break;
                        }
                        if (currentUser != null) {
                            users.put(username, currentUser);
                        }
                    } else if (currentUser instanceof Patron) {
                        if (line.startsWith("CHECKEDOUT|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length < 9) { // Adjusted to 9 since split("CHECKEDOUT|")
                                System.out.println("Invalid CHECKEDOUT line: " + line);
                                continue;
                            }
                            String id = parts[1];
                            String title = parts[2];
                            String author = parts[3];
                            String genre = parts[4];
                            int year;
                            try {
                                year = Integer.parseInt(parts[5]);
                            } catch (NumberFormatException ex) {
                                System.out.println("Invalid year in CHECKEDOUT: " + line);
                                continue;
                            }
                            BookStatus status;
                            try {
                                status = BookStatus.valueOf(parts[6]);
                            } catch (Exception ex) {
                                System.out.println("Invalid status in CHECKEDOUT: " + line);
                                continue;
                            }
                            String summary = parts[7];
                            String dueDate = parts[8];
                            Book book = new Book(title, author, genre, year, summary);
                            book.setId(id);
                            book.setStatus(status);
                            book.setDueDate(dueDate);
                            ((Patron) currentUser).checkbook().add(book);

                        } else if (line.startsWith("HISTORY|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length < 9) { // Adjusted to 9
                                System.out.println("Invalid HISTORY line: " + line);
                                continue;
                            }
                            String id = parts[1];
                            String title = parts[2];
                            String author = parts[3];
                            String genre = parts[4];
                            int year;
                            try {
                                year = Integer.parseInt(parts[5]);
                            } catch (NumberFormatException ex) {
                                System.out.println("Invalid year in HISTORY: " + line);
                                continue;
                            }
                            BookStatus status;
                            try {
                                status = BookStatus.valueOf(parts[6]);
                            } catch (Exception ex) {
                                System.out.println("Invalid status in HISTORY: " + line);
                                continue;
                            }
                            String summary = parts[7];
                            String dueDate = parts[8];
                            Book book = new Book(title, author, genre, year, summary);
                            book.setId(id);
                            book.setStatus(status);
                            book.setDueDate(dueDate);
                            ((Patron) currentUser).getCheckhistory().add(book);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void setBookId(Book book, String id) {
            book.setId(id);
        }
    }

    static class bookRepository {
        private Map<String, Book> books = new HashMap<>();
        private final String FILE_NAME = "books.txt";

        public bookRepository() {
            loadBooks();
        }

        public void addBook(Book book) {
            books.put(book.getId(), book);
            saveBooks();
        }

        public void removeBook(String bookId) {
            books.remove(bookId);
            saveBooks();
        }

        public Book getBook(String bookId) {
            return books.get(bookId);
        }

        public List<Book> searchBooks(String title, String author, String genre) {
            return books.values().stream().filter(b -> {
                boolean matches = true;
                if (title != null && !title.isEmpty())
                    matches = matches && b.getTitle().toLowerCase().contains(title.toLowerCase());
                if (author != null && !author.isEmpty())
                    matches = matches && b.getAuthor().toLowerCase().contains(author.toLowerCase());
                if (genre != null && !genre.isEmpty())
                    matches = matches && b.getGenre().toLowerCase().contains(genre.toLowerCase());
                return matches;
            }).collect(Collectors.toList());
        }

        public Collection<Book> getallb() {
            return books.values();
        }

        private void loadBooks() {
            File f = new File(FILE_NAME);
            if (!f.exists()) {
                System.out.println(FILE_NAME + " not found, starting fresh.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    if (line.startsWith("BOOK|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length < 8) {
                            System.out.println("Invalid BOOK line: " + line);
                            continue;
                        }
                        String id = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        String genre = parts[4];
                        int year;
                        try {
                            year = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid year in BOOK: " + line);
                            continue;
                        }
                        BookStatus status;
                        try {
                            status = BookStatus.valueOf(parts[6]);
                        } catch (Exception ex) {
                            System.out.println("Invalid status in BOOK: " + line);
                            continue;
                        }
                        String summary = parts[7];
                        Book book = new Book(title, author, genre, year, summary);
                        book.setId(id);
                        book.setStatus(status);
                        books.put(id, book);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveBooks() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Book b : books.values()) {
                    writer.write("BOOK|" + b.getId() + "|" + b.getTitle() + "|" + b.getAuthor() + "|" + b.getGenre() + "|" + b.getPublyear() + "|" + b.getStatus() + "|" + b.getSummary());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static class reserv_repoo {
        private List<Reservation> reservations = new ArrayList<>();
        private final String FILE_NAME = "reservations.txt";

        public reserv_repoo() {
            loadreserve();
        }

        public void addReservation(Reservation reservation) {
            reservations.add(reservation);
            saveresv();
        }

        public void removereserv1(String reservationId) {
            reservations.removeIf(r -> r.getReservationId().equals(reservationId));
            saveresv();
        }

        public List<Reservation> get_resevrs(String bookId) {
            return reservations.stream()
                    .filter(r -> r.getBookId().equals(bookId))
                    .collect(Collectors.toList());
        }

        public List<Reservation> getreversions() {
            return new ArrayList<>(reservations);
        }

        public List<Reservation> getreserva_pat(String patronUsername) {
            return reservations.stream()
                    .filter(r -> r.getPatname().equals(patronUsername))
                    .collect(Collectors.toList());
        }

        public void saveresv() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Reservation r : reservations) {
                    writer.write("RES|" + r.getReservationId() + "|" + r.getBookId() + "|" + r.getPatname() + "|" + r.isNotified());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadreserve() {
            File f = new File(FILE_NAME);
            if (!f.exists()) {
                System.out.println(FILE_NAME + " not found, starting fresh.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    if (line.startsWith("RES|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length < 5) {
                            System.out.println("Invalid RES line: " + line);
                            continue;
                        }
                        String reservationId = parts[1];
                        String bookId = parts[2];
                        String patronUsername = parts[3];
                        boolean notified = Boolean.parseBoolean(parts[4]);
                        Reservation r = new Reservation(bookId, patronUsername);
                        r.setrid(reservationId);
                        r.setNotified(notified);
                        reservations.add(r);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static class UserService {
        private user_repo userRepo;

        public UserService(user_repo userRepo) {
            this.userRepo = userRepo;
        }

        public void createAdmin(String username, String password, String name, String contact) {
            Admin admin = new Admin(username, password, name, contact);
            userRepo.addUser(admin);
        }

        public void createlib(String username, String password, String name, String contact) {
            Librarian librarian = new Librarian(username, password, name, contact);
            userRepo.addUser(librarian);
        }

        public void createPatt(String username, String password, String name, String contact) {
            Patron patron = new Patron(username, password, name, contact);
            userRepo.addUser(patron);
        }

        public void deleteUser(String username) {
            userRepo.deleteUser(username);
        }

        public User login(String username, String password) {
            User user = userRepo.getUser(username);
            if (user != null && user.checkpass(password)) {
                return user;
            }
            return null;
        }

        public void updateUserInfo(String username, String newName, String newPassword, String newContact) {
            User u = userRepo.getUser(username);
            if (u != null) {
                if (!newName.isEmpty()) u.setName(newName);
                if (!newPassword.isEmpty()) u.setPassword(newPassword);
                if (!newContact.isEmpty()) u.setinfo(newContact);
                userRepo.addUser(u);
            }
        }
    }

    static class BookService {
        private bookRepository bookRepo;

        public BookService(bookRepository bookRepo) {
            this.bookRepo = bookRepo;
        }

        public void addBook(String title, String author, String genre, int year, String summary) {
            Book b = new Book(title, author, genre, year, summary);
            bookRepo.addBook(b);
        }

        public void updateBook(String bookId, String title, String author, String genre, int year, String summary) {
            Book b = bookRepo.getBook(bookId);
            if (b != null) {
                b.setTitle(title);
                b.setAuthor(author);
                b.setGenre(genre);
                b.setSummary(summary);
                bookRepo.addBook(b);
            }
        }

        public void removeBook(String bookId) {
            bookRepo.removeBook(bookId);
        }

        public List<Book> searchBooks(String title, String author, String genre) {
            return bookRepo.searchBooks(title, author, genre);
        }

        public Book getBook(String bookId) {
            return bookRepo.getBook(bookId);
        }

        public void updatebook(String bookId, BookStatus status) {
            Book b = bookRepo.getBook(bookId);
            if (b != null) {
                b.setStatus(status);
                bookRepo.addBook(b);
            }
        }

        public Collection<Book> getAllBooks() {
            return bookRepo.getallb();
        }
    }

    static class lib_serv {
        private user_repo userRepo;
        private bookRepository bookRepo;
        private reserv_repoo reservationRepo;

        public lib_serv(user_repo userRepo, bookRepository bookRepo, reserv_repoo reservationRepo) {
            this.userRepo = userRepo;
            this.bookRepo = bookRepo;
            this.reservationRepo = reservationRepo;
        }

        public boolean checkoutBook(String librarianUsername, String patronUsername, String bookId) {
            User librarian = userRepo.getUser(librarianUsername);
            User user = userRepo.getUser(patronUsername);
            Book book = bookRepo.getBook(bookId);
            if (librarian instanceof Librarian && user instanceof Patron && book != null) {
                if (book.getStatus() == BookStatus.AVAILABLE) {
                    Patron p = (Patron) user;
                    book.setStatus(BookStatus.CHECKED_OUT);
                    book.setDueDate("Due in 14 days");
                    p.checkoutBook(book);
                    bookRepo.addBook(book);
                    userRepo.addUser(p);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public boolean returnBook(String lib_name, String pat_name, String bookId) {
            User librarian = userRepo.getUser(lib_name);
            User user = userRepo.getUser(pat_name);
            Book book = bookRepo.getBook(bookId);
            if (librarian instanceof Librarian && user instanceof Patron && book != null) {
                Patron p = (Patron) user;
                Book toReturn = null;
                for (Book b : p.checkbook()) {
                    if (b.getId().equals(bookId)) {
                        toReturn = b;
                        break;
                    }
                }
                if (toReturn != null) {
                    p.returnBook(toReturn);
                    book.setStatus(BookStatus.AVAILABLE);
                    book.setDueDate("");
                    bookRepo.addBook(book);
                    userRepo.addUser(p);
                    var reservations = reservationRepo.get_resevrs(bookId);
                    if (!reservations.isEmpty()) {
                        book.setStatus(BookStatus.RESERVED);
                        bookRepo.addBook(book);
                        notify(bookId);
                    }
                    return true;
                }
            }
            return false;
        }

        public String reserveBook(String patronUsername, String bookId) {
            User user = userRepo.getUser(patronUsername);
            Book book = bookRepo.getBook(bookId);
            if (user instanceof Patron) {
                if (book == null) {
                    return "The book does not exist.";
                }
                if (book.getStatus() == BookStatus.CHECKED_OUT) {
                    Reservation r = new Reservation(bookId, patronUsername);
                    reservationRepo.addReservation(r);
                    return "The book has been reserved successfully. You will be notified when it becomes available.";
                } else if (book.getStatus() == BookStatus.AVAILABLE) {
                    return "The book is currently available. You can borrow it directly.";
                } else {
                    return "This book cannot be reserved at the moment.";
                }
            }
            return "An error occurred.";
        }

        public void notify(String bookId) {
            List<Reservation> reservations = reservationRepo.get_resevrs(bookId);
            if (!reservations.isEmpty()) {
                Reservation r = reservations.get(0);
                r.setNotified(true);
                reservationRepo.saveresv();
            }
        }

        public void pickup_reserve(String pat_name, String bookId) {
            User user = userRepo.getUser(pat_name);
            Book book = bookRepo.getBook(bookId);
            if (user instanceof Patron && book != null && book.getStatus() == BookStatus.RESERVED) {
                var reservations = reservationRepo.get_resevrs(bookId);
                if (!reservations.isEmpty() && reservations.get(0).getPatname().equals(pat_name)) {
                    Patron p = (Patron) user;
                    book.setStatus(BookStatus.CHECKED_OUT);
                    book.setDueDate("Due in 14 days");
                    p.checkoutBook(book);
                    reservationRepo.removereserv1(reservations.get(0).getReservationId());
                    bookRepo.addBook(book);
                    userRepo.addUser(p);
                }
            }
        }

        public List<Reservation> getall_reseve() {
            return reservationRepo.getreversions();
        }
    }

    private user_repo userRepo = new user_repo();
    private bookRepository bookRepo = new bookRepository();
    private reserv_repoo reservarepo = new reserv_repoo();
    private UserService userService = new UserService(userRepo);
    private BookService bookService = new BookService(bookRepo);
    private lib_serv libserv = new lib_serv(userRepo, bookRepo, reservarepo);
    private User currentUser = null;

    @Override
    public void start(Stage primaryStage) {
        initia_data();
        showLog(primaryStage);
    }

    private void initia_data() {
        if (userRepo.getall().isEmpty()) {
            userService.createAdmin("admin", "admin123", "Main Administrator", "admin@library.com");
            userService.createlib("librarian1", "libpass", "Librarian 1", "libone@library.com");
            userService.createPatt("patron1", "patpass", "John Doe", "johndoe@example.com");
        }

        if (bookRepo.getallb().isEmpty()) {
            bookService.addBook("Moby Dick", "Herman Melville", "Fiction", 1851, "A novel about the struggle with a white whale");
            bookService.addBook("1984", "George Orwell", "Dystopia", 1949, "A novel about a totalitarian regime");
            bookService.addBook("To Kill a Mockingbird", "Harper Lee", "Fiction", 1960, "A novel about justice and injustice");
        }
    }

    private void showLog(Stage stage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Log In");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setOnAction(e -> showsign());

        HBox hbSignUp = new HBox(10);
        hbSignUp.setAlignment(Pos.BOTTOM_RIGHT);
        hbSignUp.getChildren().add(signUpBtn);

        grid.add(hbBtn, 1, 4);
        grid.add(hbSignUp, 1, 5);

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            User user = userService.login(username, password);
            if (user != null) {
                currentUser = user;
                show_board(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Username or password is incorrect.");
            }
        });

        Scene scene = new Scene(grid, 400, 275);
        stage.setTitle("Library System - Log In");
        stage.setScene(scene);
        stage.show();
    }

    private void showsign() {
        Stage stage = new Stage();
        stage.setTitle("Create Account");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField u_namefiled = new TextField();
        u_namefiled.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Info");

        Button signUpBtn = new Button("Create");
        signUpBtn.setOnAction(e -> {
            String username = u_namefiled.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String contact = contactField.getText();
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            if (userRepo.getUser(username) != null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }
            userService.createPatt(username, password, name, contact);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully. You can log in now.");
            stage.close();
        });

        vbox.getChildren().addAll(new Label("Username:"), u_namefiled,
                new Label("Password:"), passwordField,
                new Label("Full Name:"), nameField,
                new Label("Contact Info:"), contactField,
                signUpBtn);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void show_board(Stage stage) {
        if (currentUser instanceof Admin) {
            showadmin_board(stage);
        } else if (currentUser instanceof Librarian) {
            showlib_dash(stage);
        } else if (currentUser instanceof Patron) {
            showpatt(stage);
        }
    }

    private void showadmin_board(Stage stage) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label label = new Label("Admin Dashboard");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button addUserBtn = new Button("Add New User");
        addUserBtn.setMinWidth(200);
        addUserBtn.setOnAction(e -> add_user());

        Button manageUsersBtn = new Button("View and Delete Users");
        manageUsersBtn.setMinWidth(200);
        manageUsersBtn.setOnAction(e -> showAllUsers());

        Button addBookBtn = new Button("Add New Book");
        addBookBtn.setMinWidth(200);
        addBookBtn.setOnAction(e -> addbook());

        Button manageBooksBtn = new Button("View, Delete or Update Book Status");
        manageBooksBtn.setMinWidth(200);
        manageBooksBtn.setOnAction(e -> showall_books());

        Button editProfileBtn = new Button("Edit Account");
        editProfileBtn.setMinWidth(200);
        editProfileBtn.setOnAction(e -> editprof());

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setMinWidth(200);
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showLog(stage);
        });

        vbox.getChildren().addAll(label, addUserBtn, manageUsersBtn, addBookBtn, manageBooksBtn, editProfileBtn, logoutBtn);

        Scene scene = new Scene(vbox, 400, 600);
        stage.setScene(scene);
    }

    private void editprof() {
        Stage stage = new Stage();
        stage.setTitle("Edit Account");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField nameField = new TextField(currentUser.getName());
        nameField.setPromptText("New Name");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password (Leave blank if not changing)");

        TextField contactField = new TextField(currentUser.getContactinfo());
        contactField.setPromptText("New Contact Info");

        Button saveBtn = new Button("Save Changes");
        saveBtn.setOnAction(e -> {
            String newName = nameField.getText();
            String newPassword = passwordField.getText();
            String newContact = contactField.getText();
            userService.updateUserInfo(currentUser.getUsername(), newName, newPassword, newContact);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Information updated successfully.");
            currentUser = userRepo.getUser(currentUser.getUsername());
            stage.close();
        });

        vbox.getChildren().addAll(new Label("Name:"), nameField,
                new Label("Password:"), passwordField,
                new Label("Contact Info:"), contactField, saveBtn);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void add_user() {
        Stage stage = new Stage();
        stage.setTitle("Add New User");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Info");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Librarian", "Patron");
        roleBox.setValue("Patron");

        Button addBtn = new Button("Add User");
        addBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String contact = contactField.getText();
            String role = roleBox.getValue();
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            if (userRepo.getUser(username) != null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            switch (role) {
                case "Admin":
                    userService.createAdmin(username, password, name, contact);
                    break;
                case "Librarian":
                    userService.createlib(username, password, name, contact);
                    break;
                case "Patron":
                    userService.createPatt(username, password, name, contact);
                    break;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully.");
            usernameField.clear();
            passwordField.clear();
            nameField.clear();
            contactField.clear();
            roleBox.setValue("Patron");
        });

        vbox.getChildren().addAll(new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Full Name:"), nameField,
                new Label("Contact Info:"), contactField,
                new Label("Role:"), roleBox,
                addBtn);

        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAllUsers() {
        Stage stage = new Stage();
        stage.setTitle("All Users");

        TableView<User> table = new TableView<>();

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> {
            User u = data.getValue();
            String role = "Admin";
            if (u instanceof Librarian) role = "Librarian";
            else if (u instanceof Patron) role = "Patron";
            return new SimpleStringProperty(role);
        });

        TableColumn<User, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> {
            Button btn = new Button("Delete");
            TableCell<User, Void> cell = new TableCell<>() {
                {
                    btn.setOnAction(e -> {
                        User u = getTableView().getItems().get(getIndex());
                        if (!u.getUsername().equals("admin")) {
                            userService.deleteUser(u.getUsername());
                            table.setItems(FXCollections.observableArrayList(userRepo.getall()));
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete the main administrator.");
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            };
            return cell;
        });

        table.getColumns().addAll(usernameCol, nameCol, roleCol, deleteCol);
        table.setItems(FXCollections.observableArrayList(userRepo.getall()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vbox = new VBox(table);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void addbook() {
        Stage stage = new Stage();
        stage.setTitle("Add New Book");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        TextField yearField = new TextField();
        yearField.setPromptText("Publication Year");

        TextField summaryField = new TextField();
        summaryField.setPromptText("Summary");

        Button addBtn = new Button("Add Book");
        addBtn.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            String yearStr = yearField.getText();
            String summary = summaryField.getText();
            if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || yearStr.isEmpty() || summary.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Publication year must be a number.");
                return;
            }
            bookService.addBook(title, author, genre, year, summary);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");
            titleField.clear();
            authorField.clear();
            genreField.clear();
            yearField.clear();
            summaryField.clear();
        });

        vbox.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Genre:"), genreField,
                new Label("Publication Year:"), yearField,
                new Label("Summary:"), summaryField,
                addBtn);

        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showall_books() {
        Stage stage = new Stage();
        stage.setTitle("All Books");

        TableView<Book> table = new TableView<>();

        TableColumn<Book, String> idCol = new TableColumn<>("Book ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

        TableColumn<Book, String> summaryCol = new TableColumn<>("Summary");
        summaryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSummary()));

        TableColumn<Book, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> {
            Button btn = new Button("Delete");
            TableCell<Book, Void> cell = new TableCell<>() {
                {
                    btn.setOnAction(e -> {
                        Book b = getTableView().getItems().get(getIndex());
                        bookService.removeBook(b.getId());
                        table.setItems(FXCollections.observableArrayList(bookService.getAllBooks()));
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : btn);
                }
            };
            return cell;
        });

        TableColumn<Book, BookStatus> statused = new TableColumn<>("Update Status");
        statused.setCellFactory(col -> {
            return new TableCell<>() {
                ComboBox<BookStatus> combobox = new ComboBox<>(FXCollections.observableArrayList(BookStatus.values()));

                {
                    combobox.setOnAction(e -> {
                        Book b = getTableView().getItems().get(getIndex());
                        BookStatus newStatus = combobox.getValue();
                        bookService.updatebook(b.getId(), newStatus);
                        table.refresh();
                    });
                }

                @Override
                protected void updateItem(BookStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Book b = getTableView().getItems().get(getIndex());
                        combobox.setValue(b.getStatus());
                        setGraphic(combobox);
                    }
                }
            };
        });

        table.getColumns().addAll(idCol, titleCol, authorCol, genreCol, statusCol, summaryCol, deleteCol, statused);
        table.setItems(FXCollections.observableArrayList(bookService.getAllBooks()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vbox = new VBox(table);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 900, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showlib_dash(Stage stage) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label label = new Label("Librarian Dashboard");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button checkoutBtn = new Button("Checkout Book");
        checkoutBtn.setMinWidth(200);
        checkoutBtn.setOnAction(e -> checkoutBook());

        Button returnBtn = new Button("Return Book");
        returnBtn.setMinWidth(200);
        returnBtn.setOnAction(e -> returnBook());

        Button viewres = new Button("View Reservations");
        viewres.setMinWidth(200);
        viewres.setOnAction(e -> showresevv2());

        Button editprofi = new Button("Edit Account");
        editprofi.setMinWidth(200);
        editprofi.setOnAction(e -> editprof());

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setMinWidth(200);
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showLog(stage);
        });

        vbox.getChildren().addAll(label, checkoutBtn, returnBtn, viewres, editprofi, logoutBtn);

        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
    }

    private void showresevv2() {
        Stage stage = new Stage();
        stage.setTitle("View Reservations");
        TableView<Reservation> table = new TableView<>();

        TableColumn<Reservation, String> patronCol = new TableColumn<>("Patron Username");
        patronCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatname()));

        TableColumn<Reservation, String> bookCol = new TableColumn<>("Book ID");
        bookCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookId()));

        TableColumn<Reservation, String> notifiedCol = new TableColumn<>("Notified?");
        notifiedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isNotified() ? "Yes" : "No"));

        TableColumn<Reservation, Void> notifyCol = new TableColumn<>("Send Notification");
        notifyCol.setCellFactory(col -> {
            return new TableCell<>() {
                Button btn = new Button("Notify");

                {
                    btn.setOnAction(e -> {
                        Reservation r = getTableView().getItems().get(getIndex());
                        r.setNotified(true);
                        reservarepo.saveresv();
                        table.refresh();
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Notification sent.");
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) setGraphic(null);
                    else {
                        Reservation r = getTableView().getItems().get(getIndex());
                        btn.setDisable(r.isNotified());
                        setGraphic(btn);
                    }
                }
            };
        });

        table.getColumns().addAll(patronCol, bookCol, notifiedCol, notifyCol);
        table.setItems(FXCollections.observableArrayList(libserv.getall_reseve()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vbox = new VBox(table);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void checkoutBook() {
        Stage stage = new Stage();
        stage.setTitle("Checkout Book");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField patf = new TextField();
        patf.setPromptText("Patron Username");

        TextField bkfild = new TextField();
        bkfild.setPromptText("Book ID");

        Button cbtn = new Button("Checkout");
        cbtn.setOnAction(e -> {
            String patronUsername = patf.getText();
            String bookId = bkfild.getText();
            if (patronUsername.isEmpty() || bookId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            boolean success = libserv.checkoutBook("librarian1", patronUsername, bookId);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book checked out successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to checkout the book. It may not be available.");
            }
            stage.close();
        });

        vbox.getChildren().addAll(new Label("Patron Username:"), patf,
                new Label("Book ID:"), bkfild,
                cbtn);

        Scene scene = new Scene(vbox, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void returnBook() {
        Stage stage = new Stage();
        stage.setTitle("Return Book");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField field = new TextField();
        field.setPromptText("Patron Username");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");

        Button returnBtn = new Button("Return");
        returnBtn.setOnAction(e -> {
            String patronUsername = field.getText();
            String bookId = bookIdField.getText();

            if (patronUsername.isEmpty() || bookId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                return;
            }
            boolean success = libserv.returnBook("librarian1", patronUsername, bookId);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to return the book. It may not be checked out by the patron.");
            }
            stage.close();
        });

        vbox.getChildren().addAll(new Label("Patron Username:"), field,
                new Label("Book ID:"), bookIdField,
                returnBtn);
        Scene scene = new Scene(vbox, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void showpatt(Stage stage) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Label label = new Label("Patron Dashboard");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button sbook = new Button("Search Books");
        sbook.setMinWidth(200);
        sbook.setOnAction(e -> searchBooks());

        Button rese_btn = new Button("Reserve Book");
        rese_btn.setMinWidth(200);
        rese_btn.setOnAction(e -> reswin());

        Button vhist = new Button("View History");
        vhist.setMinWidth(200);
        vhist.setOnAction(e -> show_his_pat());

        Button editpro = new Button("Edit Account");
        editpro.setMinWidth(200);
        editpro.setOnAction(e -> editprof());

        Button out_btn = new Button("Log Out");
        out_btn.setMinWidth(200);
        out_btn.setOnAction(e -> {
            currentUser = null;
            showLog(stage);
        });

        boolean get_n = ch_not();
        if (get_n) {
            Button notifyBtn = new Button("Notifications");
            notifyBtn.setMinWidth(200);
            notifyBtn.setOnAction(ev -> {
                showAlert(Alert.AlertType.INFORMATION, "Notification", "The book you reserved is now available. You can pick it up.");
            });
            vbox.getChildren().add(notifyBtn);
        }

        vbox.getChildren().addAll(label, sbook, rese_btn, vhist, editpro, out_btn);

        Scene scene = new Scene(vbox, 400, 400);
        stage.setScene(scene);
    }

    private boolean ch_not() {
        if (currentUser instanceof Patron) {
            List<Reservation> reservations = reservarepo.getreserva_pat(currentUser.getUsername());
            for (Reservation r : reservations) {
                if (r.isNotified()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void searchBooks() {
        Stage stage = new Stage();
        stage.setTitle("Search for Book");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            List<Book> results = bookService.searchBooks(title, author, genre);
            if (results.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Search Results", "No books found.");
                return;
            }
            TableView<Book> table = new TableView<>();
            TableColumn<Book, String> idCol = new TableColumn<>("Book ID");
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

            TableColumn<Book, String> titleCol = new TableColumn<>("Title");
            titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

            TableColumn<Book, String> authorCol = new TableColumn<>("Author");
            authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));

            TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
            genreCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

            TableColumn<Book, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));

            TableColumn<Book, String> summaryCol = new TableColumn<>("Summary");
            summaryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSummary()));

            table.getColumns().addAll(idCol, titleCol, authorCol, genreCol, statusCol, summaryCol);
            table.setItems(FXCollections.observableArrayList(results));
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            VBox tableBox = new VBox(table);
            tableBox.setPadding(new Insets(10));

            Scene tableScene = new Scene(tableBox, 700, 400);
            Stage tableStage = new Stage();
            tableStage.setTitle("Search Results");
            tableStage.setScene(tableScene);
            tableStage.show();
        });

        vbox.getChildren().addAll(new Label("Title:"), titleField,
                new Label("Author:"), authorField,
                new Label("Genre:"), genreField,
                searchBtn);

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void reswin() {
        Stage stage = new Stage();
        stage.setTitle("Reserve a Book");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField bokfil = new TextField();
        bokfil.setPromptText("Book ID");

        Button reserveBtn = new Button("Reserve");
        reserveBtn.setOnAction(e -> {
            String bookId = bokfil.getText();
            if (bookId.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter the book ID.");
                return;
            }
            String result = libserv.reserveBook(currentUser.getUsername(), bookId);
            showAlert(Alert.AlertType.INFORMATION, "Operation Result", result);
            stage.close();
        });

        vbox.getChildren().addAll(new Label("Book ID:"), bokfil, reserveBtn);
        Scene scene = new Scene(vbox, 400, 150);
        stage.setScene(scene);
        stage.show();
    }

    private void show_his_pat() {
        Stage stage = new Stage();
        stage.setTitle("Your Records");
        TabPane tabPane = new TabPane();

        Tab currentTab = new Tab("Books Currently on Loan");
        TableView<Book> currentTable = new TableView<>();
        currentTable.setItems(FXCollections.observableArrayList(((Patron) currentUser).checkbook()));
        currentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> cTitle = new TableColumn<>("Title");
        cTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Book, String> cAuthor = new TableColumn<>("Author");
        cAuthor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));

        TableColumn<Book, String> cDue = new TableColumn<>("Due Date");
        cDue.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDate()));

        currentTable.getColumns().addAll(cTitle, cAuthor, cDue);
        currentTab.setContent(currentTable);

        Tab historyTab = new Tab("Previous History");
        TableView<Book> historyTable = new TableView<>();
        historyTable.setItems(FXCollections.observableArrayList(((Patron) currentUser).getCheckhistory()));
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> hTitle = new TableColumn<>("Title");
        hTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Book, String> hAuthor = new TableColumn<>("Author");
        hAuthor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));

        TableColumn<Book, String> hStatus = new TableColumn<>("Status Upon Return");
        hStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().toString()));

        TableColumn<Book, String> hDue = new TableColumn<>("Due Date");
        hDue.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDate()));

        historyTable.getColumns().addAll(hTitle, hAuthor, hStatus, hDue);
        historyTab.setContent(historyTable);

        tabPane.getTabs().addAll(currentTab, historyTab);

        Scene scene = new Scene(tabPane, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
