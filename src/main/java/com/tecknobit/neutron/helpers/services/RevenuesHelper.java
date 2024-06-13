package com.tecknobit.neutron.helpers.services;

import com.tecknobit.neutron.helpers.services.repositories.revenues.RevenueLabelsRepository;
import com.tecknobit.neutron.helpers.services.repositories.revenues.RevenuesRepository;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.Revenue;
import com.tecknobit.neutroncore.records.revenues.RevenueLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutron.controllers.NeutronController.generateIdentifier;

@Service
public class RevenuesHelper {

    @Autowired
    private final RevenuesRepository revenuesRepository;

    @Autowired
    private final RevenueLabelsRepository labelsRepository;

    public RevenuesHelper(RevenuesRepository revenuesRepository, RevenueLabelsRepository labelsRepository) {
        this.revenuesRepository = revenuesRepository;
        this.labelsRepository = labelsRepository;
    }

    public List<Revenue> getRevenues(String userId) {
        ArrayList<Revenue> revenues = new ArrayList<>(revenuesRepository.getGeneralRevenues(userId));
        revenues.addAll(revenuesRepository.getProjectRevenues(userId));
        revenues.sort((o1, o2) -> Long.compare(o2.getRevenueTimestamp(), o1.getRevenueTimestamp()));
        return revenues;
    }

    public boolean revenueExists(String userId, String revenueTitle) {
        return revenuesRepository.projectRevenueExists(userId, revenueTitle) != null ||
                revenuesRepository.generalRevenueExists(userId, revenueTitle) != null;
    }

    public boolean revenueExistsById(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId) != null ||
                revenuesRepository.generalRevenueExistsById(userId, revenueId) != null;
    }

    public void createProjectRevenue(String projectRevenueId, double revenueValue, String revenueTitle, long insertionDate,
                                     String userId) {
        revenuesRepository.insertProjectRevenue(
                projectRevenueId,
                revenueTitle,
                insertionDate,
                userId
        );
        revenuesRepository.insertInitialRevenue(
                generateIdentifier(),
                insertionDate,
                revenueTitle,
                revenueValue,
                userId,
                projectRevenueId
        );
    }

    public void createGeneralRevenue(String revenueId, double revenueValue, String revenueTitle, long insertionDate,
                                     String revenueDescription, ArrayList<RevenueLabel> labels, String userId) {
        revenuesRepository.insertGeneralRevenue(
                revenueId,
                insertionDate,
                revenueValue,
                revenueDescription,
                userId
        );
        for (RevenueLabel label : labels) {
            labelsRepository.insertRevenueLabel(
                generateIdentifier(),
                label.getColor(),
                label.getText(),
                revenueId
            );
        }
    }

    public ProjectRevenue getProjectRevenue(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId);
    }

    public boolean deleteRevenue(String userId, String revenueId) {
        if(getProjectRevenue(userId, revenueId) != null) {
            revenuesRepository.deleteProjectRevenue(revenueId, userId);
            return true;
        } else if(revenuesRepository.generalRevenueExistsById(userId, revenueId) != null) {
            revenuesRepository.deleteGeneralRevenue(revenueId, userId);
            return true;
        }
        return false;
    }

}
