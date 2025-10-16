package main.web;

import jakarta.servlet.http.HttpSession;
import main.model.Mob;
import main.model.Player;
import main.service.MobService;
import main.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/farm-zone")
public class FarmZoneController {

    private final PlayerService playerService;
    private final MobService mobService;

    public FarmZoneController(PlayerService playerService, MobService mobService) {
        this.playerService = playerService;
        this.mobService = mobService;
    }

    @GetMapping
    public ModelAndView getFarmZone(HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("farm-zone");

        UUID userId = (UUID) session.getAttribute("user_id");
        Player player = playerService.getById(userId);
        List<Mob> allMobs = mobService.getAll();

        modelAndView.addObject("playerAdena", player.getAdena());
        modelAndView.addObject("mobs", allMobs);

        return modelAndView;
    }
}
