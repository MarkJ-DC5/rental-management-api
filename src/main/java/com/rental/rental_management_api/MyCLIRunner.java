package com.rental.rental_management_api;

import com.github.javafaker.Faker;
import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@Slf4j
public class MyCLIRunner implements CommandLineRunner {

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    TenantRepository tenantRepository;

    @Override
    public void run(String... args) throws Exception {
        buildingRepository.saveAll(List.of(
                new Building("Behind House", "Mario Santiago Rd", "Lambakin", "Marilao", "Bulacan"),
                new Building("Cordero", "Cordero", "Lambakin", "Marilao", "Bulacan")
        ));

        HashMap<String, Integer> buildingIdsAndCount = new HashMap<>();
        buildingIdsAndCount.put("Behind House", 30);
        buildingIdsAndCount.put("Cordero", 14);

        Faker faker = new Faker(new Locale("en-PH"));
        for (Map.Entry<String, Integer> entry : buildingIdsAndCount.entrySet()) {
            Building building = buildingRepository.findById(entry.getKey()).get();

            for (int i = 1; i <= entry.getValue(); i++) {
                int rent = new Random().nextInt(2) == 1 ? 2500 : 3500;
                Room room = new Room(null, "Room " + i, Room.RoomType.Residential, rent, building);
                roomRepository.save(room);

                int tenantsToCreate;
                int chance = new Random().nextInt(11);
                if (chance > 9) {
                    tenantsToCreate = 3;
                } else if (chance > 7) {
                    tenantsToCreate = 2;
                } else {
                    tenantsToCreate = 1;
                }

                for (int j = 0; j < tenantsToCreate; j++) {
                    Tenant.Gender gender = new Random().nextInt(2) == 1 ? Tenant.Gender.F : Tenant.Gender.M;
                    Tenant tenant = new Tenant(null, j == 0, faker.name().firstName(), faker.name().lastName(), faker.name().lastName(), faker.date().birthday(22, 50).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), gender, faker.phoneNumber().cellPhone(), LocalDate.now(), null, room);
                    tenantRepository.save(tenant);
                }
            }
        }
    }
}

//        Optional<Building> building = buildingRepository.findById("Behind House");
//
//        if (building.isEmpty()){
//            log.debug("Failed to retrieve details");
//        } else {
//            log.debug("Retrieved details: " + building.get());
//        }