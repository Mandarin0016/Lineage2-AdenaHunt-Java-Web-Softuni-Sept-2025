# Fight Mechanics

## Fight Logic

```java
private FightOutcome executeFight(Player player, Mob mob) {

    double playerHealth = player.getHealth();
    double mobHealth = mob.getHealth();

    //TODO: Get Player Boosters by their class:
    List<ClassProperties.BoosterAbility> boosters = null;
    double attackBooster = boosters.stream()
            .filter(boosterAbility -> boosterAbility.getType() == BoosterType.ATTACK_BOOSTER)
            .map(ClassProperties.BoosterAbility::getValue)
            .findFirst()
            .orElse(0.00);
    double defenseBooster = boosters.stream()
            .filter(boosterAbility -> boosterAbility.getType() == BoosterType.ATTACK_BOOSTER)
            .map(ClassProperties.BoosterAbility::getValue)
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
```

## Reward Calculation
After winning, the player gains XP and Adena (gold).

```
1. if player has xpBooster abilit:
    rewardXp = mobXp + (mobXp * xpBoosterValue)
otherwise -> rewardXp = mobXp 

2. if player has adenaBooster ability: 
    rewardAdena = mobAdena + (mobAdena * adenaBoosterValue)
otherwise -> rewardAdena = mobAdena
```