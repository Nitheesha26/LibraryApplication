package com.LibraryApplication.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LibraryApplication.entity.BorrowedBook;
import com.LibraryApplication.entity.User;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long>{

	List<BorrowedBook> findByBorrower(User borrower);

	List<BorrowedBook> findByBorrowedFrom(LocalDate date);

	List<BorrowedBook> findByBorrowerAndBorrowedFromBetween(User user, LocalDate from, LocalDate to);

}
