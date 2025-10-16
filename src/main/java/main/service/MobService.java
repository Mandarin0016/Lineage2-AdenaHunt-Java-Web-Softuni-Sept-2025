package main.service;

import lombok.extern.slf4j.Slf4j;
import main.exception.DomainException;
import main.model.*;
import main.property.ClassProperties;
import main.property.MobProperties.MobDetails;
import main.repository.MobRepository;
import main.web.dto.FightResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class MobService {

    private final MobRepository mobRepository;
    private final PlayerService playerService;

    public MobService(MobRepository mobRepository, PlayerService playerService) {
        this.mobRepository = mobRepository;
        this.playerService = playerService;
    }

    public Mob create(MobDetails mobDetails) {

        Random random = new Random();
        // 10 - 20
        int levelStart = mobDetails.getLevelRange()[0];
        int levelEnd = mobDetails.getLevelRange()[1];
        int level = random.nextInt(levelStart, levelEnd + 1);

        int adenaDropStart = mobDetails.getAdenaDrop()[0];
        int adenaDropEnd = mobDetails.getAdenaDrop()[1];
        int adena = random.nextInt(adenaDropStart, adenaDropEnd + 1);

        int xpDropStart = mobDetails.getXpDrop()[0];
        int xpDropEnd = mobDetails.getXpDrop()[1];
        int xp = random.nextInt(xpDropStart, xpDropEnd + 1);

        int randomMobTypeIndex = random.nextInt(0, MobType.values().length);
        MobType type = MobType.values()[randomMobTypeIndex];

        double statMultiplier = 1.00;
        int dropMultiplier = 1;

        if (type == MobType.BLUE_CHAMPION) {
            statMultiplier = 1.05;
            dropMultiplier = 2;
        } else if (type == MobType.RED_CHAMPION) {
            statMultiplier = 1.10;
            dropMultiplier = 3;
        }

        Mob mob = Mob.builder()
                .name(mobDetails.getName())
                .level(level)
                .spawnArea(mobDetails.getSpawnArea())
                .description(mobDetails.getDescription())
                .health(mobDetails.getHealthFactor() * level * statMultiplier)
                .attack(mobDetails.getAttackFactor() * level * statMultiplier)
                .defense(mobDetails.getDefenseFactor() * level * statMultiplier)
                .adenaDrop(adena * dropMultiplier)
                .xpDrop(xp * dropMultiplier)
                .alive(true)
                .type(type)
                .imageUrl(mobDetails.getImage())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return mobRepository.save(mob);
    }

    public List<Mob> findTop3Mobs() {

        List<Mob> allMobs = mobRepository.findAll();
        allMobs.sort(Comparator.comparing(Mob::getCreatedOn).reversed());

        return allMobs.stream().limit(3).toList();
    }

    public List<Mob> getAll() {

        return mobRepository.findAll();
    }

    public Mob getById(UUID mobId) {

        return mobRepository.findById(mobId).orElseThrow(() -> new DomainException("Mob not found."));
    }

    public FightResult attack(UUID attackerId, UUID mobId) {

        Player player = playerService.getById(attackerId);
        Mob mob = getById(mobId);

        FightOutcome outcome = executeFight(player, mob);

        if (outcome == FightOutcome.MOB_WIN) {

            return FightResult.builder()
                    .outcome(outcome)
                    .mobName(mob.getName())
                    .xpEarned(0)
                    .adenaEarned(0)
                    .build();
        }

        int xpReward = mob.getXpDrop();
        int adenaReward = mob.getAdenaDrop();

        if (player.getParty() != null) {
            // трябва да наградя всички в партито
            List<Player> members = playerService.findAllByParty(player.getParty());
            xpReward = xpReward / members.size();
            adenaReward = adenaReward / members.size();
            int newXpReward = xpReward;
            int newAdenaReward = adenaReward;
            members.forEach(member -> playerService.rewardPlayer(member, newXpReward, newAdenaReward));
        } else {
            // само аз получавам награда

            playerService.rewardPlayer(player, xpReward, adenaReward);
        }

        mob.setAlive(false);
        mobRepository.save(mob);

        return FightResult.builder()
                .outcome(outcome)
                .mobName(mob.getName())
                .adenaEarned(adenaReward)
                .xpEarned(xpReward)
                .build();
    }

    private FightOutcome executeFight(Player player, Mob mob) {

        double playerHealth = player.getHealth();
        double mobHealth = mob.getHealth();

        List<ClassProperties.Booster> boosters = playerService.getBoosters(player.getPlayerClass());
        double attackBooster = boosters.stream()
                .filter(boosterAbility -> boosterAbility.getType() == BoosterType.ATTACK_BOOSTER)
                .map(ClassProperties.Booster::getValue)
                .findFirst()
                .orElse(0.00);
        double defenseBooster = boosters.stream()
                .filter(boosterAbility -> boosterAbility.getType() == BoosterType.ATTACK_BOOSTER)
                .map(ClassProperties.Booster::getValue)
                .findFirst()
                .orElse(0.00);

        while (playerHealth > 0 && mobHealth > 0) {

            // First: Player attacks mob
            double playerAttackWithBooster = player.getAttack() + (player.getAttack() * attackBooster);
            double damageToMob = Math.floor(Math.max(1, playerAttackWithBooster - (mob.getDefense() * 0.5)));
            mobHealth = Math.max(0, mobHealth - damageToMob);
            log.info("{} deals {} damage to {}. Mob HP: {}",
                    player.getNickname(), (int) damageToMob, mob.getName(), (int) mobHealth);

            // Did the mob die after player's attack?
            if (mobHealth <= 0) {
                log.info("{} killed {} and left with {} HP.", player.getNickname(), mob.getName(), (int) playerHealth);
                return FightOutcome.PLAYER_WIN;
            }

            // Second: Mob attacks player
            double playerDefenseWithBooster = player.getDefense() + (player.getDefense() * defenseBooster);
            double damageToPlayer = Math.floor(Math.max(1, mob.getAttack() - (playerDefenseWithBooster * 0.5)));
            playerHealth = Math.max(0, playerHealth - damageToPlayer);

            // Did the player die after mob's attack?
            if (playerHealth <= 0) {
                log.info("{} is defeated. {} wins with {} HP left.",
                        player.getNickname(), mob.getName(), (int) mobHealth);
                return FightOutcome.MOB_WIN;
            }
        }

        return playerHealth > 0 ? FightOutcome.PLAYER_WIN : FightOutcome.MOB_WIN;
    }
}
