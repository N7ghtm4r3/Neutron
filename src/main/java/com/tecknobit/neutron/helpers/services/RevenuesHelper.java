package com.tecknobit.neutron.helpers.services;

import com.tecknobit.neutron.helpers.services.repositories.RevenuesRepository;
import com.tecknobit.neutroncore.records.revenues.Revenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RevenuesHelper {

    @Autowired
    private final RevenuesRepository revenuesRepository;

    public RevenuesHelper(RevenuesRepository revenuesRepository) {
        this.revenuesRepository = revenuesRepository;
    }

    public List<Revenue> getRevenues(String userId) {
        ArrayList<Revenue> revenues = new ArrayList<>(revenuesRepository.getGeneralRevenues(userId));
        revenues.addAll(revenuesRepository.getProjectRevenues(userId));
        revenues.sort((o1, o2) -> Long.compare(o2.getRevenueTimestamp(), o1.getRevenueTimestamp()));
        return revenues;
    }

}
