package com.tecknobit.neutron;

import com.tecknobit.apimanager.apis.ServerProtector;
import com.tecknobit.apimanager.exceptions.SaveData;
import com.tecknobit.equinox.ResourcesProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.tecknobit.equinox.ResourcesProvider.CUSTOM_CONFIGURATION_FILE_PATH;
import static com.tecknobit.equinox.ResourcesProvider.DEFAULT_CONFIGURATION_FILE_PATH;
import static com.tecknobit.neutron.helpers.services.RevenuesHelper.refreshCurrencyRates;

/**
 * The {@code Launcher} class is useful to launch <b>Neutron's backend service</b>
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@EnableAutoConfiguration
@SpringBootApplication
@PropertySources({
        @PropertySource(value = "classpath:" + DEFAULT_CONFIGURATION_FILE_PATH),
        @PropertySource(value = "file:" + CUSTOM_CONFIGURATION_FILE_PATH, ignoreResourceNotFound = true)
})
@EnableJpaRepositories("com.tecknobit.*")
@EntityScan("com.tecknobit.*")
public class Launcher {

    /**
     * {@code protector} the instance to launch the server protector to manage the server accesses
     *
     * @apiNote the commands scheme:
     * <ul>
     *     <li>
     *         <b>rss</b> -> launch your java application with "rss" to recreate the server secret <br>
     *                       e.g java -jar Neutron.jar rss
     *     </li>
     *     <li>
     *         <b>dss</b> -> launch your java application with "dss" to delete the current server secret <br>
     *                       e.g java -jar Neutron.jar dss
     *     </li>
     *     <li>
     *         <b>dssi</b> -> launch your java application with "dssi" to delete the current server secret and interrupt
     *                        the current workflow of the server <br>
     *                        e.g java -jar Neutron.jar dssi
     *     </li>
     * </ul>
     */
    public static final ServerProtector serverProtector = new ServerProtector(
            "tecknobit/neutron/backend",
            " to correctly register a new user in the Neutron system "
    );

    /**
     * Main method to start the backend, will be created also the resources directories if not exist invoking the
     * {@link ResourcesProvider} routine
     *
     * @param args: custom arguments to share with {@link SpringApplication} and with the {@link #serverProtector}
     * @apiNote the arguments scheme:
     * <ul>
     *     <li>
     *         {@link #serverProtector} ->
     *         <ul>
     *          <li>
     *             <b>rss</b> -> launch your java application with "rss" to recreate the server secret <br>
     *                       e.g java -jar Nova.jar rss
     *             </li>
     *              <li>
     *                  <b>dss</b> -> launch your java application with "dss" to delete the current server secret <br>
     *                       e.g java -jar Nova.jar dss
     *              </li>
     *              <li>
     *                  <b>dssi</b> -> launch your java application with "dssi" to delete the current server secret and interrupt
     *                        the current workflow of the server <br>
     *                        e.g java -jar Nova.jar dssi
     *              </li>
     *          </ul>
     *     </li>
     *     <li>
     *         {@link SpringApplication} -> see the allowed arguments <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html">here</a>
     *     </li>
     * </ul>
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, SaveData {
        ResourcesProvider resourcesProvider = new ResourcesProvider("resources", List.of("profiles"));
        resourcesProvider.createContainerDirectory();
        resourcesProvider.createSubDirectories();
        refreshCurrencyRates();
        serverProtector.launch(args);
        SpringApplication.run(Launcher.class, args);
    }

    /**
     * The {@code CORSAdvice} class is useful to set the CORS policy
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    @Configuration
    public static class CORSAdvice {

        /**
         * Method to set the CORS filter <br>
         * No any-params required
         */
        @Bean
        public FilterRegistrationBean corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(false);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", config);
            FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
            bean.setOrder(0);
            return bean;
        }

    }

}
