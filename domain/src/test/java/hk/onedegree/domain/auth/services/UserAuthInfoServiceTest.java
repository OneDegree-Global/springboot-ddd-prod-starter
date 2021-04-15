package hk.onedegree.domain.auth.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.DuplicatedEmailException;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.exceptions.RepositoryOperatorException;
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
public class UserAuthInfoServiceTest {

    @Mock
    UserRepository mockUserRepository;

    UserAuthInfoService userAuthInfoService;

    @BeforeEach
    public void setup(){
        userAuthInfoService = new UserAuthInfoService();
        userAuthInfoService.userRepository = this.mockUserRepository;
    }

    @Test
    public void createUser() throws InValidPasswordException, InValidEmailException, DuplicatedEmailException, RepositoryOperatorException {
        String email ="whatever@whatever.com";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        Optional<User> result = this.userAuthInfoService.createUser(email, password);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(email, result.get().getEmail());
        verify(this.mockUserRepository, times(1)).save(any());
    }

    @Test
    public void createUser_EamilExist_ThrowsDuplicatedEmailException(@Mock User mockUser) throws RepositoryOperatorException {
        String email ="whatever@whatever.com";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.of(mockUser));

        Assertions.assertThrows(DuplicatedEmailException.class, () -> {
            this.userAuthInfoService.createUser(email, password);
        });

        verify(this.mockUserRepository, times(0)).save(any());
    }

    @Test
    public void createUser_InvalidEmail_ThrowsInValidEmailException(@Mock User mockUser) throws RepositoryOperatorException {
        String email ="whatever";
        String password = "Abc1234567";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());
        Assertions.assertThrows(InValidEmailException.class, () -> {
            this.userAuthInfoService.createUser(email, password);
        });

        verify(this.mockUserRepository, times(0)).save(any());
    }

    @Test
    public void createUser_FailSetPassword_ThrowsInValidPasswordException(@Mock User mockUser) throws InValidPasswordException, InValidEmailException, DuplicatedEmailException, RepositoryOperatorException {
        String email ="whatever@whatever.com";
        String password = "invalidPassword";
        when(this.mockUserRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        Assertions.assertThrows(InValidPasswordException.class, () -> {
            this.userAuthInfoService.createUser(email, password);
        });

        verify(this.mockUserRepository, times(0)).save(any());
    }
}
