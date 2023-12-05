package com.LibraryApplication.utility;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.LibraryApplication.entity.Book;
import com.LibraryApplication.entity.BorrowedBook;
import com.LibraryApplication.entity.User;

public class CsvParser {

    public static List<Book> parseBooks(String filePath) throws IOException {
        List<Book> books = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : parser) {
                Book book = new Book();
                book.setTitle(record.get("Title"));
                book.setAuthor(record.get("Author"));
                book.setGenre(record.get("Genre"));
                book.setPublisher(record.get("Publisher"));
                books.add(book);
            }
        }
        return books;
    }

    public static List<User> parseUsers(String filePath) throws IOException {
        List<User> users = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : parser) {
                User user = new User();
                user.setName(record.get("Name"));
                user.setFirstName(record.get("First name"));
                try {
                    user.setMemberSince(LocalDate.parse(record.get("Member since"), DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                } catch (DateTimeParseException e1) {                
                    user.setMemberSince(LocalDate.parse(record.get("Member since"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }

                String memberTill = record.get("Member till");
                if (memberTill != null && !memberTill.isEmpty()) {
                    try {
                        user.setMemberTill(LocalDate.parse(memberTill, DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                    } catch (DateTimeParseException e2) {
                        user.setMemberTill(LocalDate.parse(memberTill, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }
                }
                user.setGender(record.get("Gender"));
                users.add(user);
            }
        }
        return users;
    }

    public static List<BorrowedBook> parseBorrowedBooks(String filePath, List<User> allUsers, List<Book> allBooks) throws IOException {
        List<BorrowedBook> borrowedBooks = new ArrayList<>();
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withHeader())) {
            for (CSVRecord record : parser) {
                BorrowedBook borrowedBook = new BorrowedBook();
                String borrowerName = record.get("Borrower");
                User borrower = findUserByName(allUsers, borrowerName);
                borrowedBook.setBorrower(borrower);

                String bookTitle = record.get("Book");
                Book book = findBookByTitle(allBooks, bookTitle);
                borrowedBook.setBook(book);
                
                LocalDate borrowedFrom = LocalDate.parse(record.get("borrowed from"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                LocalDate borrowedTo = LocalDate.parse(record.get("borrowed to"), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                
                borrowedBook.setBorrowedFrom(borrowedFrom);
                borrowedBook.setBorrowedTo(borrowedTo);

                borrowedBooks.add(borrowedBook);
            }
        }
        return borrowedBooks;
    }

    private static User findUserByName(List<User> allUsers, String name) {
        return allUsers.stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private static Book findBookByTitle(List<Book> allBooks, String title) {
        return allBooks.stream()
                .filter(book -> book.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }
}
