package main.web;

import jakarta.servlet.http.HttpSession;
import main.model.PlayerClass;
import main.service.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ClassController {

    private final PlayerService playerService;

    public ClassController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/class")
    public String getClassSelectionPage() {

        return "classes";
    }

    @PatchMapping("/players/me/class")
    public String selectClass(@RequestParam("selectedClass") PlayerClass playerClass, HttpSession session) {

        UUID userId = (UUID) session.getAttribute("user_id");
        playerService.selectClass(userId, playerClass);

        return "redirect:/lobby";
    }
}
