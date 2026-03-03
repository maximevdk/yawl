package com.it;

import com.yawl.annotations.DeleteMapping;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.PutMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.WebController;
import com.yawl.http.model.HttpStatus;

import java.util.List;

@WebController(path = "ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping(path = "/{id}")
    public Pong ping(@PathParam(name = "id") String id) {
        return pingService.get(id);
    }

    @GetMapping(path = "/by-ids")
    public List<Pong> pingsByIds(@QueryParam(name = "ids") List<String> ids) {
        return pingService.find(ids);
    }

    @GetMapping
    public Pong pingByQuery(@QueryParam(name = "id") String id) {
        return pingService.get(id);
    }

    @PostMapping(status = HttpStatus.ACCEPTED)
    public Pong setPing(@QueryParam(name = "name") String name) {
        return pingService.set(name);
    }

    @PutMapping(path = "{id}", status = HttpStatus.ACCEPTED)
    public void updatePing(@PathParam(name = "id") String id, @QueryParam(name = "name") String name) {
        pingService.update(id, name);
    }

    @DeleteMapping(path = "{id}", status = HttpStatus.NO_CONTENT)
    public void deletePing(@PathParam(name = "id") String id) {
        pingService.delete(id);
    }
}
