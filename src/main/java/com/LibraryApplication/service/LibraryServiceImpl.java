package com.LibraryApplication.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.LibraryApplication.controller.LibraryController;
import com.LibraryApplication.entity.Book;
import com.LibraryApplication.entity.BorrowedBook;
import com.LibraryApplication.entity.User;
import com.LibraryApplication.repository.BookRepository;
import com.LibraryApplication.repository.BorrowedBookRepository;
import com.LibraryApplication.repository.UserRepository;
import com.LibraryApplication.utility.CsvParser;

import ch.qos.logback.classic.Logger;
import jakarta.annotation.PostConstruct;

@Service
public class LibraryServiceImpl implements LibraryService {
	
	private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BorrowedBookRepository borrowedBookRepository;

    @Autowired
    public LibraryServiceImpl(
            UserRepository userRepository,
            BookRepository bookRepository,
            BorrowedBookRepository borrowedBookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.borrowedBookRepository = borrowedBookRepository;
    }
    
    @Value("${csv.books.path}")
    private String booksCsvPath;

    @Value("${csv.user.path}")
    private String usersCsvPath;

    @Value("${csv.borrowed.path}")
    private String borrowedCsvPath;
    
    private final Logger logger = (Logger) LoggerFactory.getLogger(LibraryServiceImpl.class);

    @Override
    public List<User> getUsersWithBorrowedBooks() {
        List<User> usersWithBorrowedBooks = userRepository.findAll()
                .stream()
                .filter(user -> !getBorrowedBooks(user).isEmpty())
                .peek(user -> logger.info("User {} has {} borrowed books.", user.getName(), getBorrowedBooks(user).size()))
                .collect(Collectors.toList());

        logger.info("Total users with borrowed books: {}", usersWithBorrowedBooks.size());
        return usersWithBorrowedBooks;
    }

	private List<BorrowedBook> getBorrowedBooks(User user) {
		return borrowedBookRepository.findByBorrower(user);
	}

	@Override
	public List<User> getNonTerminatedUsersWithNoCurrentBooks() {
		LocalDate today = LocalDate.now();
        return userRepository.findByMemberTillIsNull().stream()
                .filter(user -> getBorrowedBooks(user).isEmpty())
                .collect(Collectors.toList());
	}

	@Override
	public List<User> getUsersWithBorrowedBooksOnDate(LocalDate date) {
		return borrowedBookRepository.findByBorrowedFrom(date).stream()
                .map(BorrowedBook::getBorrower)
                .collect(Collectors.toList());
	}

	@Override
	public List<Book> getBooksBorrowedByUserInDateRange(User user, LocalDate from, LocalDate to) {
		return borrowedBookRepository.findByBorrowerAndBorrowedFromBetween(user, from, to).stream()
                .map(BorrowedBook::getBook)
                .collect(Collectors.toList());
	}

	@Override
	public List<Book> getAvailableBooks() {
		List<Book> allBooks = bookRepository.findAll();
        List<Book> borrowedBooks = borrowedBookRepository.findAll().stream()
                .map(BorrowedBook::getBook)
                .collect(Collectors.toList());

        return allBooks.stream()
                .filter(book -> !borrowedBooks.contains(book))
                .collect(Collectors.toList());
	}
	
	@Override
    public void saveBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        borrowedBookRepository.saveAll(borrowedBooks);
    }
	
	@PostConstruct
	public void initDataFromCsv() {
	    try {
	        List<Book> books = CsvParser.parseBooks(booksCsvPath);
	        List<User> users = CsvParser.parseUsers(usersCsvPath);
	        List<BorrowedBook> borrowedBooks = CsvParser.parseBorrowedBooks(borrowedCsvPath, users, books);

	        bookRepository.saveAll(books);
	        userRepository.saveAll(users);
	        saveBorrowedBooks(borrowedBooks);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
