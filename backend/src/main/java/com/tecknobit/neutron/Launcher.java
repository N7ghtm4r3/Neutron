package com.tecknobit.neutron;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesProvider.CUSTOM_CONFIGURATION_FILE_PATH;
import static com.tecknobit.equinoxbackend.resourcesutils.ResourcesProvider.DEFAULT_CONFIGURATION_FILE_PATH;

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
@ComponentScan(value = {"com.tecknobit.neutron.*", "com.tecknobit.equinoxbackend.environment.configuration"})
public class Launcher {

    /**
     * Main method to start the backend, will be created also the resources directories
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
    public static void main(String[] args) {
        EquinoxController.initEquinoxEnvironment(Launcher.class, args);
        SpringApplication.run(Launcher.class, args);
    }

}
