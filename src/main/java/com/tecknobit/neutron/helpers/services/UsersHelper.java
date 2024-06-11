package com.tecknobit.neutron.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.neutron.helpers.resources.ResourcesManager;
import com.tecknobit.neutron.helpers.services.repositories.UsersRepository;
import com.tecknobit.neutroncore.records.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM;
import static java.lang.System.currentTimeMillis;

@Service
public class UsersHelper implements ResourcesManager {

    @Autowired
    private final UsersRepository usersRepository;

    public UsersHelper(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Method to sign up a new user in the Nova's system
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     */
    public void signUpUser(String id, String token, String name, String surname, String email, String password,
                           String language) throws NoSuchAlgorithmException {
        System.out.println(token.length());
        usersRepository.save(new User(
                id,
                token,
                name,
                surname,
                email,
                hash(password),
                language
        ));
    }

    /**
     * Method to sign in an existing user
     *
     * @param email: the email of the user
     * @param password: the password of the user
     *
     * @return the authenticated user as {@link User} if the credentials inserted were correct
     */
    public User signInUser(String email, String password) throws NoSuchAlgorithmException {
        User user = usersRepository.findUserByEmail(email);
        if(user != null && user.getPassword().equals(hash(password)))
            return user;
        return null;
    }

    /**
     * Method to change the profile pic of the {@link User}
     *
     * @param profilePic: the profile pic resource
     * @param userId: the identifier of the user
     */
    public String changeProfilePic(MultipartFile profilePic, String userId) throws IOException {
        String profilePicPath = createProfileResource(profilePic, userId + currentTimeMillis());
        usersRepository.changeProfilePic(profilePicPath, userId);
        deleteProfileResource(userId);
        saveResource(profilePic, profilePicPath);
        return profilePicPath;
    }

    /**
     * Method to change the email of the {@link User}
     *
     * @param newEmail: the new email of the user
     * @param userId: the identifier of the user
     */
    public void changeEmail(String newEmail, String userId) {
        usersRepository.changeEmail(newEmail, userId);
    }

    /**
     * Method to change the password of the {@link User}
     *
     * @param newPassword: the new password of the user
     * @param userId: the identifier of the user
     */
    public void changePassword(String newPassword, String userId) throws NoSuchAlgorithmException {
        usersRepository.changePassword(hash(newPassword), userId);
    }

    /**
     * Method to change the language of the {@link User}
     *
     * @param newLanguage: the new language of the user
     * @param userId: the identifier of the user
     */
    public void changeLanguage(String newLanguage, String userId) {
        usersRepository.changeLanguage(newLanguage, userId);
    }

    /**
     * Method to delete a user
     * @param id: the identifier of the user to delete
     */
    public void deleteUser(String id) {
        usersRepository.deleteById(id);
        deleteProfileResource(id);
    }

    /**
     * Method to hash a sensitive user data
     *
     * @param secret: the user value to hash
     * @throws NoSuchAlgorithmException when the hash of the user value fails
     */
    private String hash(String secret) throws NoSuchAlgorithmException {
        return APIRequest.base64Digest(secret, SHA256_ALGORITHM);
    }

}
