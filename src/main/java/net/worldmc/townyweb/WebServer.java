package net.worldmc.townyweb;

import io.javalin.Javalin;
import net.worldmc.townyweb.routes.Shops;
import net.worldmc.townyweb.routes.Towny;

public class WebServer {
    public static final int MAX_PAGE_SIZE = 20;

    public static void setupRoutes(Javalin app) {
        app.get("/nations", Towny::getNations);
        app.get("/nations/{uuid}", Towny::getNation);
        app.get("/towns", Towny::getTowns);
        app.get("/towns/{uuid}", Towny::getTown);
        app.get("/residents", Towny::getResidents);
        app.get("/residents/{uuid}", Towny::getResident);
        app.get("/shops", Shops::getShops);
    }
}
