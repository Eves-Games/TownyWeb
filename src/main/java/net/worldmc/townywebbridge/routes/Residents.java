package net.worldmc.townywebbridge.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townywebbridge.adapters.ResidentTypeAdapter;

import java.util.List;
import java.util.UUID;

public class Residents {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Resident.class, new ResidentTypeAdapter())
            .create();

    private Resident getResidentByUUID(String uuidParam) {
        if (uuidParam.isEmpty()) {
            throw new HttpResponseException(400, "Required path parameter 'uuid' is missing.");
        }

        UUID uuid = UUID.fromString(uuidParam);
        Resident resident = TownyAPI.getInstance().getResident(uuid);

        if (resident == null) {
            throw new HttpResponseException(404, "Resident not found");
        }

        return resident;
    }

    public void getResidentFriends(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Resident resident = getResidentByUUID(uuidParam);

        String json = gson.toJson(resident.getFriends());
        ctx.json(json);
    }

    public void getResident(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Resident resident = getResidentByUUID(uuidParam);

        String json = gson.toJson(resident);
        ctx.json(json);
    };

    public void getResidents(Context ctx) {
        List<Resident> residents = TownyAPI.getInstance().getResidents();

        String json = gson.toJson(residents);
        ctx.json(json);
    }
}
