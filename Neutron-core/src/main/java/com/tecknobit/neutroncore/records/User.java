package com.tecknobit.neutroncore.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tecknobit.apimanager.annotations.Returner;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import static com.tecknobit.neutroncore.records.User.ApplicationTheme.Auto;
import static com.tecknobit.neutroncore.records.User.NeutronCurrency.DOLLAR;
import static com.tecknobit.neutroncore.records.User.USERS_KEY;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code User} class is useful to represent a Neutron's user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 */
@Entity
@Table(name = USERS_KEY)
public class User extends NeutronItem {

    /**
     * {@code USERS_KEY} the key for the <b>"users"</b> field
     */
    public static final String USERS_KEY = "users";

    /**
     * {@code TOKEN_KEY} the key for the <b>"token"</b> field
     */
    public static final String TOKEN_KEY = "token";

    /**
     * {@code NAME_KEY} the key for the <b>"name"</b> field
     */
    public static final String NAME_KEY = "name";

    /**
     * {@code SURNAME_KEY} the key for the <b>"surname"</b> field
     */
    public static final String SURNAME_KEY = "surname";

    /**
     * {@code EMAIL_KEY} the key for the <b>"email"</b> field
     */
    public static final String EMAIL_KEY = "email";

    /**
     * {@code PASSWORD_KEY} the key for the <b>"password"</b> field
     */
    public static final String PASSWORD_KEY = "password";

    /**
     * {@code PROFILE_PIC_KEY} the key for the <b>"profile_pic"</b> field
     */
    public static final String PROFILE_PIC_KEY = "profile_pic";

    /**
     * {@code OWNER_KEY} the key for the <b>"owner"</b> field
     */
    public static final String OWNER_KEY = "owner";

    /**
     * {@code DEFAULT_PROFILE_PIC} the default profile pic path when the user has not set own image
     */
    //TODO: SET THE REAL DEFAULT PROFILE PIC
    public static final String DEFAULT_PROFILE_PIC = "profiles/defProfilePic.jpg";

    /**
     * {@code LANGUAGE_KEY} the key for the <b>"language"</b> field
     */
    public static final String LANGUAGE_KEY = "language";

    /**
     * {@code CURRENCY_KEY} the key for the <b>"currency"</b> field
     */
    public static final String CURRENCY_KEY = "currency";

    /**
     * {@code THEME_KEY} the key for the <b>"theme"</b> field
     */
    public static final String THEME_KEY = "theme";

    /**
     * {@code ApplicationTheme} list of the available theming for the client applications
     */
    public enum ApplicationTheme {

        /**
         * {@code Dark} the dark theme to use as theme
         */
        Dark,

        /**
         * {@code Light} the light theme to use as theme
         */
        Light,

        /**
         * {@code Auto} the theme to use based on the user current theme set
         */
        Auto;

        /**
         * Method to get an instance of the {@link ApplicationTheme}
         *
         * @param theme: the name of the theme to get
         * @return the theme instance as {@link ApplicationTheme}
         */
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

    /**
     * {@code NeutronCurrency} list of the available fiat currencies supported
     */
    public enum NeutronCurrency {

        /**
         * {@code EURO} fiat currency
         */
        EURO("EUR", "Euro", Currency.getInstance(Locale.ITALY).getSymbol()),

        /**
         * {@code DOLLAR} fiat currency
         */
        DOLLAR("USD","US Dollar", Currency.getInstance(Locale.US).getSymbol()),

        /**
         * {@code POUND_STERLING} fiat currency
         */
        POUND_STERLING("GBP", "Pound sterling", Currency.getInstance(Locale.UK).getSymbol()),

        /**
         * {@code JAPANESE_YEN} fiat currency
         */
        JAPANESE_YEN("JPY", "Japanese Yen", Currency.getInstance(Locale.JAPAN).getSymbol()),

        /**
         * {@code CHINESE_YEN} fiat currency
         */
        CHINESE_YEN("CNY", "Chinese Yuan", Currency.getInstance(Locale.CHINA).getSymbol());

        /**
         * {@code isoCode} the iso code, 3 letters, of the currency
         */
        private final String isoCode;

        /**
         * {@code isoCode} the iso name of the currency
         */
        private final String isoName;

        /**
         * {@code symbol} the symbol of the currency
         */
        private final String symbol;

        /**
         * Constructor to init {@link NeutronCurrency}
         * @param isoCode: the iso code, 3 letters, of the currency
         * @param isoName:{@code isoCode} the iso name of the currency
         * @param symbol: the symbol of the currency
         */
        NeutronCurrency(String isoCode, String isoName, String symbol) {
            this.isoCode = isoCode;
            this.isoName = isoName;
            this.symbol = symbol;
        }

        /**
         * Method to get {@link #isoCode} instance <br>
         * No-any params required
         *
         * @return {@link #isoCode} instance as {@link String}
         */
        public String getIsoCode() {
            return isoCode;
        }

        /**
         * Method to get {@link #isoName} instance <br>
         * No-any params required
         *
         * @return {@link #isoName} instance as {@link String}
         */
        public String getIsoName() {
            return isoName;
        }

        /**
         * Method to get {@link #symbol} instance <br>
         * No-any params required
         *
         * @return {@link #symbol} instance as {@link String}
         */
        public String getSymbol() {
            return symbol;
        }

        /**
         * Method to get an instance of the {@link NeutronCurrency}
         *
         * @param currencyName: the name of the currency to get
         * @return the currency instance as {@link NeutronCurrency}
         */
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

