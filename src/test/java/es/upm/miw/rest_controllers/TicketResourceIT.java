package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.TicketController;
import es.upm.miw.documents.Ticket;
import es.upm.miw.dtos.*;
import es.upm.miw.repositories.TicketRepository;
import es.upm.miw.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ApiTestConfig
class TicketResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketController ticketController;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void testCreateTicket() {
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(null, new BigDecimal("100.00")
                , BigDecimal.ZERO, BigDecimal.ZERO, Arrays.asList(shoppingDto), "Nota del ticket...");
        byte[] pdf = this.restService.loginAdmin().restBuilder(new RestBuilder<byte[]>()).clazz(byte[].class)
                .path(TicketResource.TICKETS).body(ticketCreationInputDto)
                .post().build();
        assertNotNull(pdf);
    }

    @Test
    void testCreateReserve() {
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), false);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto("666666004",
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, Arrays.asList(shoppingDto),
                "Nota del ticket...");
        byte[] pdf = this.restService.loginAdmin().restBuilder(new RestBuilder<byte[]>()).clazz(byte[].class)
                .path(TicketResource.TICKETS).body(ticketCreationInputDto)
                .post().build();
        assertNotNull(pdf);
    }

    @Test
    void testCreateReserveNonCash() {
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), true);
        ShoppingDto shoppingDto2 =
                new ShoppingDto("1", "", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), false);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto("666666004",
                new BigDecimal("20.00"), BigDecimal.ZERO, BigDecimal.ZERO, Arrays.asList(shoppingDto),
                "Nota del ticket...");
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder().path(TicketResource.TICKETS)
                        .body(ticketCreationInputDto).post().build()
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testFindTicketByUserMobile() {
        //Crear ticket
        String userMobile = "666666005";
        ShoppingDto shoppingDto = new ShoppingDto("1", "", new BigDecimal("100.00"), 1,
                BigDecimal.ZERO, new BigDecimal("100.00"), true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(userMobile,
                new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, Arrays.asList(shoppingDto),
                "Nota del ticket...");
        this.restService.loginAdmin().restBuilder(new RestBuilder<byte[]>()).clazz(byte[].class)
                .path(TicketResource.TICKETS).body(ticketCreationInputDto).post().build();
        //Preparacion de busqueda
        TicketQueryInputDto searchTicketDto = new TicketQueryInputDto();
        searchTicketDto.setUserMobile(userMobile);
        //Busqueda
        TicketQueryOutputDto[] results = this.restService.loginAdmin()
                .restBuilder(new RestBuilder<TicketQueryOutputDto[]>().clazz(TicketQueryOutputDto[].class))
                .path(TicketResource.TICKETS).path(TicketResource.QUERY).body(searchTicketDto).post().build();
        assertEquals(1, results.length);
    }

    @Test
    void testFindTicketByUserMobileTicketsNotFoundException() {
        //Crear ticket
        String userMobile = "666666005";
        //Preparacion de busqueda
        TicketQueryInputDto searchTicketDto = new TicketQueryInputDto();
        searchTicketDto.setUserMobile(userMobile);
        //Generar excepcion
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<TicketQueryOutputDto[]>()
                        .clazz(TicketQueryOutputDto[].class)).log()
                        .path(TicketResource.TICKETS).path(TicketResource.QUERY).body(searchTicketDto).post().build());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testFindTicketByUserMobileNotFoundException() {
        String userMobile = "999111222";
        //Preparacion de busqueda
        TicketQueryInputDto searchTicketDto = new TicketQueryInputDto();
        searchTicketDto.setUserMobile(userMobile);
        //Generar excepcion
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->
                this.restService.loginAdmin().restBuilder(new RestBuilder<TicketQueryOutputDto[]>()
                        .clazz(TicketQueryOutputDto[].class)).log()
                        .path(TicketResource.TICKETS).path(TicketResource.QUERY).body(searchTicketDto).post().build());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testFindTicketByDateRange() {
        LocalDateTime dateStart = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
        LocalDateTime dateEnd = LocalDateTime.of(2019, 1, 10, 0, 0, 0);
        LocalDateTime ticket1Date = LocalDateTime.of(2019, 1, 3, 0, 0, 0);
        LocalDateTime ticket2Date = LocalDateTime.of(2019, 1, 6, 0, 0, 0);
        LocalDateTime ticket3Date = LocalDateTime.of(2019, 1, 25, 0, 0, 0);
        ShoppingDto shoppingDto = new ShoppingDto("1", "", new BigDecimal("100.00"), 1,
                BigDecimal.ZERO, new BigDecimal("100.00"), true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(null,
                new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, Arrays.asList(shoppingDto),
                "Nota del ticket...");
        Ticket ticket1 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        Ticket ticket2 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        Ticket ticket3 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        ticket1.setCreationDate(ticket1Date);
        ticket2.setCreationDate(ticket2Date);
        ticket3.setCreationDate(ticket3Date);
        this.ticketRepository.save(ticket1);
        this.ticketRepository.save(ticket2);
        this.ticketRepository.save(ticket3);
        //Search by Date Range Only
        TicketQueryInputDto searchTicketDto = new TicketQueryInputDto();
        searchTicketDto.setDateStart(dateStart);
        searchTicketDto.setDateEnd(dateEnd);
        //Searching
        TicketQueryOutputDto[] results = this.restService.loginAdmin()
                .restBuilder(new RestBuilder<TicketQueryOutputDto[]>().clazz(TicketQueryOutputDto[].class))
                .path(TicketResource.TICKETS).path(TicketResource.QUERY).body(searchTicketDto).post().build();
        assertEquals(2, results.length);
    }

    @Test
    void testFindTicketByDateRangeAndUserMobile() {
        LocalDateTime dateStart = LocalDateTime.of(2019,1,1,0,0,0);
        LocalDateTime dateEnd = LocalDateTime.of(2019,1,10,0,0,0);
        LocalDateTime ticket1Date = LocalDateTime.of(2019,1,3,0,0,0);
        LocalDateTime ticket2Date = LocalDateTime.of(2019,1,6,0,0,0);
        LocalDateTime ticket3Date = LocalDateTime.of(2019,1,25,0,0,0);
        String userMobile1 = "666666005";
        String userMobile2 = "666666004";
        ShoppingDto shoppingDto = new ShoppingDto("1", "", new BigDecimal("100.00"), 1,
                BigDecimal.ZERO, new BigDecimal("100.00"), true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(userMobile1,
                new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, Arrays.asList(shoppingDto),
                "Nota del ticket...");
        Ticket ticket1 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        Ticket ticket3 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        ticketCreationInputDto.setUserMobile(userMobile2);
        Ticket ticket2 = this.ticketController.createTicketForTests(ticketCreationInputDto);
        ticket1.setCreationDate(ticket1Date);
        ticket2.setCreationDate(ticket2Date);
        ticket3.setCreationDate(ticket3Date);
        this.ticketRepository.save(ticket1);
        this.ticketRepository.save(ticket2);
        this.ticketRepository.save(ticket3);
        //Search by Date Range AND User Mobile
        TicketQueryInputDto searchTicketDto = new TicketQueryInputDto();
        searchTicketDto.setUserMobile(userMobile2);
        searchTicketDto.setDateStart(dateStart);
        searchTicketDto.setDateEnd(dateEnd);
        //Searching
        TicketQueryOutputDto[] results = this.restService.loginAdmin()
                .restBuilder(new RestBuilder<TicketQueryOutputDto[]>().clazz(TicketQueryOutputDto[].class))
                .path(TicketResource.TICKETS).path(TicketResource.QUERY).body(searchTicketDto).post().build();
        assertEquals(1, results.length);
    }
}
