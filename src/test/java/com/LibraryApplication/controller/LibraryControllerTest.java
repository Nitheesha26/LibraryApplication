package com.LibraryApplication.controller;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.LibraryApplication.entity.Book;
import com.LibraryApplication.entity.User;
import com.LibraryApplication.repository.UserRepository;
import com.LibraryApplication.service.LibraryService;

@WebMvcTest(LibraryController.class)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getUsersWithBorrowedBooks() throws Exception {
        List<User> users = Arrays.asList(new User(), new User());
        when(libraryService.getUsersWithBorrowedBooks()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/library/users-with-borrowed-books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}, {}]"));
    }

    @Test
    void getNonTerminatedUsersWithNoCurrentBooks() throws Exception {
        List<User> users = Arrays.asList(new User(), new User());
        when(libraryService.getNonTerminatedUsersWithNoCurrentBooks()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/library/non-terminated-users-no-current-books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}, {}]"));
    }

    @Test
    void getUsersBorrowedOnDate() throws Exception {
        LocalDate date = LocalDate.now();
        List<User> users = Arrays.asList(new User(), new User());
        when(libraryService.getUsersWithBorrowedBooksOnDate(date)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/library/users-borrowed-on-date")
                .param("date", date.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}, {}]"));
    }

    @Test
    void getBooksBorrowedByUserInDateRange() throws Exception {
        Long userId = 1L;
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        User user = new User();
        List<Book> books = Arrays.asList(new Book(), new Book());

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(libraryService.getBooksBorrowedByUserInDateRange(user, from, to)).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/library/books-borrowed-by-user-in-date-range")
                .param("userId", userId.toString())
                .param("from", from.toString())
                .param("to", to.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}, {}]"));
    }

    @Test
    void getAvailableBooks() throws Exception {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(libraryService.getAvailableBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/library/available-books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[{}, {}]"));
    }
}