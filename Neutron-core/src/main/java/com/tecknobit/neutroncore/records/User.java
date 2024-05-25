package com.tecknobit.neutroncore.records;

import java.util.HashMap;

public class User extends NeutronItem {

    public enum ApplicationTheme {

        Dark,

        Light,

        Auto

    }

    public enum UserStorage {

        Local,

        Online

    }

    public static final HashMap<String, String> LANGUAGES_SUPPORTED = new HashMap<>();

    static {
        LANGUAGES_SUPPORTED.put("it", "Italiano");
        LANGUAGES_SUPPORTED.put("en", "English");
        LANGUAGES_SUPPORTED.put("fr", "Francais");
        LANGUAGES_SUPPORTED.put("es", "Espanol");
    }

    private final String name;

    private final String surname;

    private String email;

    private String password;

    // TODO: CHECK TO SET AS FINAL
    private String profilePic;

    // TODO: CHECK TO SET AS FINAL
    private String language;

    // TODO: CHECK TO SET AS FINAL
    private ApplicationTheme theme;

    // TODO: CHECK TO SET AS FINAL
    private UserStorage storage;

    // TODO: TO REMOVE
    public User() {
        this("id", "User", "Name", "user.name@gmail.com", "password",
                "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                "it", ApplicationTheme.Auto, UserStorage.Online);
    }

    public User(String id, String name, String surname, String email, String password, String profilePic, String language,
                ApplicationTheme theme, UserStorage storage) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.language = language;
        this.theme = theme;
        this.storage = storage;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCompleteName() {
        return name + " " + surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    // TODO: TO REMOVE
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ApplicationTheme getTheme() {
        return theme;
    }

    // TODO: TO REMOVE
    public void setTheme(ApplicationTheme theme) {
        this.theme = theme;
    }

    public UserStorage getStorage() {
        return storage;
    }

    // TODO: TO REMOVE
    public void setStorage(UserStorage storage) {
        this.storage = storage;
    }

}
