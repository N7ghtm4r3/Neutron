package com.tecknobit.neutroncore.records;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

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

    public enum NeutronCurrency {

        EURO("EUR", "Euro", Currency.getInstance(Locale.ITALY).getSymbol()),

        DOLLAR("USD","US Dollar",Currency.getInstance(Locale.US).getSymbol()),

        POUND_STERLING("GBP", "Pound sterling", Currency.getInstance(Locale.UK).getSymbol()),

        JAPANESE_YEN("JPY", "Japanese Yen", Currency.getInstance(Locale.JAPAN).getSymbol()),

        CHINESE_YEN("CNY", "Chinese Yuan", Currency.getInstance(Locale.CHINA).getSymbol());

        private final String isoCode;

        private final String isoName;

        private final String symbol;

        NeutronCurrency(String isoCode, String isoName, String symbol) {
            this.isoCode = isoCode;
            this.isoName = isoName;
            this.symbol = symbol;
        }

        public String getIsoCode() {
            return isoCode;
        }

        public String getIsoName() {
            return isoName;
        }

        public String getSymbol() {
            return symbol;
        }

    }

    public static final HashMap<String, String> CURRENCIES_SUPPORTED = new HashMap<>();

    static {
        CURRENCIES_SUPPORTED.put("EUR", "Italiano");
        CURRENCIES_SUPPORTED.put("en", "English");
        CURRENCIES_SUPPORTED.put("fr", "Francais");
        CURRENCIES_SUPPORTED.put("es", "Espanol");
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
    private NeutronCurrency currency;

    // TODO: CHECK TO SET AS FINAL
    private ApplicationTheme theme;

    // TODO: CHECK TO SET AS FINAL
    private UserStorage storage;

    // TODO: TO REMOVE
    public User() {
        this("id", "User", "Name", "user.name@gmail.com", "password",
                "https://res.cloudinary.com/momentum-media-group-pty-ltd/image/upload/v1686795211/Space%20Connect/space-exploration-sc_fm1ysf.jpg",
                "it", NeutronCurrency.EURO, ApplicationTheme.Auto, UserStorage.Online);
    }

    public User(String id, String name, String surname, String email, String password, String profilePic, String language,
                NeutronCurrency currency, ApplicationTheme theme, UserStorage storage) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
        this.currency = currency;
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

    public NeutronCurrency getCurrency() {
        return currency;
    }

    // TODO: TO REMOVE
    public void setCurrency(NeutronCurrency currency) {
        this.currency = currency;
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
