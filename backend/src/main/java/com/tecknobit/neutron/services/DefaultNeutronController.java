package com.tecknobit.neutron.services;

import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutron.services.users.service.NeutronUsersService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class DefaultNeutronController extends EquinoxController<NeutronUser, NeutronUsersRepository, NeutronUsersService> {
}
