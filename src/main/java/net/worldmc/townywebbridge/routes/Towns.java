package net.worldmc.townywebbridge.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.InvalidNameException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import net.worldmc.townywebbridge.adapters.ResidentTypeAdapter;
import net.worldmc.townywebbridge.adapters.TownTypeAdapter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Towns {
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Town.class, new TownTypeAdapter())
            .registerTypeAdapter(Resident.class, new ResidentTypeAdapter())
            .create();

    private Town getTownByUUID(String uuidParam) {
        if (uuidParam.isEmpty()) throw new HttpResponseException(400, "Required path parameter 'uuid' is missing.");

        UUID uuid = UUID.fromString(uuidParam);
        Town town = TownyAPI.getInstance().getTown(uuid);

        if (town == null) throw new HttpResponseException(404, "Town not found");

        return town;
    }

    public void getTowns(Context ctx) {
        List<Town> towns = TownyAPI.getInstance().getTowns();

        String json = gson.toJson(towns);
        ctx.json(json);
    }

    public void getTown(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Town town = getTownByUUID(uuidParam);

        String json = gson.toJson(town);
        ctx.json(json);
    }

    public void getTownResidents(Context ctx) {
        String uuidParam = ctx.pathParam("uuid");
        Town town = getTownByUUID(uuidParam);

        String json = gson.toJson(town.getResidents());
        ctx.json(json);
    }

    static class TownCreationRequest {
        @SerializedName("name")
        String name;

        @SerializedName("board")
        String board;

        @SerializedName("mayorUUID")
        UUID mayorUUID;
    }

    public void createTown(Context ctx) {
        TownCreationRequest request = gson.fromJson(ctx.body(), TownCreationRequest.class);
        String name = request.name;
        String board = request.board;
        UUID mayorUUID = request.mayorUUID;

        Resident resident = TownyAPI.getInstance().getResident(mayorUUID);

        if (resident == null) throw new HttpResponseException(404, "Resident not found");
        if (TownyAPI.getInstance().getResidentTownOrNull(resident) != null) throw new HttpResponseException(400, "Resident is already in a town");

        try {
            TownyUniverse.getInstance().newTown(name);
            Town newTown = TownyAPI.getInstance().getTown(name);

            assert newTown != null;

            try {
                resident.setTown(newTown);
            } catch (AlreadyRegisteredException e) {
                throw new HttpResponseException(400, "Resident is already registered in the town");
            }

            newTown.setRegistered(Instant.now().toEpochMilli());
            newTown.setBoard(board);
            newTown.setMayor(resident);

            newTown.save();

            String json = gson.toJson(newTown);
            ctx.json(json);
        } catch (AlreadyRegisteredException e) {
            throw new HttpResponseException(409, "A town with this name already exists");
        } catch (InvalidNameException e) {
            throw new HttpResponseException(400, "Invalid town name: " + e.getMessage());
        } catch (Exception e) {
            throw new HttpResponseException(500, "An error occurred while creating the town: " + e.getMessage());
        }
    }
}