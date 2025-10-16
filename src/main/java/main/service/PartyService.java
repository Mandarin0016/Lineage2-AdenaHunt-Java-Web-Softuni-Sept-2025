package main.service;

import main.exception.DomainException;
import main.model.Party;
import main.model.Player;
import main.repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PartyService {

    private final PartyRepository partyRepository;
    private final PlayerService playerService;

    public PartyService(PartyRepository partyRepository, PlayerService playerService) {
        this.partyRepository = partyRepository;
        this.playerService = playerService;
    }

    public void inviteToParty(UUID senderId, UUID receiverId) {

        // Gosho
        Player sender = playerService.getById(senderId);
        // Pesho
        Player receiver = playerService.getById(receiverId);

        if (receiver.getParty() != null) {
            throw new DomainException("Receiver is already in a party!");
        }

        Party party = getParty(sender);

        List<Player> allMembersTillNow = playerService.findAllByParty(party);
        if (allMembersTillNow.size() == 3) {
            throw new DomainException("Party is full");
        }

        receiver.setParty(party);
        playerService.update(receiver);
    }

    private Party getParty(Player player) {

        if (player.getParty() != null) {
            return player.getParty();
        }

        Party party = Party.builder()
                .leader(player)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        partyRepository.save(party);

        player.setParty(party);
        playerService.update(player);

        return party;
    }

    public void dismissParty(UUID partyId) {

        Party party = partyRepository.findById(partyId).orElseThrow(() -> new DomainException("Party not found."));

        party.setUpdatedOn(LocalDateTime.now());

        List<Player> members = playerService.findAllByParty(party);

        members.forEach(member -> {
            member.setParty(null);
            playerService.update(member);
        });
        partyRepository.save(party);
    }
}
