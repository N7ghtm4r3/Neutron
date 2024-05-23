package com.tecknobit.neutroncore.records;

public class User extends NeutronItem {

    public enum ApplicationTheme {

        Dark,

        Light,

        Auto

    }

    private final String name;

    private final String surname;

    private final String email;

    private final String password;

    private final String profilePic;

    private final String language;

    // TODO: CHECK TO SET AS FINAL
    private ApplicationTheme theme;

    // TODO: TO REMOVE
    public User() {
        this("id", "User", "Name", "user.name@gmail.com", "password",
                "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                "Italian", ApplicationTheme.Auto);
    }

    public User(String id, String name, String surname, String email, String password, String profilePic, String language,
                ApplicationTheme theme) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.language = language;
        this.theme = theme;
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

    public String getPassword() {
        return password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getLanguage() {
        return language;
    }

    public ApplicationTheme getTheme() {
        return theme;
    }

    // TODO: TO REMOVE
    public void setTheme(ApplicationTheme theme) {
        this.theme = theme;
    }

}
