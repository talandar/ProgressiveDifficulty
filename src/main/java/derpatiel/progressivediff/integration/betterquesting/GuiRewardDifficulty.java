package derpatiel.progressivediff.integration.betterquesting;

import betterquesting.api.client.gui.GuiElement;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiRewardDifficulty extends GuiElement implements IGuiEmbedded{

    private RewardDifficulty reward;
    private Minecraft mc;

    private int posX = 0;
    private int posY = 0;
    private int sizeX = 0;
    private int sizeY = 0;

    public GuiRewardDifficulty(RewardDifficulty reward, int posX, int posY, int sizeX, int sizeY){
        this.mc = Minecraft.getMinecraft();
        this.reward = reward;
        this.posX = posX;
        this.posY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }


    @Override
    public void drawBackground(int i, int i1, float v) {
        if(reward.visible) {
            String label = I18n.format("progressivedifficulty.integration.betterquest.reward.difficulty");
            String txt2 = "" + TextFormatting.BOLD;

            if (reward.difficulty >= 0) {
                txt2 += TextFormatting.RED + "+" + Math.abs(reward.difficulty);
            } else {
                txt2 += TextFormatting.GREEN + "-" + Math.abs(reward.difficulty);
            }

            txt2 += " "+label;

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5F, 1.5F, 1F);
            mc.fontRenderer.drawString(txt2, (int) ((posX + sizeX / 2 - mc.fontRenderer.getStringWidth(txt2) / 1.5F) / 1.5F), (int) ((posY + sizeY / 2) / 1.5F), getTextColor(), false);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawForeground(int i, int i1, float v) {

    }

    @Override
    public void onMouseClick(int i, int i1, int i2) {

    }

    @Override
    public void onMouseScroll(int i, int i1, int i2) {

    }

    @Override
    public void onKeyTyped(char c, int i) {

    }
}
