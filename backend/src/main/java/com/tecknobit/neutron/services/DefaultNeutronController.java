package com.tecknobit.neutron.services;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutron.services.users.service.NeutronUsersService;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code DefaultNeutronController} class is useful to give the base behavior of the <b>Neutron's controllers</b>
 * with the default {@link EquinoxUser} class and for a greater clarity during your own implementation:
 *
 * <pre>
 * {@code
 *
 *     @RestController
 *     public SomeCustomController extends EquinoxController<EquinoxUser> {
 *          // rest of the class
 *     }
 *
 *     @RestController
 *     public SomeCustomControllerTwo extends EquinoxController<EquinoxUser> {
 *          // rest of the class
 *     }
 *
 *     // wrap the declaration for a better readability
 *     @RestController
 *     public SomeCustomController extends DefaultNeutronController {
 *          // rest of the class
 *     }
 *
 *      // wrap the declaration for a better readability
 *     @RestController
 *     public SomeCustomControllerTwo extends DefaultNeutronController {
 *          // rest of the class
 *     }
 * }
 * </pre>
 *
 * @author N7ghtm4r3 - Tecknobit
 * @since 1.0.1
 *
 * @see EquinoxController
 */
@RestController
public abstract class DefaultNeutronController extends EquinoxController<NeutronUser, NeutronUsersRepository, NeutronUsersService> {
}
