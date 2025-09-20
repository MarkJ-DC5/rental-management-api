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
import com.rental.rental_management_api.security.auth.AuthenticationService;
import com.rental.rental_management_api.security.auth.payload.RegisterRequest;
import com.rental.rental_management_api.security.user.UserRepository;
import com.rental.rental_management_api.security.user.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyCLIRunner implements CommandLineRunner {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    private final long SEED = 5032001L;
    private final Random random = new Random(SEED);
    private final Faker faker = new Faker(Locale.of("en-PH"),
                                          random);

    @Value("${spring.sql.init.mode}")
    private String sqlInitMode;

    @Override
    public void run(String... args) throws Exception {
        log.debug("SQL Initialization Mode: {}",
                  sqlInitMode);
        if (sqlInitMode.equalsIgnoreCase("always")) {
            log.info("Initializing Data...");
            createDefaultUsers();
            createBuildings();
            createRooms();
            createTenants();
            createTransactions();
        }
    }

    public void createDefaultUsers() {
        log.info("Creating Default Users...");
        List<RegisterRequest> defaultUsers = List.of(RegisterRequest.builder()
                                                                    .username("admin")
                                                                    .password("admin")
                                                                    .role(UserRole.ADMIN)
                                                                    .firstName("Admin")
                                                                    .lastName("User")
                                                                    .build(),
                                                     RegisterRequest.builder()
                                                                    .username("mjdc")
                                                                    .password("mjdc")
                                                                    .role(UserRole.PROP_MNGR)
                                                                    .firstName("Mark")
                                                                    .lastName("Dela Cruz")
                                                                    .build());

        for (RegisterRequest user : defaultUsers) {
            if (userRepository.existsByUsername(user.getUsername())) {
                continue;
            }

            authenticationService.register(user);
        }
    }

    public void createBuildings() {
        log.info("Creating Buildings...");
        List<Building> buildings = List.of(new Building("Behind House",
                                                        "Mario Santiago Rd",
                                                        "Lambakin",
                                                        "Marilao",
                                                        "Bulacan"),
                                           new Building("Cordero",
                                                        "Cordero",
                                                        "Lambakin",
                                                        "Marilao",
                                                        "Bulacan"));

        buildingRepository.saveAll(buildings);
    }

    public void createRooms() {
        log.info("Creating Rooms...");

        int roomsBatchSize = 20;

        HashMap<Integer, Integer> buildingIdsAndRoomCount = new HashMap<>();
        buildingIdsAndRoomCount.put(1,
                                    30);
        buildingIdsAndRoomCount.put(2,
                                    14);

        ArrayList<Room> rooms = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : buildingIdsAndRoomCount.entrySet()) {
            Building building = buildingRepository.findById(entry.getKey())
                                                  .orElseThrow();

            for (int i = 1; i <= entry.getValue(); i++) {
                int rent = random.nextInt(2) == 1 ? 2500 : 3500;
                String roomName = String.format("Room %02d",
                                                i);

                rooms.add(Room.builder()
                              .roomId(null)
                              .roomName(roomName)
                              .roomType(RoomType.RESIDENTIAL)
                              .rent(rent)
                              .building(building)
                              .build());

                if (rooms.size() == roomsBatchSize) {
                    roomRepository.saveAll(rooms);
                    rooms.clear();
                }
            }
        }

        if (!rooms.isEmpty()) {
            roomRepository.saveAll(rooms);
            rooms.clear();
        }
    }

    public void createTenants() {
        log.info("Creating Tenants...");

        int tenantBatchSize = 50;

        int roomPageSize = 10;
        int roomPageNum = 0;

        Page<Room> page;

        ArrayList<Tenant> tenants = new ArrayList<>();
        do {
            page = roomRepository.findAll(PageRequest.of(roomPageNum,
                                                         roomPageSize));

            for (Room room : page.getContent()) {
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
                                               .birthday(22,
                                                         50)
                                               .toInstant()
                                               .atZone(ZoneId.systemDefault())
                                               .toLocalDate();

                    LocalDate moveInDate = LocalDate.now()
                                                    .minusMonths(random.nextInt(12) + 1);

                    // Decide if tenant has moved out
                    LocalDate moveOutDate = null;
                    if (allMovedOut || random.nextInt(4) == 0) { // 25% chance each tenant moved out, OR force all moved out
                        moveOutDate = moveInDate.plusMonths(random.nextInt(12));
                        if (moveOutDate.isAfter(LocalDate.now())) {
                            moveOutDate = null; // still living, future date invalid
                        }
                    }

                    tenants.add(Tenant.builder()
                                      .tenantId(null)
                                      .isPrimary(j == 0)
                                      .firstName(faker.name()
                                                      .firstName())
                                      .middleName(faker.name()
                                                       .lastName())
                                      .lastName(faker.name()
                                                     .lastName())
                                      .birthDate(birthDate)
                                      .gender(gender)
                                      .contactNumber("0956-123-4567")
                                      .dateMovedIn(moveInDate)
                                      .dateMovedOut(moveOutDate)
                                      .room(room)
                                      .build());
                }
            }

            if (tenants.size() >= tenantBatchSize) {
                tenantRepository.saveAll(tenants);
                tenants.clear();
            }

            roomPageNum++;
        } while (page.hasNext());

        if (!tenants.isEmpty()) {
            tenantRepository.saveAll(tenants);
            tenants.clear();
        }
    }

    @Transactional
    public void createTransactions() {
        log.info("Creating Transactions...");

        int transactionsBatchSize = 50;

        int tenantPageSize = 10;
        int tenantPageNum = 0;

        long buildingCount = buildingRepository.count();

        Page<Tenant> page;

        ArrayList<Transaction> transactions = new ArrayList<>();

        for (int buildingId = 1; buildingId < buildingCount; buildingId++) {
            do {
                page = tenantRepository.findByBuildingId(buildingId,
                                                         true,
                                                         PageRequest.of(tenantPageNum,
                                                                        tenantPageSize));
                for (Tenant tenant : page.getContent()) {
                    Room room = tenant.getRoom();

                    int numPayments = 3 + random.nextInt(4);

                    // calculate start month
                    int startMonth = 8 - numPayments + 1; // ensure span ends at August
                    List<YearMonth> months = new ArrayList<>();
                    for (int m = startMonth; m <= 8; m++) {
                        months.add(YearMonth.of(2025,
                                                m));
                    }

                    // shuffle to randomize order
                    Collections.shuffle(months);

                    // create one payment for each month
                    for (YearMonth ym : months) {
                        int day = 1 + random.nextInt(ym.lengthOfMonth());
                        LocalDate transactionDate = ym.atDay(day);
                        LocalDate forMonthOf = ym.atDay(1);

                        TransactionType type = random.nextInt(11) <= 9 ? TransactionType.RENT : TransactionType.OTHERS;
                        Integer amount = type == TransactionType.RENT ? room.getRent() : 400 + random.nextInt(1000);

                        transactions.add(Transaction.builder()
                                                    .transactionType(type)
                                                    .amount(amount)
                                                    .forMonthOf(forMonthOf)
                                                    .transactionDate(transactionDate)
                                                    .notes("Auto-generated payment")
                                                    .room(room)
                                                    .tenant(tenant)
                                                    .build());


                    }

                    if (transactions.size() >= transactionsBatchSize) {
                        transactionRepository.saveAll(transactions);
                        transactions.clear();
                    }
                }

                tenantPageNum++;
            } while (page.hasNext());
        }

        if (!transactions.isEmpty()) {
            transactionRepository.saveAll(transactions);
            transactions.clear();
        }
    }
}