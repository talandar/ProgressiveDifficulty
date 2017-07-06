package derpatiel.progressivediff.util;

import derpatiel.progressivediff.MultiplePlayerCombineType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;

import java.util.List;
import java.util.function.Function;

public class PlayerAreaStatAccumulator {

    public static int getStatForPlayersInArea(MultiplePlayerCombineType type, EntityLiving entity, int radius, Function<EntityPlayerMP,Integer> function){
        List<EntityPlayerMP> playersInRange = entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayerMP.class, entity.getEntityBoundingBox().grow(radius));
        int counter = 0;
        if(playersInRange.size()>0) {
            switch (type) {
                case AVERAGE:
                    int avgSum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int count = function.apply(player);
                        avgSum += count;
                    }
                    counter = avgSum / playersInRange.size();
                    break;
                case CLOSEST:
                    EntityPlayerMP closestPlayer = (EntityPlayerMP) entity.getEntityWorld().getClosestPlayerToEntity(entity, 128.0d);
                    counter = function.apply(closestPlayer);
                    break;
                case MAX:
                    int max = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int count = function.apply(player);
                        if (count > max) {
                            max = count;
                        }
                    }
                    counter = max;
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (EntityPlayerMP player : playersInRange) {
                        int count = function.apply(player);
                        if (count < min) {
                            min = count;
                        }
                    }
                    counter = min;
                    break;
                case SUM:
                    int sum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int count = function.apply(player);
                        sum += count;
                    }
                    counter = sum;
                    break;
            }
        }
        return counter;
    }

    public static int getStatForPlayersInArea(MultiplePlayerCombineType type, StatBase stat, EntityLiving entity, int radius) {
        return getStatForPlayersInArea(type,entity,radius, (player)->player.getStatFile().readStat(stat));
    }
}
