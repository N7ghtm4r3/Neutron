package com.tecknobit.neutroncore.records;

public class User extends NeutronItem {

    private final String name;

    private final String surname;

    private final String email;

    private final String password;

    private final String profilePic;

    private final String language;

    // TODO: TO REMOVE
    public User() {
        this("id", "User", "Name", "user.name@gmail.com", "password",
                "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                "Italian");
    }

    public User(String id, String name, String surname, String email, String password, String profilePic, String language) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.language = language;
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

}
