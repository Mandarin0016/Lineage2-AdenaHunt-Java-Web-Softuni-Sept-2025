package main.web;

import jakarta.servlet.http.HttpSession;
import main.service.PartyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/party")
public class PartyController {

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping()
    public String invitePlayer(@RequestParam("receiverId") UUID receiverId, HttpSession session) {

        UUID senderId = (UUID) session.getAttribute("user_id");
        partyService.inviteToParty(senderId, receiverId);

        return "redirect:/lobby";
    }

    @DeleteMapping("/{id}")
    public String dismissParty(@PathVariable("id") UUID partyId) {

        partyService.dismissParty(partyId);

        return "redirect:/lobby";
    }
}