    /**
     * {@code CURRENCIES_SUPPORTED}
     */
    @Transient
    public static final HashMap<String, String> CURRENCIES_SUPPORTED = new HashMap<>();

    static {
        CURRENCIES_SUPPORTED.put("it", "Italiano");
        CURRENCIES_SUPPORTED.put("en", "English");
        CURRENCIES_SUPPORTED.put("fr", "Francais");
        CURRENCIES_SUPPORTED.put("es", "Espanol");
    }

    /**
     * {@code token} the token which the user is allowed to operate on server
     */
    @Column(
            name = TOKEN_KEY,
            columnDefinition = "VARCHAR(32) NOT NULL",
            unique = true
    )
    private final String token;

    /**
     * {@code name} the name of the user
     */
    @Column(
            name = NAME_KEY,
            columnDefinition = "VARCHAR(20) NOT NULL"
    )
    private final String name;

    /**
     * {@code surname} the surname of the user
     */
    @Column(
            name = SURNAME_KEY,
            columnDefinition = "VARCHAR(30) NOT NULL"
    )
    private final String surname;

    /**
     * {@code email} the email of the user
     */
    @Column(
            name = EMAIL_KEY,
            columnDefinition = "VARCHAR(75) NOT NULL",
            unique = true
    )
    private final String email;

    /**
     * {@code password} the password of the user
     */
    @Column(
            name = PASSWORD_KEY,
            nullable = false
    )
    @JsonIgnore
    private final String password;

    /**
     * {@code profilePic} the profile pic of the user
     */
    @Column(
            name = PROFILE_PIC_KEY,
            columnDefinition = "TEXT DEFAULT '" + DEFAULT_PROFILE_PIC + "'",
            insertable = false
    )
    private final String profilePic;

    /**
     * {@code language} the language of the user
     */
    @Column(
            name = LANGUAGE_KEY,
            columnDefinition = "VARCHAR(2) NOT NULL"
    )
    private final String language;

    /**
     * {@code currency} the currency of the user
     */
    @Enumerated(value = STRING)
    @Column(name = CURRENCY_KEY)
    private final NeutronCurrency currency;

    /**
     * {@code theme} the theme of the user
     */
    @Transient
    private final ApplicationTheme theme;

    /**
     * Constructor to init the {@link User} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public User() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * Constructor to init the {@link User} class
     *
     * @param id: identifier of the user
     * @param token: the token which the user is allowed to operate on server
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     *
     */
    public User(String id, String token, String name, String surname, String email, String password, String language) {
        this(id, token, name, surname, email, password, DEFAULT_PROFILE_PIC, language, DOLLAR, Auto);
    }

    /**
     * Constructor to init the {@link User} class
     *
     * @param id: identifier of the user
     * @param token: the token which the user is allowed to operate on server
     * @param name: the password of the user
     * @param surname: the surname of the user
     * @param email: the password of the user
     * @param password: the password of the user
     * @param profilePic: the profile pic of the user
     * @param language: the password of the user
     * @param currency: the currency of the user
     * @param theme: the theme of the user
     *
     */
    public User(String id, String token, String name, String surname, String email, String password, String profilePic,
                String language, NeutronCurrency currency, ApplicationTheme theme) {
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
    }

    /**
     * Constructor to init the {@link User} class
     *
     * @param jUser: user details formatted as JSON
     *
     */
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
    }

    /**
     * Method to get {@link #token} instance <br>
     * No-any params required
     *
     * @return {@link #token} instance as {@link String}
     */
    public String getToken() {
        return token;
    }

    /**
     * Method to get {@link #name} instance <br>
     * No-any params required
     *
     * @return {@link #name} instance as {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get {@link #surname} instance <br>
     * No-any params required
     *
     * @return {@link #surname} instance as {@link String}
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Method to get the complete name of the user <br>
     * No-any params required
     *
     * @return the complete name of the user as {@link String}
     */
    @JsonIgnore
    public String getCompleteName() {
        return name + " " + surname;
    }

    /**
     * Method to get {@link #email} instance <br>
     * No-any params required
     *
     * @return {@link #email} instance as {@link String}
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method to get {@link #password} instance <br>
     * No-any params required
     *
     * @return {@link #password} instance as {@link String}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method to get {@link #profilePic} instance <br>
     * No-any params required
     *
     * @return {@link #profilePic} instance as {@link String}
     */
    @JsonProperty(PROFILE_PIC_KEY)
    public String getProfilePic() {
        return profilePic;
    }

    /**
     * Method to get {@link #language} instance <br>
     * No-any params required
     *
     * @return {@link #language} instance as {@link String}
     */
    public String getLanguage() {
        return language;
    }
    /**
     * Method to get {@link #currency} instance <br>
     * No-any params required
     *
     * @return {@link #currency} instance as {@link NeutronCurrency}
     */
    public NeutronCurrency getCurrency() {
        return currency;
    }

    /**
     * Method to get {@link #name} instance <br>
     * No-any params required
     *
     * @return {@link #name} instance as {@link ApplicationTheme}
     */
    public ApplicationTheme getTheme() {
        return theme;
    }

    /**
     * Method to assemble and return a {@link User} instance
     *
     * @param jUser: user details formatted as JSON
     *
     * @return the user instance as {@link User}
     */
    @Returner
    public static User getInstance(JSONObject jUser) {
        if (jUser != null)
            return new User(jUser);
        return null;
    }

}
