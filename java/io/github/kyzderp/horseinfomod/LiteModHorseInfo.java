package io.github.kyzderp.horseinfomod;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker.ReturnValue;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;

@ExposableOptions(strategy = ConfigStrategy.Versioned, filename="horseinfo.json")
public class LiteModHorseInfo implements Tickable
{
	private EntityHorse prevHorse;
	private double speed;
	private double jumpHeight;

	public LiteModHorseInfo() {}

	@Override
	public String getName() {return "HorseInfo";}

	@Override
	public String getVersion() {return "1.0.0";}

	@Override
	public void init(File configPath){}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) 
	{
		if (inGame && Minecraft.isGuiEnabled()
				&& minecraft.thePlayer.ridingEntity != null
				&& minecraft.thePlayer.ridingEntity instanceof EntityHorse
				&& minecraft.currentScreen instanceof GuiScreenHorseInventory)
		{

			EntityHorse horse = (EntityHorse) minecraft.thePlayer.ridingEntity;
			DecimalFormat df = new DecimalFormat("#.##");

			if (this.prevHorse == null || !this.prevHorse.equals(horse))
			{
				this.prevHorse = horse;
				System.out.println("RECALCULATE");
				double yVelocity = horse.getHorseJumpStrength(); //horses's jump strength attribute
				double jump = 0;
				while (yVelocity > 0)
				{
					jump += yVelocity;
					yVelocity -= 0.08;
					yVelocity *= 0.98;
				}
				this.jumpHeight = jump;
				this.speed = 43 * horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
			}

			FontRenderer fontRender = minecraft.fontRendererObj;
			int height = Minecraft.getMinecraft().displayHeight;
			fontRender.drawStringWithShadow("Max speed: ~" + df.format(this.speed) + " m/s", 1, height/2 - 21, 0x1FE700);
			fontRender.drawStringWithShadow("Jump height: ~" + df.format(this.jumpHeight) + " m", 1, height/2 - 11, 0x1FE700);
		}
	}

	//	Movement speed is randomly determined for horses, ranging from 0.1125 
	//	to 0.3375 in internal units but tending toward the average of 0.225.
	// = 14.57 m/s -> 43.17
	//	For donkeys and mules speed is always 0.175, 
	// = 7.525 m/s -> 43
	//	and for skeletal and zombie horses it is always 0.2.
	//	For comparison, the player's normal walking speed is 0.1.
	// = 4.3 m/s -> 43
	//	Jump strength for horses, skeletal horses, and zombie horses is random, ranging from 0.4 
	//	to 1.0 but tending towards the average 
	//	0.7. For donkeys (and mules), jump strength is always 0.5.
	//	For comparison, a player's jump strength is 0.42, enough to jump 1 block high. 
	//	0.5 is enough to jump 1.5 blocks, 
	//	while 1.0 is enough to jump 5.5 blocks.
}

