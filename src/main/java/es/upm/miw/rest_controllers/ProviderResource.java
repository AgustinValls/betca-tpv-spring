package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.ProviderController;
import es.upm.miw.dtos.ProviderDto;
import es.upm.miw.dtos.ProviderMinimunDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(ProviderResource.PROVIDERS)
public class ProviderResource {

    public static final String PROVIDERS = "/providers";
    public static final String ACTIVES = "/actives";
    public static final String ID = "/{id}";

    @Autowired
    private ProviderController providerController;

    @GetMapping
    public List<ProviderMinimunDto> readAll() {
        return this.providerController.readAll();
    }

    @GetMapping(value = ID)
    public ProviderDto read(@PathVariable String id) {
        return this.providerController.read(id);
    }

    @GetMapping(value = ACTIVES)
    public List<ProviderMinimunDto> readAllActives() {
        return this.providerController.readAllActives();
    }

    @PostMapping
    public ProviderDto create(@Valid @RequestBody ProviderDto providerDto) {
        return this.providerController.create(providerDto);
    }

    @PutMapping(value = ID)
    public ProviderDto update(@PathVariable String id, @Valid @RequestBody ProviderDto providerDto) {
        return this.providerController.update(id, providerDto);
    }
}
