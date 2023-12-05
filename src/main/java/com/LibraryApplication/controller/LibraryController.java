package com.LibraryApplication.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LibraryApplication.entity.Book;
import com.LibraryApplication.entity.User;
import com.LibraryApplication.repository.UserRepository;
import com.LibraryApplication.service.LibraryService;

import ch.qos.logback.classic.Logger;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
	
	private final LibraryService libraryService;
    private final UserRepository userRepository;

    @Autowired
    public LibraryController(LibraryService libraryService, UserRepository userRepository) {
        this.libraryService = libraryService;
        this.userRepository = userRepository;
    }
    
    private final Logger logger = (Logger) LoggerFactory.getLogger(LibraryController.class);

    @GetMapping("/users-with-borrowed-books")
    public ResponseEntity<List<User>> getUsersWithBorrowedBooks() {
    	logger.info("inside getUsersWithBorrowedBooks controller");
        List<User> users = libraryService.getUsersWithBorrowedBooks();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/non-terminated-users-no-current-books")
    public ResponseEntity<List<User>> getNonTerminatedUsersWithNoCurrentBooks() {
        List<User> users = libraryService.getNonTerminatedUsersWithNoCurrentBooks();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users-borrowed-on-date")
    public ResponseEntity<?> getUsersBorrowedOnDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<User> users = libraryService.getUsersWithBorrowedBooksOnDate(date);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid date format. Please provide a date in ISO format (yyyy-MM-dd).", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/books-borrowed-by-user-in-date-range")
    public ResponseEntity<List<Book>> getBooksBorrowedByUserInDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Book> books = libraryService.getBooksBorrowedByUserInDateRange(user, from, to);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/available-books")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> books = libraryService.getAvailableBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
