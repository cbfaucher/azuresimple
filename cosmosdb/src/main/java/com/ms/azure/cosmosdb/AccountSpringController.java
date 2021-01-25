package com.ms.azure.cosmosdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@RestController
@Profile("spring")  //will only spin up if 'spring' context is specified
public class AccountSpringController {

    @Autowired
    private Consumer<AccountUpdate> addHandler;

    @Autowired
    private Function<String, List<AccountUpdate>> listHandler;

    public AccountSpringController() {
        System.out.println("--> Account Spring Controller created [profiles has 'spring']");
    }

    @RequestMapping(path = "api/accountUpdate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void add(@RequestBody final AccountUpdate update) {
        addHandler.accept(update);
    }

    @RequestMapping(path = "api/list/{accountNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccountUpdate> list(@PathVariable("accountNumber") final String accountNumber) {
        return listHandler.apply(accountNumber);
    }
}
