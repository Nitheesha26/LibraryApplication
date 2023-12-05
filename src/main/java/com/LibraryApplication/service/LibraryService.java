package com.LibraryApplication.service;

import java.time.LocalDate;
import java.util.List;

import com.LibraryApplication.entity.Book;
import com.LibraryApplication.entity.BorrowedBook;
import com.LibraryApplication.entity.User;

public interface LibraryService {

	List<User> getUsersWithBorrowedBooks();
    List<User> getNonTerminatedUsersWithNoCurrentBooks();
    List<User> getUsersWithBorrowedBooksOnDate(LocalDate date);
    List<Book> getBooksBorrowedByUserInDateRange(User user, LocalDate from, LocalDate to);
    List<Book> getAvailableBooks();
    
    void saveBorrowedBooks(List<BorrowedBook> borrowedBooks);
	
}
