package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class DepthControl extends DifficultyControl {

    private double addedPerBlock;

    public DepthControl(double addedPerBlock){
        this.addedPerBlock = addedPerBlock;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details, int currentDifficulty) {
        int depth = 64-(int)details.entity.getPosition().getY();
        if(depth<=0){
            return currentDifficulty;
        }else{
            return currentDifficulty+(int)(depth * addedPerBlock);
        }
    }

    @Override
    public int getSortWeight() {
        return 1;
    }
}
