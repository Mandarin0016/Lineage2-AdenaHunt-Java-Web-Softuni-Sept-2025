package main.web;

import jakarta.servlet.http.HttpSession;
import main.service.MobService;
import main.web.dto.FightResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/mobs")
public class MobController {

    private final MobService mobService;

    public MobController(MobService mobService) {
        this.mobService = mobService;
    }

    @PatchMapping("/{id}")
    public String attackMob(@PathVariable("id") UUID mobId, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID attackerId = (UUID) session.getAttribute("user_id");

        FightResult fightResult = mobService.attack(attackerId, mobId);
        redirectAttributes.addFlashAttribute("fightResult", fightResult);

        return "redirect:/farm-zone";
    }
}
