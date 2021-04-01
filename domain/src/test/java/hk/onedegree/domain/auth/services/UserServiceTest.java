package hk.onedegree.domain.auth.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository mockUserRepository;

    UserService userService;

    @BeforeEach
    public void setup(){
        userService = new UserService();
        userService.userRepository = this.mockUserRepository;
    }

    @Test
    public void createUser(){
        String email ="whatever@whatever.com";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        Optional<User> result = this.userService.createUser(email, password);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(email, result.get().getEmail());
        verify(this.mockUserRepository, times(1)).save(any());
    }

    @Test
    public void createUser_EamilExist_ReturnEmpty(@Mock User mockUser){
        String email ="whatever@whatever.com";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.of(mockUser));

        Optional<User> result = this.userService.createUser(email, password);

        Assertions.assertTrue(result.isEmpty());
        verify(this.mockUserRepository, times(0)).save(any());
    }

    @Test
    public void createUser_FailConstructUser_ReturnEmpty(@Mock User mockUser){
        String email ="whatever";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        Optional<User> result = this.userService.createUser(email, password);

        Assertions.assertTrue(result.isEmpty());
        verify(this.mockUserRepository, times(0)).save(any());
    }

    @Test
    public void createUser_FailSetPassword_ReturnEmpty(@Mock User mockUser){
        String email ="whatever@whatever.com";
        String password = "invalidPassword";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        Optional<User> result = this.userService.createUser(email, password);

        Assertions.assertTrue(result.isEmpty());
        verify(this.mockUserRepository, times(0)).save(any());
    }
}
