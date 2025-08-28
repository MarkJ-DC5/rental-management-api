package com.rental.rental_management_api;

import com.github.javafaker.Faker;
import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.entity.Transaction;
import com.rental.rental_management_api.model.RoomType;
import com.rental.rental_management_api.model.TenantGender;
import com.rental.rental_management_api.model.TransactionType;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import com.rental.rental_management_api.repository.TransactionRepository;
import com.rental.rental_management_api.service.BuildingService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
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
    @Autowired
    TransactionRepository transactionRepository;

    @Value("${spring.sql.init.mode}")
    private String sqlInitMode;

    @Override
    public void run(String... args) throws Exception {
        log.debug("SQL Initialization Mode: " + sqlInitMode);
        if (sqlInitMode.equalsIgnoreCase("always")) {
            initializeData();
        }
    }

    public void initializeData() {
        final long seed = 05032001L;

        log.debug("Initializing Data...");
        buildingRepository.saveAll(List.of(
                new Building("Behind House", "Mario Santiago Rd", "Lambakin", "Marilao", "Bulacan"),
                new Building("Cordero", "Cordero", "Lambakin", "Marilao", "Bulacan")
        ));

        HashMap<Integer, Integer> buildingIdsAndCount = new HashMap<>();
        buildingIdsAndCount.put(1, 30);
        buildingIdsAndCount.put(2, 14);

        Random random = new Random(seed);
        Faker faker = new Faker(new Locale("en-PH"), random);
        for (Map.Entry<Integer, Integer> entry : buildingIdsAndCount.entrySet()) {
            Building building = buildingRepository.findById(entry.getKey()).get();

            for (int i = 1; i <= entry.getValue(); i++) {
                int rent = random.nextInt(2) == 1 ? 2500 : 3500;
                String roomName = String.format("Room %02d", i);
                Room room = new Room(null, roomName, RoomType.Residential, rent, building);
                roomRepository.save(room);

                // Decide how many tenants this room had historically
                int tenantsToCreate;
                int chance = random.nextInt(11);
                if (chance > 9) {
                    tenantsToCreate = 3;
                } else if (chance > 7) {
                    tenantsToCreate = 2;
                } else {
                    tenantsToCreate = 1;
                }

                // Randomly decide if this room should be completely vacant now
                boolean allMovedOut = random.nextInt(5) == 0; // ~20% chance

                for (int j = 0; j < tenantsToCreate; j++) {
                    TenantGender gender = random.nextInt(2) == 1 ? TenantGender.F : TenantGender.M;

                    // Generate tenant basic info
                    LocalDate birthDate = faker.date()
                            .birthday(22, 50)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate moveInDate = LocalDate.now().minusMonths(random.nextInt(12) + 1);

                    // Decide if tenant has moved out
                    LocalDate moveOutDate = null;
                    if (allMovedOut ||
                            random.nextInt(4) == 0) { // 25% chance each tenant moved out, OR force all moved out
                        moveOutDate = moveInDate.plusMonths(random.nextInt(12));
                        if (moveOutDate.isAfter(LocalDate.now())) {
                            moveOutDate = null; // still living, future date invalid
                        }
                    }

                    Tenant tenant = new Tenant(
                            null,
                            j == 0, // is primary tenant
                            faker.name().firstName(),
                            faker.name().lastName(),
                            faker.name().lastName(),
                            birthDate,
                            gender,
                            "0956-123-4567",
                            moveInDate,
                            moveOutDate,
                            room
                    );
                    tenantRepository.save(tenant);

                    if (j == 0){
                        int numPayments = 3 + random.nextInt(4);

                        // calculate start month
                        int startMonth = 8 - numPayments + 1; // ensure span ends at August
                        List<YearMonth> months = new ArrayList<>();
                        for (int m = startMonth; m <= 8; m++) {
                            months.add(YearMonth.of(2025, m));
                        }

                        // shuffle to randomize order
                        Collections.shuffle(months);

                        // create one payment for each month
                        for (YearMonth ym : months) {
                            int day = 1 + random.nextInt(ym.lengthOfMonth());
                            LocalDate transactionDate = ym.atDay(day);
                            LocalDate forMonthOf = ym.atDay(1);

                            TransactionType type = random.nextInt(11) <= 9 ? TransactionType.Rent :
                                    TransactionType.Others;
                            Integer ammount = type == TransactionType.Rent ? room.getRent() :
                                    400 + random.nextInt(1000);

                            Transaction transaction = Transaction.builder()
                                    .transactionType(type)
                                    .amount(ammount)
                                    .forMonthOf(forMonthOf)
                                    .transactionDate(transactionDate)
                                    .notes("Auto-generated payment")
                                    .room(room)
                                    .tenant(tenant)
                                    .build();

                            transactionRepository.save(transaction);
                        }
                    }
                }
            }
        }
    }
}