package derpatiel.progressivediff.util;

import derpatiel.progressivediff.ProgressiveDifficulty;
import org.apache.logging.log4j.Level;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class LOG {

	public static void log(Level logLevel, Object object){
		FMLLog.log(ProgressiveDifficulty.MODID, logLevel, String.valueOf(object));
	}
	
	public static void all(Object obj){ log(Level.ALL,obj);	}
	public static void debug(Object obj){ log(Level.DEBUG,obj);}
	public static void error(Object obj){ log(Level.ERROR,obj);}
	public static void fatal(Object obj){ log(Level.FATAL,obj);}
	public static void info(Object obj){ log(Level.INFO,obj);}
	public static void off(Object obj){ log(Level.OFF,obj);}
	public static void trace(Object obj){ log(Level.TRACE,obj);}
	public static void warn(Object obj){ log(Level.WARN,obj);}

	public static void infoLogEvent(String eventName, World worldIn, BlockPos pos) {
		LOG.info("EVENT: "+eventName+", happened on the "+(worldIn.isRemote ? "client" : "server")+ ", a tile entity is "+(worldIn.getTileEntity(pos)==null ? "NOT " : "" )+ "present.");	
	}
	
}
