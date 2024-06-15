package com.tecknobit.neutron.helpers.services;

import com.tecknobit.neutron.helpers.services.repositories.UsersRepository;
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TransferHelper {

    private final RevenuesHelper revenuesHelper;

    @Autowired
    private final UsersRepository usersRepository;

    public TransferHelper(RevenuesHelper revenuesHelper, UsersRepository usersRepository) {
        this.revenuesHelper = revenuesHelper;
        this.usersRepository = usersRepository;
    }

    public void storeGeneralRevenues(ArrayList<GeneralRevenue> generalRevenues, String userId) {
        for (GeneralRevenue revenue : generalRevenues) {
            revenuesHelper.createGeneralRevenue(
                    revenue.getId(),
                    revenue.getValue(),
                    revenue.getTitle(),
                    revenue.getRevenueTimestamp(),
                    revenue.getDescription(),
                    revenue.getLabels(),
                    userId
            );
        }
    }

    public void storeProjectRevenues(ArrayList<ProjectRevenue> projectRevenues, String userId) {
        for (ProjectRevenue revenue : projectRevenues) {
            List<TicketRevenue> tickets = revenue.getTickets();
            String projectId = revenue.getId();
            revenuesHelper.createProjectRevenue(
                    projectId,
                    revenue.getInitialRevenue().getValue(),
                    revenue.getTitle(),
                    revenue.getRevenueTimestamp(),
                    userId
            );
            for (TicketRevenue ticket : tickets)  {
                revenuesHelper.addTicketToProjectRevenue(
                        ticket.getId(),
                        ticket.getValue(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getRevenueTimestamp(),
                        ticket.getClosingTimestamp(),
                        projectId,
                        userId
                );
            }
        }
    }

    public void deleteAfterTransferred(String userId) {
        ExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    usersRepository.deleteAfterTransferred(userId);
                } catch (InterruptedException e) {
                    executor.execute(this);
                }
            }
        });
    }

}
