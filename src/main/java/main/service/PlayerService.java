package main.service;

import main.exception.DomainException;
import main.model.BoosterType;
import main.model.Party;
import main.model.Player;
import main.model.PlayerClass;
import main.property.ClassProperties;
import main.property.ClassProperties.ClassDetails;
import main.repository.PlayerRepository;
import main.web.dto.LoginRequest;
import main.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClassProperties classProperties;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, ClassProperties classProperties) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.classProperties = classProperties;
    }

    public void register(RegisterRequest registerRequest) {

        Player player = Player.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .nickname(registerRequest.getNickname())
                .level(1)
                .xp(100)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        playerRepository.save(player);
    }

    public Player login(LoginRequest loginRequest) {

        Player player = playerRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new DomainException("Username or password incorrect!"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), player.getPassword())) {
            throw new DomainException("Username or password incorrect!");
        }

        return player;
    }

    public void selectClass(UUID userId, PlayerClass playerClass) {

        Player player = getById(userId);
        ClassDetails classDetails = classProperties.getDetailsByPlayerClass(playerClass);

        player.setPlayerClass(playerClass);
        player.setBannerImg(classDetails.getBannerImg());

        player.setAttack(classDetails.getAttackFactor());
        player.setHealth(classDetails.getHealthFactor());
        player.setDefense(classDetails.getDefenseFactor());

        player.setUpdatedOn(LocalDateTime.now());

        update(player);
    }

    public void update(Player player) {

        playerRepository.save(player);
    }

    public Player getById(UUID playerId) {

        return playerRepository.findById(playerId).orElseThrow(() -> new DomainException("Player not found"));
    }

    public List<ClassProperties.Booster> getBoosters(PlayerClass playerClass) {

        return classProperties.getDetailsByPlayerClass(playerClass).getBoosters();
    }

    public List<Player> findAllByParty(Party party) {

        if (party == null) {
            return List.of();
        }

        return playerRepository.findAllByParty_Id(party.getId());
    }

    public List<Player> findAllFreePlayersForInvite() {

        List<Player> allPlayers = playerRepository.findAll();

        return allPlayers.stream()
                .filter(p -> p.getPlayerClass() != null && p.getParty() == null)
                .collect(Collectors.toList());
    }

    public void rewardPlayer(Player player, int xpReward, int adenaReward) {

        List<ClassProperties.Booster> boosters = getBoosters(player.getPlayerClass());
        double xpBooster = boosters.stream()
                .filter(boosterAbility -> boosterAbility.getType() == BoosterType.XP_BOOSTER)
                .map(ClassProperties.Booster::getValue)
                .findFirst()
                .orElse(0.00);
        double adenaBooster = boosters.stream()
                .filter(boosterAbility -> boosterAbility.getType() == BoosterType.ADENA_BOOSTER)
                .map(ClassProperties.Booster::getValue)
                .findFirst()
                .orElse(0.00);

        double finalXp = xpReward + (xpReward * xpBooster);
        double finalAdena = adenaReward + (adenaReward * adenaBooster);

        player.setXp(player.getXp() + finalXp);
        player.setAdena((int) (player.getAdena() + finalAdena));

        levelUp(player);
        playerRepository.save(player);
    }

    private void levelUp(Player player) {

        int newLevel = (int) player.getXp() / 100;

        if (newLevel > player.getLevel()) {

            player.setLevel(newLevel);
            ClassDetails classDetails = classProperties.getDetailsByPlayerClass(player.getPlayerClass());

            player.setHealth(classDetails.getHealthFactor() * newLevel);
            player.setAttack(classDetails.getAttackFactor() * newLevel);
            player.setDefense(classDetails.getDefenseFactor() * newLevel);

            player.setUpdatedOn(LocalDateTime.now());
        }
    }
}
