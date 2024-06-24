package com.tecknobit.neutroncore.l;

public class LNeutronServer {

    public static final String LOCAL_DATABASE_NAME = "Neutron.db";

    private final LUserController userController;

    private final LRevenuesController revenuesController;

    public LNeutronServer(LUserController userController, LRevenuesController revenuesController) {
        this.userController = userController;
        this.revenuesController = revenuesController;
    }

    public LUserController getUserController() {
        return userController;
    }

    public LRevenuesController getRevenuesController() {
        return revenuesController;
    }

}
