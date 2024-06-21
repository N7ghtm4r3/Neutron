package com.tecknobit.neutroncore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import static com.tecknobit.neutroncore.records.User.ApplicationTheme.Auto;
import static com.tecknobit.neutroncore.records.User.NeutronCurrency.DOLLAR;
import static com.tecknobit.neutroncore.records.User.USERS_KEY;
import static com.tecknobit.neutroncore.records.User.UserStorage.Online;
import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = USERS_KEY)
public class User extends NeutronItem implements Transferable {

    //TODO: CHECK TO MOVE
    public static final String USERS_KEY = "users";

    public static final String TOKEN_KEY = "token";

    public static final String NAME_KEY = "name";

    public static final String SURNAME_KEY = "surname";

    public static final String EMAIL_KEY = "email";

    public static final String PASSWORD_KEY = "password";

    public static final String PROFILE_PIC_KEY = "profile_pic";

    public static final String OWNER_KEY = "owner";

    //TODO: SET THE REAL DEFAULT PROFILE PIC
    public static final String DEFAULT_PROFILE_PIC = "profiles/defProfilePic.jpg";

    public static final String LANGUAGE_KEY = "language";

    public static final String CURRENCY_KEY = "currency";

    public static final String THEME_KEY = "theme";

    public static final String USER_STORAGE_KEY = "user_storage";

    public enum ApplicationTheme {

        Dark,

        Light,

        Auto;

        public static ApplicationTheme getInstance(String theme) {
            if(theme == null)
                return Auto;
            return switch (theme) {
                case "Dark" -> Dark;
                case "Light" -> Light;
                default -> Auto;
            };
        }

    }

    public enum UserStorage {

        Local,

        Online

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

        public static NeutronCurrency getInstance(String currencyName) {
            if(currencyName == null)
                return DOLLAR;
            return switch (currencyName) {
                case "EURO" -> EURO;
                case "POUND_STERLING" -> POUND_STERLING;
                case "JAPANESE_YEN" -> JAPANESE_YEN;
                case "CHINESE_YEN" -> CHINESE_YEN;
                default -> DOLLAR;
            };
        }

    }

    @Transient
    public static final HashMap<String, String> CURRENCIES_SUPPORTED = new HashMap<>();

    static {
        CURRENCIES_SUPPORTED.put("EUR", "Italiano");
        CURRENCIES_SUPPORTED.put("en", "English");
        CURRENCIES_SUPPORTED.put("fr", "Francais");
        CURRENCIES_SUPPORTED.put("es", "Espanol");
    }

    @Column(
            name = TOKEN_KEY,
            columnDefinition = "VARCHAR(32) NOT NULL",
            unique = true
    )
    private final String token;

    @Column(
            name = NAME_KEY,
            columnDefinition = "VARCHAR(20) NOT NULL"
    )
    private final String name;

    @Column(
            name = SURNAME_KEY,
            columnDefinition = "VARCHAR(30) NOT NULL"
    )
    private final String surname;

    @Column(
            name = EMAIL_KEY,
            columnDefinition = "VARCHAR(75) NOT NULL",
            unique = true
    )
    private String email;

    @Column(
            name = PASSWORD_KEY,
            nullable = false
    )
    @JsonIgnore
    private String password;

    @Column(
            name = PROFILE_PIC_KEY,
            columnDefinition = "TEXT DEFAULT '" + DEFAULT_PROFILE_PIC + "'",
            insertable = false
    )
    // TODO: CHECK TO SET AS FINAL
    private String profilePic;

    @Column(
            name = LANGUAGE_KEY,
            columnDefinition = "VARCHAR(2) NOT NULL"
    )
    // TODO: CHECK TO SET AS FINAL
    private String language;

    @Enumerated(value = STRING)
    @Column(name = CURRENCY_KEY)
    // TODO: CHECK TO SET AS FINAL
    private NeutronCurrency currency;

    @Transient
    // TODO: CHECK TO SET AS FINAL
    private ApplicationTheme theme;

    @Transient
    // TODO: CHECK TO SET AS FINAL
    private UserStorage storage;

    public User() {
        this(null, null, null, null, null, null, null);
    }

    public User(String id, String token, String name, String surname, String email, String password, String language) {
        this(id, token, name, surname, email, password, DEFAULT_PROFILE_PIC, language, DOLLAR, Auto, null);
    }

    public User(String id, String token, String name, String surname, String email, String password, String profilePic,
                String language, NeutronCurrency currency, ApplicationTheme theme, UserStorage storage) {
        super(id);
        this.token = token;
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

    public User(JSONObject jUser) {
        super(jUser);
        token = hItem.getString(TOKEN_KEY);
        name = hItem.getString(NAME_KEY);
        surname = hItem.getString(SURNAME_KEY);
        email = hItem.getString(EMAIL_KEY);
        password = hItem.getString(PASSWORD_KEY);
        profilePic = hItem.getString(PROFILE_PIC_KEY);
        currency = NeutronCurrency.getInstance(hItem.getString(CURRENCY_KEY));
        language = hItem.getString(LANGUAGE_KEY);
        theme = Auto;
        storage = Online;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @JsonIgnore
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

    @JsonProperty(PROFILE_PIC_KEY)
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

    public static User getInstance(JSONObject jUser) {
        if (jUser != null)
            return new User(jUser);
        return null;
    }

    @Override
    public JSONObject toTransferTarget() {
        return new JSONObject()
                .put(NAME_KEY, name)
                .put(SURNAME_KEY, surname)
                .put(EMAIL_KEY, email)
                .put(PASSWORD_KEY, password)
                .put(LANGUAGE_KEY, language);
    }

}
