package com.tecknobit.neutron.helpers.services;

import com.tecknobit.neutron.helpers.services.repositories.RevenuesRepository;
import com.tecknobit.neutroncore.records.revenues.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutron.controllers.NeutronController.generateIdentifier;

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

    public boolean projectRevenueNotExists(String userId, String revenueTitle) {
        return revenuesRepository.projectRevenueExists(userId, revenueTitle) == null;
    }

    public boolean generalRevenueNotExists(String userId, String revenueTitle) {
        return revenuesRepository.generalRevenueExists(userId, revenueTitle) == null;
    }

    public void createProjectRevenue(String id, double revenueValue, String revenueTitle, long insertionDate) {
        revenuesRepository.save(
            new ProjectRevenue(
                id,
                revenueTitle,
                insertionDate,
                new InitialRevenue(
                        generateIdentifier(),
                        revenueValue,
                        insertionDate
                )
            )
        );
    }

    public void createGeneralRevenue(String id, double revenueValue, String revenueTitle, long insertionDate,
                                     String revenueDescription, ArrayList<RevenueLabel> labels) {
        revenuesRepository.save(
            new GeneralRevenue(
                id,
                revenueTitle,
                revenueValue,
                insertionDate,
                labels,
                revenueDescription
            )
        );
    }

}
